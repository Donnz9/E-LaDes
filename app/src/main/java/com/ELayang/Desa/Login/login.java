        package com.ELayang.Desa.Login;

        import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.text.TextUtils;
        import android.util.Log;
        import android.view.KeyEvent;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;

        import com.ELayang.Desa.API.APIRequestData;
        import com.ELayang.Desa.API.RetroServer;
        import com.ELayang.Desa.DataModel.Akun.ResponLogin;
        import com.ELayang.Desa.DataModel.Akun.ModelLogin;
        import com.ELayang.Desa.R;
        import com.ELayang.Desa.menu;
        import com.google.android.gms.auth.api.Auth;
        import com.google.android.gms.auth.api.identity.BeginSignInRequest;
        import com.google.android.gms.auth.api.identity.SignInClient;
        import com.google.android.gms.auth.api.identity.SignInCredential;
        import com.google.android.gms.auth.api.signin.*;
        import com.google.android.gms.common.SignInButton;
        import com.google.android.gms.common.api.ApiException;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.common.api.ResultCallback;
        import com.google.android.gms.common.api.Status;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.BuildConfig;
        import com.google.firebase.auth.AuthCredential;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.auth.GoogleAuthProvider;

        import org.jetbrains.annotations.Nullable;

        import java.util.HashMap;
        import java.util.Map;

        import retrofit2.Call;
        import retrofit2.Callback;
        import retrofit2.Response;

        public class login extends AppCompatActivity {
            private static final int RC_SIGN_IN = 6969;
            GoogleApiClient googleApiClient;
            SignInClient oneTapClient;
            private FirebaseAuth firebaseAuth;
            private GoogleSignInClient mGoogleSignInClient;
            EditText username, password;
            Button masuk;
            private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
            private boolean showOneTapUI = true;
            private FirebaseAuth mAuth;


            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_login);

                password = findViewById(R.id.password);

                // Konfigurasi Google Sign-In
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken("17751188767-tf4gmigh0b42bbg2fa2ej54qms8dmkeg.apps.googleusercontent.com") // webclientid di strings.xml
                        .requestEmail()
                        .build();

                // Inisialisasi GoogleSignInClient
                mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

                // Firebase Auth
                mAuth = FirebaseAuth.getInstance();

                SignInButton btnGoogleSignIn = findViewById(R.id.login_google);
                btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());

                SharedPreferences sharedPreferences = getSharedPreferences("prefLogin", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Mengambil seluruh data dari SharedPreferences
                Map<String, ?> allEntries = sharedPreferences.getAll();

                // Mengiterasi dan menampilkan seluruh data ke Logcat
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    Log.d("SharedPreferencesData", entry.getKey() + ": " + entry.getValue().toString());
                }

                EditText username = findViewById(R.id.username), password = findViewById(R.id.password);

                masuk = findViewById(R.id.masuk);
                masuk.setOnClickListener(view -> {

                    String usernameText = username.getText().toString();
                    String passwordText = password.getText().toString();


                    username.setError(null);
                    password.setError(null);
                    String usernametext = username.getText().toString();
                    if (TextUtils.isEmpty(usernametext)) {
                        username.setError("Username Harus Diisi");
                        username.requestFocus();
                    } else if (password.getText().toString().isEmpty()) {
                        password.setError("Password Harus Diisi");
                        password.requestFocus();
                    } else {
                        // Tambahkan logging hanya pada debug mode
                        if (BuildConfig.DEBUG) {
                            Log.d("DEBUG", "Login attempt, username: " + usernameText);
                            // Hindari mencetak password demi keamanan
                        }

                        APIRequestData ardData = RetroServer.konekRetrofit().create(APIRequestData.class);
                        Call<ResponLogin> getLoginResponse = ardData.login(username.getText().toString(), password.getText().toString());
                        getLoginResponse.enqueue(new Callback<ResponLogin>() {
                            @Override
                            public void onResponse(Call<ResponLogin> call, Response<ResponLogin> response) {
                                if (response.body().kode == 1) {
                                    ModelLogin user = response.body().getData().get(0);
                                    editor.putString("username", user.getUsername());
                                    editor.putString("password", user.getPassword());
                                    editor.putString("email", user.getEmail());
                                    editor.putString("nama", user.getNama());
                                    editor.putString("kode_otp", user.getKode_otp());
                                    editor.putString("profile_image", user.getAPI_IMAGE());
                                    editor.apply();
                                    Log.e("pue", "profil: " + user.getAPI_IMAGE());
                                    Intent pindah = new Intent(login.this, menu.class);
                                    startActivity(pindah);
                                    finish();

                                } else if (response.body().kode == 0) {
                                    username.requestFocus();
                                    Toast.makeText(login.this, "Akun Belum Terdaftar", Toast.LENGTH_SHORT).show();
                                } else if (response.body().kode == 2) {
                                    password.requestFocus();
                                    Toast.makeText(login.this, "Password Salah!!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponLogin> call, Throwable t) {
                                Toast.makeText(login.this, "Login failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                firebaseAuth = FirebaseAuth.getInstance();

                // Inisialisasi klien GoogleSignInClient
                BeginSignInRequest signInRequest = BeginSignInRequest.builder()
                        .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                // Your server's client ID, not your Android client ID.
                                .setServerClientId(getString(R.string.webclientid))
                                // Only show accounts previously used to sign in.
                                .setFilterByAuthorizedAccounts(true)
                                .build())
                        .build();


    //            GoogleSignInOptions gso = null;
    //            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    //            SignInButton btnGoogleSignIn = findViewById(R.id.login_google);
    //            btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());

            }

            public void bregister(View view) {
                Intent buka = new Intent(this, register1.class);
                startActivity(buka);
            }

            public void blupapassword(View view) {
                Intent buka = new Intent(this, lupa_password.class);
                startActivity(buka);
            }

            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event) {
                return super.onKeyDown(keyCode, event);
            }


            //firebase baru
            public void signInWithGoogle() {
                // Memulai proses login dengan Google dan memilih akun
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }



            @Override
            protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                switch (requestCode) {
                    case REQ_ONE_TAP:
                        try {
                            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                            String idToken = credential.getGoogleIdToken();
                            if (idToken !=  null) {
                                // Got an ID token from Google. Use it to authenticate
                                // with Firebase.
                                Log.d(TAG, "Got ID token.");
                            }
                        } catch (ApiException e) {
                            // ...
                        }
                        break;
                }
                if (requestCode == RC_SIGN_IN) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleGoogleSignInResult(task);
                }
                Log.d(TAG, "onActivityResult dipanggil, requestCode: " + requestCode);
            }

            private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
                try {
                    GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                    Log.d(TAG, "Google Sign-In berhasil: " + account.getEmail());
                    firebaseAuthWithGoogle(account);    // Melanjutkan ke Firebase Auth
                } catch (ApiException e) {
                    Log.e(TAG, "Google Sign-In gagal: " + e.getStatusCode(), e);
                }
            }

            private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            String email = user.getEmail();
                            APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
                            Call<ResponLogin> call = apiRequestData.logingoogle(email);
                            call.enqueue(new Callback<ResponLogin>() {
                                @Override
                                public void onResponse(Call<ResponLogin> call, Response<ResponLogin> response) {
                                    if (response.body() != null && response.body().kode == 1) {
                                        SharedPreferences sharedPreferences = getSharedPreferences("prefLogin", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        ModelLogin user = response.body().getData().get(0);
                                        Log.d("DEBUG", "Response dari API: " + user);

                                        String passwordToSave = "";
                                        if (password != null) {
                                            passwordToSave = password.getText().toString();
                                        }
                                        editor.putString("username", user.getUsername());
                                        editor.putString("email", user.getEmail());
                                        editor.putString("nama", user.getNama());
                                        editor.putString("password", user.getPassword());
                                        editor.putString("kode_otp", user.getKode_otp());
                                        editor.putString("profile_image", user.getAPI_IMAGE());
                                        editor.apply();
                                        Log.d("DEBUG", "Password disimpan: " + sharedPreferences.getString("password", ""));

                                        Intent pindah = new Intent(login.this, menu.class);
                                        revokeAccess();
                                        startActivity(pindah);
                                        finish(); // Pastikan untuk menutup activity saat ini setelah pindah ke menu.class
                                    } else {
                                        Intent intent = new Intent(login.this, register1.class);
                                        intent.putExtra("email", user.getEmail());
                                        intent.putExtra("nama", user.getDisplayName());
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponLogin> call, Throwable t) {
                                    Log.e("error sign in google:", t.getMessage());
                                }
                            });
                        }
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                    signOut();
                });
            }
            private void revokeAccess() {
                mGoogleSignInClient.revokeAccess();
            }

            public void signOut() {
                mGoogleSignInClient.signOut();
                revokeAccess();
            }

        }