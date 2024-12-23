package com.ELayang.Desa.Surat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ELayang.Desa.API.APIRequestData;
import com.ELayang.Desa.API.RetroServer;
import com.ELayang.Desa.DataModel.Surat.ModelSktm;
import com.ELayang.Desa.DataModel.Surat.ResponSkck;
import com.ELayang.Desa.DataModel.Surat.ResponSktm;
import com.ELayang.Desa.R;
import com.google.android.gms.common.api.Api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SKTM extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST_CODE = 1;
    DatePickerDialog picker;
    String selectedGender, username;

    String tempattanggallahirbapak, tempattanggallahiribu, tempattanggallahiranak;

    EditText namabapak, tempatbapak, tanggalbapak, pekerjaanbapak, alamatbapak;
    EditText namaibu, tempatibu, tanggalibu, pekerjaanibu, alamatibu;

    EditText namaanak, tempatanak, tanggalanak, alamatanak;
    Spinner jeniskelaminanak;
    Button kirim, update;
    private boolean hasilCek = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surat_sktm);

        SharedPreferences sharedPreferences = getSharedPreferences("prefLogin", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        TextView namafile = findViewById(R.id.t_file_name);

        //bapak
        namabapak = findViewById(R.id.namabapak);
        tempatbapak = findViewById(R.id.tempatbapak);
        tanggalbapak = findViewById(R.id.tanggalbapak);
        tanggalbapak.setFocusableInTouchMode(false);
        tanggalbapak.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);

            picker = new DatePickerDialog(SKTM.this, (view, year1, monthOfYear, dayOfMonth) -> {
                cldr.set(year1, monthOfYear, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
                String formattedDate = sdf.format(cldr.getTime());
                tanggalbapak.setText(formattedDate);
            }, year, month, day);
            picker.show();
        });
        pekerjaanbapak = findViewById(R.id.pekerjaanbapak);
        alamatbapak = findViewById(R.id.alamatbapak);

        //ibu
        namaibu = findViewById(R.id.namaibu);
        tempatibu = findViewById(R.id.tempatibu);
        tanggalibu = findViewById(R.id.tanggalibu);
        tanggalibu.setFocusableInTouchMode(false);
        tanggalibu.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);

            picker = new DatePickerDialog(SKTM.this, (view, year1, monthOfYear, dayOfMonth) -> {
                cldr.set(year1, monthOfYear, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
                String formattedDate = sdf.format(cldr.getTime());
                tanggalibu.setText(formattedDate);
            }, year, month, day);
            picker.show();
        });
        pekerjaanibu = findViewById(R.id.pekerjaanibu);
        alamatibu = findViewById(R.id.alamatibu);

        //anak
        namaanak = findViewById(R.id.namaanak);
        tempatanak = findViewById(R.id.tempatanak);
        tanggalanak = findViewById(R.id.tanggalanak);
        tanggalanak.setFocusableInTouchMode(false);
        tanggalanak.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);

            picker = new DatePickerDialog(SKTM.this, (view, year1, monthOfYear, dayOfMonth) -> {
                cldr.set(year1, monthOfYear, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
                String formattedDate = sdf.format(cldr.getTime());
                tanggalanak.setText(formattedDate);
            }, year, month, day);
            picker.show();
        });
        alamatanak = findViewById(R.id.alamatanak);
        kirim = findViewById(R.id.kirim);
        update = findViewById(R.id.update);
        Button btnChooseFile = findViewById(R.id.btn_choose_file);

        //pilihan jenis kelamin
        String[] genderOptions = getResources().getStringArray(R.array.jenis_kelamin_array);
        jeniskelaminanak = findViewById(R.id.jeniskelamin);
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderOptions);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jeniskelaminanak.setAdapter(genderAdapter);
        jeniskelaminanak.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedGender = genderOptions[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Toast.makeText(SKTM.this, "Pilih Jenis Kelamin", Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = getIntent();
        String nopengajuan = intent.getStringExtra("nopengajuan");

        if (nopengajuan != null) {
            kirim.setVisibility(View.INVISIBLE);

            APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
            Call<ResponSktm> call = apiRequestData.ambilsktm(nopengajuan, "0");

            call.enqueue(new Callback<ResponSktm>() {
                @Override
                public void onResponse(Call<ResponSktm> call, Response<ResponSktm> response) {
                    if (response.body().isKode()) {
                        // Anggap bahwa Anda memiliki objek SktmData dari respons Retrofit
                        ModelSktm sktmData = response.body().getData().get(0);

                        // Set nilai untuk Bapak
                        namabapak.setText(sktmData.getNama_bapak());
                        tempatbapak.setText(sktmData.getTempat_lahir_bapak());
                        tanggalbapak.setText(sktmData.getTanggal_lahir_bapak());
                        pekerjaanbapak.setText(sktmData.getPekerjaan_bapak());
                        alamatbapak.setText(sktmData.getAlamat_bapak());

                        // Set nilai untuk Ibu
                        namaibu.setText(sktmData.getNama_ibu());
                        tempatibu.setText(sktmData.getTempat_lahir_ibu());
                        tanggalibu.setText(sktmData.getTanggal_lahir_ibu());
                        pekerjaanibu.setText(sktmData.getPekerjaan_ibu());
                        alamatibu.setText(sktmData.getAlamat_ibu());

                        // Set nilai untuk Anak
                        namaanak.setText(sktmData.getNama_anak());
                        tempatanak.setText(sktmData.getTempat_lahir_anak());
                        tanggalanak.setText(sktmData.getTanggal_lahir_anak());
                        alamatanak.setText(sktmData.getAlamat_anak());

                        String jeniskelamin = sktmData.getJenis_kelamin_anak();

                        String[] jenis = getResources().getStringArray(R.array.jenis_kelamin_array);
                        int index = Arrays.asList(jenis).indexOf(jeniskelamin);
                        jeniskelaminanak.setSelection(index);

                    } else {
                        Toast.makeText(SKTM.this, response.body().getPesan(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponSktm> call, Throwable t) {
                    Log.e("error pengambilan data sktm", t.getMessage());
                }
            });
        } else {
            update.setVisibility(View.INVISIBLE);
        }

        update.setOnClickListener(v->{
            cek(namabapak);
            cek(tanggalbapak);
            cek(tempatbapak);
            cek(alamatbapak);
            cek(pekerjaanbapak);
            cek(namaibu);
            cek(tanggalibu);
            cek(tempatibu);
            cek(alamatibu);
            cek(pekerjaanibu);
            cek(namaanak);
            cek(tanggalanak);
            cek(tempatanak);
            cek(alamatanak);
            cek(tempatanak);


            if (hasilCek == false) {
                Toast.makeText(this, "Isi semua formulir terlebih dahulu", Toast.LENGTH_SHORT).show();
                reset();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Apakah data yang anda masukan sudah benar?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tempattanggallahirbapak = tempatbapak.getText().toString() + ", " + tanggalbapak.getText().toString();
                        tempattanggallahiribu = tempatibu.getText().toString() + ", " + tanggalibu.getText().toString();
                        tempattanggallahiranak = tempatanak.getText().toString() + ", " + tanggalanak.getText().toString();

                        String kode = "1";
                        APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
                        Call<ResponSktm> call = apiRequestData.updatesktm(nopengajuan,kode,namabapak.getText().toString(),tempattanggallahirbapak,pekerjaanbapak.getText().toString(),
                                alamatbapak.getText().toString(),namaibu.getText().toString(),tempattanggallahiribu,pekerjaanibu.getText().toString(),alamatibu.getText().toString(),
                                namaanak.getText().toString(),tempattanggallahiranak,selectedGender,alamatanak.getText().toString());

                        call.enqueue(new Callback<ResponSktm>() {
                            @Override
                            public void onResponse(Call<ResponSktm> call, Response<ResponSktm> response) {
                                if (response.body().isKode()){
                                    Toast.makeText(SKTM.this, response.body().getPesan(), Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    Toast.makeText(SKTM.this, "gagal merubah data", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponSktm> call, Throwable t) {
                                    Log.e("error pada update sktm", t.getMessage());
                            }
                        });
                    }
                }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });

        //api
//        APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
//        Call<ResponSktm> call = apiRequestData.sktm(username, namabapak.getText().toString(), tempattanggallahirbapak,pekerjaanbapak.getText().toString(), alamatbapak.getText().toString(),
//                namaibu.getText().toString(), tempattanggallahiribu, pekerjaanibu.getText().toString(), alamatibu.getText().toString(),
//                namaanak.getText().toString(), tempattanggallahiranak,selectedGender,alamatanak.getText().toString());

//        kirim.setEnabled(true);
        kirim.setOnClickListener(v -> {

            cek(namabapak);
            cek(tanggalbapak);
            cek(tempatbapak);
            cek(alamatbapak);
            cek(pekerjaanbapak);
            cek(namaibu);
            cek(tanggalibu);
            cek(tempatibu);
            cek(alamatibu);
            cek(pekerjaanibu);
            cek(namaanak);
            cek(tanggalanak);
            cek(tempatanak);
            cek(alamatanak);
            cek(tempatanak);

            if (hasilCek == false) {
                Toast.makeText(this, "Isi semua formulir terlebih dahulu", Toast.LENGTH_SHORT).show();
                reset();
            } else {
                kirim.setEnabled(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Pastikan data yang anda masukan benar!").setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tempattanggallahirbapak = tempatbapak.getText().toString() + ", " + tanggalbapak.getText().toString();
                        tempattanggallahiribu = tempatibu.getText().toString() + ", " + tanggalibu.getText().toString();
                        tempattanggallahiranak = tempatanak.getText().toString() + ", " + tanggalanak.getText().toString();

                        RequestBody usernameRequestBody = RequestBody.create(MediaType.parse("text/plain"), username);
                        RequestBody nama_bapakRequestBody = RequestBody.create(MediaType.parse("text/plain"), namabapak.getText().toString());
                        RequestBody tempat_tanggal_lahir_bapakRequestBody = RequestBody.create(MediaType.parse("text/plain"), tempattanggallahirbapak);
                        RequestBody pekerjaan_bapakRequestBody = RequestBody.create(MediaType.parse("text/plain"), pekerjaanbapak.getText().toString());
                        RequestBody alamat_bapakRequestBody = RequestBody.create(MediaType.parse("text/plain"), alamatbapak.getText().toString());
                        RequestBody nama_ibuRequestBody = RequestBody.create(MediaType.parse("text/plain"), namaibu.getText().toString());
                        RequestBody tempat_tanggal_lahir_ibuRequestBody = RequestBody.create(MediaType.parse("text/plain"), tempattanggallahiribu);
                        RequestBody pekerjaan_ibuRequestBody = RequestBody.create(MediaType.parse("text/plain"), pekerjaanibu.getText().toString());
                        RequestBody alamat_ibuRequestBody = RequestBody.create(MediaType.parse("text/plain"), alamatibu.getText().toString());
                        RequestBody namaRequestBody = RequestBody.create(MediaType.parse("text/plain"), namaanak.getText().toString());
                        RequestBody tempat_tanggal_lahir_anakRequestBody = RequestBody.create(MediaType.parse("text/plain"), tempattanggallahiranak);
                        RequestBody jenis_kelamin_anakRequestBody = RequestBody.create(MediaType.parse("text/plain"), selectedGender);
                        RequestBody alamatRequestBody = RequestBody.create(MediaType.parse("text/plain"), alamatanak.getText().toString());

                        // Jika file dipilih, masukkan file ke dalam permintaan
                        MultipartBody.Part filePart = null;
                        if (namafile.getText().length() > 0) {
                            // Menyiapkan file untuk di-upload
                            File file = new File(getFilesDir(), namafile.getText().toString()); // Path of file
                            RequestBody fileRequestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
                            filePart = MultipartBody.Part.createFormData("file", file.getName(), fileRequestBody);
                        }

                        // Call Retrofit APIRequestData to upload
                        APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
                        Call<ResponSktm> sktm = apiRequestData.sktm(usernameRequestBody, nama_bapakRequestBody, tempat_tanggal_lahir_bapakRequestBody,
                                pekerjaan_bapakRequestBody, alamat_bapakRequestBody, nama_ibuRequestBody, tempat_tanggal_lahir_ibuRequestBody,
                                pekerjaan_ibuRequestBody, alamat_ibuRequestBody, namaRequestBody, tempat_tanggal_lahir_anakRequestBody, jenis_kelamin_anakRequestBody, alamatRequestBody, filePart);

//                        APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
//                        Call<ResponSktm> call = apiRequestData.sktm(username, namabapak.getText().toString(),
//                                tempattanggallahirbapak, pekerjaanbapak.getText().toString(), alamatbapak.getText().toString(),
//                                namaibu.getText().toString(), tempattanggallahiribu, pekerjaanibu.getText().toString(),
//                                alamatibu.getText().toString(), namaanak.getText().toString(), tempattanggallahiranak,
//                                selectedGender, alamatanak.getText().toString());


                        sktm.enqueue(new Callback<ResponSktm>() {
                            @Override
                            public void onResponse(Call<ResponSktm> call, Response<ResponSktm> response) {
//                                ResponSktm respon = response.body();
//                                if (respon.isKode() == true) {
//                                    Toast.makeText(SKTM.this, "berhasil menambahkan", Toast.LENGTH_SHORT).show();
//
//
//                                    finish();
//                                }else {
//                                    kirim.setEnabled(true);
//                                }
                                if (response.isSuccessful()) {
                                    ResponSktm responSktm = response.body();
                                    if (responSktm != null && responSktm.isKode()) {
                                        Log.d("User Input", "Username: " + username);
                                        Toast.makeText(SKTM.this, "berhasil menambahkan", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(SKTM.this, "Gagal mengirim data", Toast.LENGTH_SHORT).show();
                                        kirim.setEnabled(true);
                                    }
                                } else {
                                    Toast.makeText(SKTM.this, "Gagal mengirim data", Toast.LENGTH_SHORT).show();
                                    kirim.setEnabled(true);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponSktm> call, Throwable t) {
                                Toast.makeText(SKTM.this, "Gagal menambahkan data", Toast.LENGTH_SHORT).show();
                                kirim.setEnabled(true);
                            }
                        });
                    }
                }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Kode yang akan dijalankan jika tombol "Tidak" diklik
                        // Misalnya, batalkan operasi
                    }
                }).show();
            }
        });
        btnChooseFile.setOnClickListener(v -> {
            chooseFile();
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TextView namafile = findViewById(R.id.t_file_name);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get the URI of the selected file
            Uri fileUri = data.getData();
            if (fileUri != null) {
                String fileName = getFileManager(fileUri);
                namafile.setText(fileName);
                Log.d("File Picker", "File pilih: " + fileUri.toString());

                String savedFilePath = saveFileToInternalStorage(fileUri, fileName);
                if (savedFilePath != null) {
                    // Menyimpan path dan nama file ke database
//                    saveFileMetadataToDatabase(fileName, savedFilePath);
                }
            }
        }
    }

    private String saveFileToInternalStorage(Uri fileUri, String fileName) {
        try {
            // Mengambil input stream dari file yang dipilih
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            File file = new File(getFilesDir(), fileName); // Menyimpan file di direktori internal aplikasi
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            outputStream.close();

            return file.getAbsolutePath(); // Mengembalikan path file yang disalin
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFileManager(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (columnIndex != -1) {
                        result = cursor.getString(columnIndex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        // Fallback untuk file uri
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }

        return result;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin kembali?").setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();

    }


    private void cek(EditText editText) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            editText.setError("Harus Diisi");
            editText.requestFocus();
            hasilCek = false;
        }
    }


    private void reset() {
        hasilCek = true;
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");  // Set MIME type to "*/*" to allow any file type
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }
}