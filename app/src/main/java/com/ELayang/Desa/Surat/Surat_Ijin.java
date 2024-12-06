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
import com.ELayang.Desa.DataModel.Surat.ModelSkck;
import com.ELayang.Desa.DataModel.Surat.ModelSuratijin;
import com.ELayang.Desa.DataModel.Surat.ResponSkck;
import com.ELayang.Desa.DataModel.Surat.ResponSuratijin;
import com.ELayang.Desa.R;

import org.chromium.base.Log;

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

public class Surat_Ijin extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST_CODE = 1;
    DatePickerDialog picker;
    String selectedGender;

    EditText nama, nik, jenis_kelamin, tempat_tgl_lahir, tgl_lahir,
            kewarganegaraan, agama, pekerjaan, alamat, tempat_kerja, bagian, tanggal, alasan;

    Button kirim, update;
    Spinner spinnerGender;

    private boolean hasilCek = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surat_ijin);

        SharedPreferences sharedPreferences = getSharedPreferences("prefLogin", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        nama = findViewById(R.id.e_nama);
        nik = findViewById(R.id.e_nik);
        tempat_tgl_lahir = findViewById(R.id.e_tempat_lahir);
        tgl_lahir = findViewById(R.id.e_tanggal);
        kewarganegaraan = findViewById(R.id.e_kebangsaan);
        agama = findViewById(R.id.e_agama);
        pekerjaan = findViewById(R.id.e_pekerjaan);
        alamat = findViewById(R.id.e_alamat);
        tempat_kerja = findViewById(R.id.e_tempat_kerja);
        bagian = findViewById(R.id.e_bagian);
        tanggal = findViewById(R.id.e_tanggal_bawah);
        alasan = findViewById(R.id.e_alasan);
        TextView namafile = findViewById(R.id.t_file_name);
        spinnerGender = findViewById(R.id.e_jenis);

        tanggal.setFocusableInTouchMode(false);
        tanggal.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);

            picker = new DatePickerDialog(Surat_Ijin.this, (view, year1, monthOfYear, dayOfMonth) -> {
                cldr.set(year1, monthOfYear, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
                String formattedDate = sdf.format(cldr.getTime());
                tanggal.setText(formattedDate);
            }, year, month, day);
            picker.show();
        });

        tgl_lahir.setFocusableInTouchMode(false);
        tgl_lahir.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);

            picker = new DatePickerDialog(Surat_Ijin.this, (view, year1, monthOfYear, dayOfMonth) -> {
                cldr.set(year1, monthOfYear, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
                String formattedDate = sdf.format(cldr.getTime());
                tgl_lahir.setText(formattedDate);
            }, year, month, day);
            picker.show();
        });
         kirim = findViewById(R.id.kirim);
         update = findViewById(R.id.update);
         Button btnChooseFile = findViewById(R.id.btn_choose_file);

        String[] genderOptions = getResources().getStringArray(R.array.jenis_kelamin_array);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderOptions);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedGender = genderOptions[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Toast.makeText(Surat_Ijin.this, "Pilih Jenis Kelamin", Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = getIntent();
        String nopengajuan = intent.getStringExtra("nopengajuan");

        if (nopengajuan != null) {
            kirim.setVisibility(View.INVISIBLE);

            String kode = "0";
            APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
            Call<ResponSuratijin> call = apiRequestData.ambilsuratijin(nopengajuan,kode);

            call.enqueue(new Callback<ResponSuratijin>() {
                @Override
                public void onResponse(Call<ResponSuratijin> call, Response<ResponSuratijin> response) {
                    if (response.body().isKode()){
                        ModelSuratijin  model = response.body().getData().get(0);
                        nik.setText(model.getNik());
                        nama.setText(model.getNama());
                        tempat_tgl_lahir.setText(model.getTempat());
                        tgl_lahir.setText(model.getTanggal());
                        kewarganegaraan.setText(model.getKewarganegaraan());
                        agama.setText(model.getAgama());
                        pekerjaan.setText(model.getPekerjaan());
                        alamat.setText(model.getAlamat());
                        tempat_kerja.setText(model.getTempat_Kerja());
                        bagian.setText(model.getBagian());
                        tanggal.setText(model.getTanggal_Ijin());
                        alasan.setText(model.getAlasan());

                        String jenis = model.getJenis_kelamin();

                        String[] jeniskelamin = getResources().getStringArray(R.array.jenis_kelamin_array);
                        int index = Arrays.asList(jeniskelamin).indexOf(jenis);
                        spinnerGender.setSelection(index);
                    }else{
                        Toast.makeText(Surat_Ijin.this, response.body().getPesan(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponSuratijin> call, Throwable t) {

                }
            });

        } else {
            update.setVisibility(View.GONE);
        }

        update.setOnClickListener(v->{
            cek(nama);
            cek(nik);
            cek(tempat_tgl_lahir);
            cek(tempat_kerja);
            cek(tgl_lahir);
            cek(kewarganegaraan);
            cek(agama);
            cek(pekerjaan);
            cek(alamat);
            cek(alasan);
            cek(bagian);
            cek(tanggal);

            if (hasilCek == false) {
                Toast.makeText(this, "Isi semua formulir terlebih dahulu", Toast.LENGTH_SHORT).show();
                reset();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Apakah data yang anda masukan sudah benar?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String kode = "1";
                                String tempat_tanggal_lahir = tempat_tgl_lahir.getText() + ", " + tgl_lahir.getText();
                                APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
                                Call<ResponSuratijin> call = apiRequestData.updatesuratijin(nopengajuan, kode, nama.getText().toString(),
                                        nik.getText().toString(), selectedGender, tempat_tanggal_lahir, kewarganegaraan.getText().toString(),
                                        agama.getText().toString(), pekerjaan.getText().toString(), alamat.getText().toString(),
                                        tempat_kerja.getText().toString(), bagian.getText().toString(), tanggal.getText().toString(),
                                        alasan.getText().toString());
                                call.enqueue(new Callback<ResponSuratijin>() {
                                    @Override
                                    public void onResponse(Call<ResponSuratijin> call, Response<ResponSuratijin> response) {
                                        if (response.body().isKode()) {
                                            Toast.makeText(Surat_Ijin.this, response.body().getPesan(), Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(Surat_Ijin.this, response.body().getPesan(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponSuratijin> call, Throwable t) {
                                        Log.e("error update surat_ijin", t.getMessage());
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });


//        kirim.setEnabled(true);
        kirim.setOnClickListener(v -> {
            cek(nama);
            cek(nik);
            cek(tempat_tgl_lahir);
            cek(tempat_kerja);
            cek(tgl_lahir);
            cek(kewarganegaraan);
            cek(agama);
            cek(pekerjaan);
            cek(alamat);
            cek(alasan);
            cek(bagian);
            cek(tanggal);

            if(nik.length() < 13){
                Toast.makeText(this, "Lengkapi Nik anda", Toast.LENGTH_SHORT).show();
            } else if (hasilCek == false) {
                Toast.makeText(this, "Isi semua formulir terlebih dahulu", Toast.LENGTH_SHORT).show();
                reset();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Apakah data yang anda inputkan benar?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String tempat_tanggal_lahir = tempat_tgl_lahir.getText() + ", " + tgl_lahir.getText();

//                                APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
//                                Call<ResponSuratijin> call = apiRequestData.suratijin(username, nama.getText().toString(),
//                                        nik.getText().toString(), selectedGender, tempat_tanggal_lahir, kewarganegaraan.getText().toString(),
//                                        agama.getText().toString(), pekerjaan.getText().toString(), alamat.getText().toString(),
//                                        tempat_kerja.getText().toString(), bagian.getText().toString(), tanggal.getText().toString(),
//                                        alasan.getText().toString());

                                // Convert data to RequestBody
                                RequestBody namaRequestBody = RequestBody.create(MediaType.parse("text/plain"), nama.getText().toString());
                                RequestBody nikRequestBody = RequestBody.create(MediaType.parse("text/plain"), nik.getText().toString());
                                RequestBody tempatTanggalLahirRequestBody = RequestBody.create(MediaType.parse("text/plain"), tempat_tanggal_lahir);
                                RequestBody kewarganegaraanRequestBody = RequestBody.create(MediaType.parse("text/plain"), kewarganegaraan.getText().toString());
                                RequestBody agamaRequestBody = RequestBody.create(MediaType.parse("text/plain"), agama.getText().toString());
                                RequestBody pekerjaanRequestBody = RequestBody.create(MediaType.parse("text/plain"), pekerjaan.getText().toString());
                                RequestBody alamatRequestBody = RequestBody.create(MediaType.parse("text/plain"), alamat.getText().toString());
                                RequestBody usernameRequestBody = RequestBody.create(MediaType.parse("text/plain"), username);
                                RequestBody jenis_kelaminRequestBody = RequestBody.create(MediaType.parse("text/plain"), selectedGender);
                                RequestBody tempat_kerjaRequestBody = RequestBody.create(MediaType.parse("text/plain"), tempat_kerja.getText().toString());
                                RequestBody bagianRequestBody = RequestBody.create(MediaType.parse("text/plain"), bagian.getText().toString());
                                RequestBody tanggalRequestBody = RequestBody.create(MediaType.parse("text/plain"), tanggal.getText().toString());
                                RequestBody alasanRequestBody = RequestBody.create(MediaType.parse("text/plain"), alasan.getText().toString());

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
                                Call<ResponSuratijin> suratijin = apiRequestData.suratijin(usernameRequestBody, namaRequestBody, nikRequestBody,
                                        jenis_kelaminRequestBody, tempatTanggalLahirRequestBody, kewarganegaraanRequestBody, agamaRequestBody, pekerjaanRequestBody,
                                        alamatRequestBody, tempat_kerjaRequestBody, bagianRequestBody, tanggalRequestBody, alasanRequestBody, filePart);

                                suratijin.enqueue(new Callback<ResponSuratijin>() {
                                    @Override
                                    public void onResponse(Call<ResponSuratijin> call, Response<ResponSuratijin> response) {
//                                        ResponSuratijin respon = response.body();
//                                        if (respon.isKode() == true) {
//                                            Toast.makeText(Surat_Ijin.this, "Berhasil Ditambahkan", Toast.LENGTH_SHORT).show();
//                                            finish();
//                                        }else{
//                                            update.setEnabled(true);
//                                        }
                                        if (response.isSuccessful()) {
                                            ResponSuratijin responSuratijin = response.body();
                                            if (responSuratijin != null && responSuratijin.isKode()) {
                                                android.util.Log.d("User Input", "Username: " + username);
                                                Toast.makeText(Surat_Ijin.this, "berhasil menambahkan", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                Toast.makeText(Surat_Ijin.this, "Gagal mengirim data", Toast.LENGTH_SHORT).show();
                                                kirim.setEnabled(true);
                                            }
                                        } else {
                                            Toast.makeText(Surat_Ijin.this, "Gagal mengirim data", Toast.LENGTH_SHORT).show();
                                            kirim.setEnabled(true);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponSuratijin> call, Throwable t) {
                                        Toast.makeText(Surat_Ijin.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.e("error", t.getMessage());

                                        update.setEnabled(true);
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
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
                android.util.Log.d("File Picker", "File pilih: " + fileUri.toString());

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
        builder.setMessage("Apakah anda yakin ingin kembali?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void cek(EditText editText) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            editText.setError("Harus Diisi");
            editText.requestFocus();
            hasilCek = false;
        }
    }
    private void reset(){
        hasilCek =true;
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");  // Set MIME type to "*/*" to allow any file type
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }
}