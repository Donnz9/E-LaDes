package com.ELayang.Desa.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Register.ResponRegister1;
import com.ELayang.Desa.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class register1 extends AppCompatActivity {
    ImageButton kembali;
    Button lanjut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);

        EditText username = findViewById(R.id.username),
                email = findViewById(R.id.email),
                nama = findViewById(R.id.nama);

        Intent intent = getIntent();

        String isiemail = intent.getStringExtra("email");
        String isinama = intent.getStringExtra("nama");

        nama.setText(isinama);
        email.setText(isiemail);



        SharedPreferences sharedPreferences = getSharedPreferences("prefRegister",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        kembali = findViewById(R.id.kembali);
        kembali.setOnClickListener(view -> {
            finish();
        });


        lanjut = findViewById(R.id.lanjut);
        lanjut.setEnabled(true);
        lanjut.setOnClickListener(view ->{
            lanjut.setEnabled(false);                               // Tombol dinonaktifkan saat ditekan
            String usernameText = username.getText().toString();
            String emailtext = email.getText().toString();
            String namatext = nama.getText().toString();
            if (TextUtils.isEmpty(usernameText)) {
                username.setError("Username Harus Diisi");
                username.requestFocus();
                enableButton(lanjut);                           // Aktifkan kembali tombol
            }else if (username.length() <= 6) {
                username.setError("Username haurs lebih dari 6");
                username.requestFocus();
                enableButton(lanjut);
            } else if(email.getText().toString().isEmpty()) {
                email.setError("Email Harus Diisi");
                email.requestFocus();
                enableButton(lanjut);
            } else if (nama.getText().toString().isEmpty()) {
                nama.setError("Nama Harus Diisi");
                enableButton(lanjut);
            }else {
                APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
                Call<ResponRegister1> call = apiRequestData.register1(usernameText, emailtext, namatext);

                call.enqueue(new Callback<ResponRegister1>() {
                    @Override
                    public void onResponse(Call<ResponRegister1> call, Response<ResponRegister1> response) {
                        if (response.body().kode == 1) {
                            editor.putString("username", usernameText);
                            editor.apply();
                            Intent pindah = new Intent(register1.this, register2.class);
                            startActivity(pindah);
                            finish();
                        } else if (response.body().kode == 0) {         // 0=username
                            Toast.makeText(register1.this, "username sudah terdaftar", Toast.LENGTH_SHORT).show();
                            enableButton(lanjut);
                        }else if (response.body().kode == 3) {          // 3=email
                            // Email sudah terdaftar
                            Toast.makeText(register1.this, "Email sudah terdaftar", Toast.LENGTH_SHORT).show();
                            enableButton(lanjut);
                        }  else if (response.body().kode == 2) {        // 2=registrasi gagal
                            Toast.makeText(register1.this, "Registrasi gagal", Toast.LENGTH_SHORT).show();
                            enableButton(lanjut);
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponRegister1> call, Throwable t) {
                        Toast.makeText(register1.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        enableButton(lanjut);
                    }
                });
            }

        });








    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void enableButton(Button button){
        button.setEnabled(true);
    }
}