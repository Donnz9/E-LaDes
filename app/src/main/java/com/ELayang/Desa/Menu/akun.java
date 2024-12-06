package com.ELayang.Desa.Menu;

import static android.content.Context.MODE_PRIVATE;

import static com.ELayang.Desa.API.RetroServer.API_IMAGE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Akun.ModelLogin;
import com.ELayang.Desa.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.io.File;

public class akun extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    TextView nama, email, usernamee;
    private ImageView foto;
    MaterialButton buka;
    Button keluar;
    private static final String CHANNEL_ID = "MyChannelID";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_akun, container, false);

        nama = view.findViewById(R.id.masukan_nama);
        email = view.findViewById(R.id.masukan_email);
        usernamee = view.findViewById(R.id.masukan_username);
        foto = view.findViewById(R.id.foto_profil);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("prefLogin", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
//        String fotoPath = sharedPreferences.getString("foto_profil", null);
//
        ModelLogin api_image1 = new ModelLogin();
        api_image1.getAPI_IMAGE();
        String api_image = API_IMAGE+api_image1;
//        if (api_image != null) {
//            Glide.with(this)
//                    .load(api_image) // Path gambar yang disimpan
//                    .placeholder(R.drawable.akun_profil) // Placeholder jika gambar tidak ada
//                    .into(foto); // ImageView tempat menampilkan foto
//        }
//        if (api_image == null) {
//            foto.setImageResource(R.drawable.akun_profil);
//        }

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
        nama.setText(sharedPreferences.getString("nama", ""));
        email.setText(sharedPreferences.getString("email", ""));
        usernamee.setText(sharedPreferences.getString("username", ""));

        keluar = view.findViewById(R.id.logout);
        keluar.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Konfirmasi Keluar");
            builder.setMessage("Apakah anda yakin ingin keluar?");
            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Hapus data dari SharedPreferences
                    editor.clear();
                    editor.apply();

                    getActivity().finish();
                }
            });
            builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        });

        buka = view.findViewById(R.id.buka);
        buka.setOnClickListener(v -> {

            Intent buka = new Intent(getContext(), edit_profil.class);
            startActivity(buka);
        });


        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("GoogleSignIn", "Connection failed: " + connectionResult);
    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }
//
//    private void createNotificationChannel() {
//        // Cek versi Android, karena pembuatan channel diperlukan pada Android Oreo (API level 26) ke atas
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "MyChannelName";
//            String description = "MyChannelDescription";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//            channel.enableLights(true);
//            channel.setLightColor(Color.RED);
//
//            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }
//
//    private void showNotification() {
//        Intent intent = new Intent(getActivity(), akun.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
//                .setSmallIcon(R.drawable.logo)
//                .setContentTitle("Judul Notifikasi")
//                .setContentText("Isi notifikasi yang ingin ditampilkan.")
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);
//
//        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(1, builder.build());
//    }

}