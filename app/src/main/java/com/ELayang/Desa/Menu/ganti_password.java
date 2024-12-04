package com.ELayang.Desa.Menu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Akun.ResponUpdate;
import com.ELayang.Desa.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ganti_password extends AppCompatActivity {
    EditText password1, password2;
    Button simpan;
    private boolean isPasswordVisible = true;
    private ImageView ikon;
    private String KEY_NAME = "NAMA";
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganti_password);

        password1 = findViewById(R.id.e_password1);
        password2 = findViewById(R.id.e_password2);

        SharedPreferences sharedPreferences = getSharedPreferences("prefLogin", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");

        // Menyembunyikan password dengan PasswordTransformationMethod
        password1.setTransformationMethod(PasswordTransformationMethod.getInstance());
        password2.setTransformationMethod(PasswordTransformationMethod.getInstance());

        simpan = findViewById(R.id.simpan);
        simpan.setOnClickListener(v -> {

            if (password1.getText().toString().isEmpty()) {
                password1.setError("Password Harus Diisi");
                password1.requestFocus();
            } else if (password2.getText().toString().isEmpty()) {
                password2.setError("Masukan Konformasi Password");
                password2.requestFocus();
            } else if (!password1.getText().toString().equals(password2.getText().toString())) {
                password2.setError("Konfirmasi Password Tidak Sama");
                password2.requestFocus();
            } else if (password1.length() < 6) {
                password1.setError("Password Harus Lebih dari 6 karakter");
                password1.requestFocus();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Apakah kamu yakin ingin melanjutkan?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
                                Call<ResponUpdate> call = apiRequestData.update_akun(username,sharedPreferences.getString("email", ""),
                                        password2.getText().toString(), sharedPreferences.getString("nama", ""));
                                Log.d("DEBUG", "Password1: " + password1.getText().toString());
                                Log.d("DEBUG", "Password2: " + password2.getText().toString());

                                call.enqueue(new Callback<ResponUpdate>() {
                                    @Override
                                    public void onResponse(Call<ResponUpdate> call, Response<ResponUpdate> response) {
                                        if (response.body().isKode() == true) {
                                            // Update password di SharedPreferences
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.apply();

                                            Toast.makeText(ganti_password.this, "Password Berhasil Diupdate", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(ganti_password.this, "Password Gagal Diupdate", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponUpdate> call, Throwable t) {
                                        Toast.makeText(ganti_password.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton ("Tidak", null)
                        .show();

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(this, "gunakan tombol kembali yang ada di atas", Toast.LENGTH_SHORT).show();
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    public void kembali(View view) {
        finish();
    }
//
//    private void togglePasswordVisibility(EditText editText) {
//        // Toggle antara menampilkan dan menyembunyikan password
//
//        editText.setTransformationMethod(isPasswordVisible ?
//                null : PasswordTransformationMethod.getInstance());
//
//        // Set jenis input berdasarkan kondisi
//        editText.setInputType(isPasswordVisible ?
//                android.text.InputType.TYPE_CLASS_TEXT :
//                android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
//
//        // Mengatur teks tombol berdasarkan keadaan password
//        show.setText(isPasswordVisible ? "Sembunyikan Password" : "Lihat Password");
//
//    }
//
//    private boolean reset(){
//        isPasswordVisible = !isPasswordVisible;
//        return isPasswordVisible;
//    }
}