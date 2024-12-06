package com.ELayang.Desa.Surat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.ELayang.Desa.DataModel.Surat.ModelSkck;
import com.ELayang.Desa.DataModel.Surat.ResponSkck;
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

public class SKCK extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST_CODE = 1;
    DatePickerDialog picker;
    String selectedGender;
    private boolean hasilCek = true;

//    ImageButton kembali;
//    TextView kode = findViewById(R.id.kode_surat),
//        ket= findViewById(R.id.keterangan);

    EditText nik, nama, tempat_lahir, kebangsaan, agama, status, pekerjaan, tempat_tinggal, tanggal;
    TextView tFileName;
    Button kirim, update;
    Spinner spinnerGender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surat_skck);
        SharedPreferences sharedPreferences = getSharedPreferences("prefLogin", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
//        String keterangan = sharedPreferences.getString("keterangan","");
//        kode.setText(kode_surat);
//        ket.setText(keterangan);

        kirim = findViewById(R.id.kirim);
        update = findViewById(R.id.update);
        Button btnChooseFile = findViewById(R.id.btn_choose_file);
        TextView namafile = findViewById(R.id.t_file_name);
        spinnerGender = findViewById(R.id.e_jenis);

        nik = findViewById(R.id.e_nik);
        nama = findViewById(R.id.e_nama);
        tempat_lahir = findViewById(R.id.e_tempat_lahir);
        kebangsaan = findViewById(R.id.e_kebangsaan);
        agama = findViewById(R.id.e_agama);
        status = findViewById(R.id.e_kawin);
        pekerjaan = findViewById(R.id.e_pekerjaan);
        tempat_tinggal = findViewById(R.id.e_tempat_tinggal);
        tanggal = findViewById(R.id.e_tanggal);

        tanggal.setFocusableInTouchMode(false);
        tanggal.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);

            picker = new DatePickerDialog(SKCK.this, (view, year1, monthOfYear, dayOfMonth) -> {
                cldr.set(year1, monthOfYear, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
                String formattedDate = sdf.format(cldr.getTime());
                tanggal.setText(formattedDate);
            }, year, month, day);
            picker.show();
        });
        Intent intent = getIntent();
        String nopengajuan = intent.getStringExtra("nopengajuan");
        if (nopengajuan != null) {
            kirim.setVisibility(View.INVISIBLE);

            String kode = "0";
            APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
            Call<ResponSkck> call = apiRequestData.ambilskck(nopengajuan, kode);

            call.enqueue(new Callback<ResponSkck>() {
                @Override
                public void onResponse(Call<ResponSkck> call, Response<ResponSkck> response) {
                    if (response.body().kode) {
                        Toast.makeText(SKCK.this, response.body().getPesan(), Toast.LENGTH_SHORT).show();
                        ModelSkck model = response.body().getData().get(0);
                        nik.setText(model.getNik());
                        nama.setText(model.getNama());
                        tempat_lahir.setText(model.getTempat());
                        kebangsaan.setText(model.getKebangsaan());
                        agama.setText(model.getAgama());
                        status.setText(model.getStatus_perkawinan());
                        pekerjaan.setText(model.getPekerjaan());
                        tempat_tinggal.setText(model.getTempat_tinggal());
                        tanggal.setText(model.getTanggal());
                        //set selected spinner
                        String jenis = model.getJenis_kelamin();
                        String[] jeniskelamin = getResources().getStringArray(R.array.jenis_kelamin_array);
                        int position = Arrays.asList(jeniskelamin).indexOf(jenis);
                        spinnerGender.setSelection(position);
                    } else {
                        Toast.makeText(SKCK.this, response.body().getPesan(), Toast.LENGTH_SHORT).show();
                    }
                }


                @Override
                public void onFailure(Call<ResponSkck> call, Throwable t) {
                    Log.e("error setText", t.getMessage());
                }
            });

        } else {
            update.setVisibility(View.GONE);
        }

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
                Toast.makeText(SKCK.this, "Pilih Jenis Kelamin", Toast.LENGTH_SHORT).show();
            }
        });


