package com.ELayang.Desa.Menu;

import static com.ELayang.Desa.API.RetroServer.API_IMAGE;

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
import com.ELayang.Desa.DataModel.Akun.ModelLogin;
import com.ELayang.Desa.DataModel.Akun.ResponUpdate;
import com.ELayang.Desa.R;
import com.bumptech.glide.Glide;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ganti_password extends AppCompatActivity {
    EditText password1, password2;
    Button simpan;
    private boolean isPasswordVisible = true;
    private ImageView foto;
    private String KEY_NAME = "NAMA";
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganti_password);

        password1 = findViewById(R.id.e_password1);
        password2 = findViewById(R.id.e_password2);
        foto = findViewById(R.id.ikon);

        SharedPreferences sharedPreferences = getSharedPreferences("prefLogin", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        ModelLogin api_image1 = new ModelLogin();
        api_image1.getAPI_IMAGE();
        String api_image = API_IMAGE+api_image1;
        RetroServer image = new RetroServer();

        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");
        String savedImagePath = sharedPreferences.getString("profile_image", "");

        Log.d("savedImagePath", "onCreate: "+savedImagePath);
        File imgFile = new File(savedImagePath);
        Log.d("imgFile", "onCreate: "+imgFile);
        if ( imgFile != null) {
            Log.d("getUrlImage", "onCreate: "+image.getUrlImage()+imgFile);
            Glide.with(this)
                    .load(image.getUrlImage()+imgFile) // Path gambar yang disimpan
                    .placeholder(R.drawable.akun_profil)
                    .circleCrop()// Placeholder jika gambar tidak ada
                    .into(foto);
        }

        Log.d("api_image", "onCreateView: "+api_image);

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
}