//        kembali.findViewById(R.id.balikbos);
//        kembali.setOnClickListener(v->{
//            finish();
//        });

        btnChooseFile.setOnClickListener(v -> {
            chooseFile();
        });

        kirim.setEnabled(true);
        kirim.setOnClickListener(v -> {
            cek(nik);
            cek(nama);
            cek(tempat_lahir);
            cek(kebangsaan);
            cek(agama);
            cek(status);
            cek(pekerjaan);
            cek(tempat_tinggal);
//            cek(tanggal);

            if(nik.length() < 13){
                Toast.makeText(this, "Lengkapi Nik anda", Toast.LENGTH_SHORT).show();
            } else if (!hasilCek) {
                Toast.makeText(this, "Isi semua formulir terlebih dahulu", Toast.LENGTH_SHORT).show();
                reset();
            } else {
                kirim.setEnabled(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Apakah data yang anda inputkan sudah benar?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String tempat_tanggal_lahir = tempat_lahir.getText().toString() + ", " + tanggal.getText().toString();
//
//                                APIRequestData ardData = RetroServer.konekRetrofit().create(APIRequestData.class);
//                                Call<ResponSkck> skck = ardData.skck(nama.getText().toString(), nik.getText().toString()
//                                        , tempat_tanggal_lahir, kebangsaan.getText().toString(), agama.getText().toString()
//                                        , status.getText().toString(), pekerjaan.getText().toString()
//                                        , tempat_tinggal.getText().toString(), username, selectedGender
//                                );
                                // Convert data to RequestBody
                                RequestBody namaRequestBody = RequestBody.create(MediaType.parse("text/plain"), nama.getText().toString());
                                RequestBody nikRequestBody = RequestBody.create(MediaType.parse("text/plain"), nik.getText().toString());
                                RequestBody tempatTanggalLahirRequestBody = RequestBody.create(MediaType.parse("text/plain"), tempat_tanggal_lahir);
                                RequestBody kebangsaanRequestBody = RequestBody.create(MediaType.parse("text/plain"), kebangsaan.getText().toString());
                                RequestBody agamaRequestBody = RequestBody.create(MediaType.parse("text/plain"), agama.getText().toString());
                                RequestBody statusRequestBody = RequestBody.create(MediaType.parse("text/plain"), status.getText().toString());
                                RequestBody pekerjaanRequestBody = RequestBody.create(MediaType.parse("text/plain"), pekerjaan.getText().toString());
                                RequestBody tempatTinggalRequestBody = RequestBody.create(MediaType.parse("text/plain"), tempat_tinggal.getText().toString());
                                RequestBody usernameRequestBody = RequestBody.create(MediaType.parse("text/plain"), username);
                                RequestBody jenis_kelaminRequestBody = RequestBody.create(MediaType.parse("text/plain"), selectedGender);

//                                // Prepare the file for upload
//                                File file = new File(getFilesDir(), namafile.getText().toString()); // Path of file
//                                RequestBody fileRequestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
//                                MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), fileRequestBody);
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
                                Call<ResponSkck> uploadFile = apiRequestData.uploadFile(namaRequestBody, nikRequestBody, tempatTanggalLahirRequestBody,
                                        kebangsaanRequestBody, agamaRequestBody, statusRequestBody, pekerjaanRequestBody, tempatTinggalRequestBody,
                                        usernameRequestBody, jenis_kelaminRequestBody, filePart);
//                                skck.enqueue(new Callback<ResponSkck>() {
                                uploadFile.enqueue(new Callback<ResponSkck>() {

                                    @Override
                                    public void onResponse(Call<ResponSkck> call, Response<ResponSkck> response) {
                                        if (response.isSuccessful()) {
                                            ResponSkck responSkck = response.body();
                                            if (responSkck != null && responSkck.isKode()) {
                                                Log.d("User Input", "Username: " + username);
                                                Toast.makeText(SKCK.this, "berhasil menambahkan", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                Toast.makeText(SKCK.this, "Gagal mengirim data", Toast.LENGTH_SHORT).show();
                                                kirim.setEnabled(true);
                                            }
                                        } else {
                                            Toast.makeText(SKCK.this, "Gagal mengirim data", Toast.LENGTH_SHORT).show();
                                            kirim.setEnabled(true);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponSkck> call, Throwable t) {
                                        Toast.makeText(SKCK.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                        kirim.setEnabled(true);
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

//        update.setEnabled(true);
        update.setOnClickListener(v -> {

            cek(nik);
            cek(nama);
            cek(tempat_lahir);
            cek(kebangsaan);
            cek(agama);
            cek(status);
            cek(pekerjaan);
            cek(tempat_tinggal);

            if (!hasilCek) {
                Toast.makeText(this, "Isi semua formulir terlebih dahulu", Toast.LENGTH_SHORT).show();
                reset();
            } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Apakah data yang anda masukan sudah benar?")
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String tempat_tanggal_lahir = tempat_lahir.getText().toString() + ", " + tanggal.getText().toString();
                            String kode = "1";
                            APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
                            Call<ResponSkck> call = apiRequestData.updateskck(nopengajuan, kode, nama.getText().toString()
                                    , nik.getText().toString()
                                    , tempat_tanggal_lahir, kebangsaan.getText().toString(), agama.getText().toString()
                                    , status.getText().toString(), pekerjaan.getText().toString()
                                    , tempat_tinggal.getText().toString(), selectedGender);

                            call.enqueue(new Callback<ResponSkck>() {
                                @Override
                                public void onResponse(Call<ResponSkck> call, Response<ResponSkck> response) {
                                    if (response.body().kode) {
                                        Toast.makeText(SKCK.this, response.body().getPesan(), Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(SKCK.this, response.body().getPesan(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponSkck> call, Throwable t) {
                                    Log.e("error update skck", t.getMessage());
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
        namafile.setOnClickListener(v -> {
//            String filePath = getFilesDir() + "/" + namafile.getText().toString(); // Path file yang disimpan
//            File file = new File(filePath);
//
//            if (file.exists()) {
//                // Buat Intent untuk membuka file sesuai dengan jenisnya
//                Intent intent1 = new Intent(Intent.ACTION_VIEW);
//                Uri fileUri = Uri.fromFile(file);
//                intent1.setDataAndType(fileUri, getContentResolver().getType(fileUri));
//                intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Memberikan izin untuk membaca file
//                startActivity(intent1);
//            } else {
//                Toast.makeText(SKCK.this, "File tidak ditemukan", Toast.LENGTH_SHORT).show();
//            }
            String filename = namafile.getText().toString(); // Ambil nama file dari EditText

            // Menyimpan file di direktori aplikasi
            File file = new File(getFilesDir(), filename);
            Uri fileUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);

            // Membuat intent untuk membuka file dengan aplikasi lain
            Intent intent1 = new Intent(Intent.ACTION_VIEW);
            intent1.setDataAndType(fileUri, getContentResolver().getType(fileUri));
            intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Memberikan izin untuk membaca file

            // Memulai aktivitas dengan intent
            startActivity(intent);
        });
    }

    @Override
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