package com.ELayang.Desa.API;

import com.ELayang.Desa.DataModel.Akun.ResponUpdate;
import com.ELayang.Desa.DataModel.Lupa_Password.ResponPassword1;
import com.ELayang.Desa.DataModel.Lupa_Password.ResponPassword2;
import com.ELayang.Desa.DataModel.Akun.ResponLogin;
import com.ELayang.Desa.DataModel.Notifikasi.ResponNotifikasi;
import com.ELayang.Desa.DataModel.Register.ResponOTP;
import com.ELayang.Desa.DataModel.Register.ResponRegister1;
import com.ELayang.Desa.DataModel.Register.ResponRegister2;
import com.ELayang.Desa.DataModel.Register.ResponRegister3;
import com.ELayang.Desa.DataModel.StatusDasboardRespon;
import com.ELayang.Desa.DataModel.Surat.ResponSkck;
import com.ELayang.Desa.DataModel.ResponSurat;
import com.ELayang.Desa.DataModel.RiwayatSurat.ResponDiajukan;
import com.ELayang.Desa.DataModel.RiwayatSurat.ResponSelesai;
import com.ELayang.Desa.DataModel.Surat.ResponSktm;
import com.ELayang.Desa.DataModel.Surat.ResponSuratijin;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIRequestData {
    //buat ngambil data dari API/webservice retrieve.php
    @GET("retrieve.php")
    Call<ResponLogin> ardRetrieveData();

    @FormUrlEncoded
    @POST("Login.php")
    Call<ResponLogin> login(
            @Field("username") String username,
            @Field("password") String password
    );


    @GET("surat.php")
    Call<ResponSurat> surat();


//    @FormUrlEncoded
//    @POST("surat/skck.php")
//    Call<ResponSkck> skck(
//            @Field("nama") String nama,
//            @Field("nik") String nik,
//            @Field("tempat_tanggal_lahir") String tempat_tanggal_lahir,
//            @Field("kebangsaan") String kebangsaan,
//            @Field("agama") String agama,
//            @Field("status_perkawinan") String status,
//            @Field("pekerjaan") String pekerjaan,
//            @Field("tempat_tinggal") String tinggal,
//            @Field("username") String username,
//            @Field("jenis_kelamin") String jenis_kelamin
//    );

    @Multipart
    @POST("surat/skck.php")
    Call<ResponSkck> uploadFile(
            @Part("nama") RequestBody nama,
            @Part("nik") RequestBody nik,
            @Part("tempat_tanggal_lahir") RequestBody tempat_tanggal_lahir,
            @Part("kebangsaan") RequestBody kebangsaan,
            @Part("agama") RequestBody agama,
            @Part("status_perkawinan") RequestBody status,
            @Part("pekerjaan") RequestBody pekerjaan,
            @Part("tempat_tinggal") RequestBody tempatTinggal,
//            @Part("username") String username,
            @Part("username") RequestBody username,
            @Part("jenis_kelamin") RequestBody jenis_kelamin,
            @Part MultipartBody.Part file
    );

    @FormUrlEncoded
    @POST("register/register1.php")
    Call<ResponRegister1> register1(
            @Field("username") String username,
            @Field("email") String email,
            @Field("nama") String nama

    );

    @FormUrlEncoded
    @POST("register/register2.php")
    Call<ResponRegister2> register2(
            @Field("username") String username,
            @Field("kode_otp") String kode_otp

    );

    @FormUrlEncoded
    @POST("register/register3.php")
    Call<ResponRegister3> register3(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("register/kode_otp.php")
    Call<ResponOTP> kirim_otp(
            @Field("username") String username
    );

    @FormUrlEncoded
    @POST("lupa_password/password1.php")
    Call<ResponPassword1> lupa_password1(
            @Field("username") String username
    );

    @FormUrlEncoded
    @POST("lupa_password/password2.php")
    Call<ResponPassword2> lupa_password2(
            @Field("kode_otp") String kode_otp,
            @Field("password") String password,
            @Field("username") String username
    );

    @FormUrlEncoded
    @POST("riwayat_surat/surat_proses.php")
    Call<ResponDiajukan> proses(
            @Field("username") String username
    );

    @FormUrlEncoded
    @POST("riwayat_surat/surat_selesai.php")
    Call<ResponSelesai> selesai(
            @Field("username") String username
    );

//    @FormUrlEncoded
//    @POST("surat/surat_ijin.php")
//    Call<ResponSuratijin> suratijin(
//            @Field("username") String username,
//            @Field("nama") String Nama,
//            @Field("nik") String NIK,
//            @Field("jenis_kelamin") String Jenis_kelamin,
//            @Field("tempat_tanggal_lahir") String Tempat_tanggal_lahir,
//            @Field("kewarganegaraan") String Kewarganegaraan,
//            @Field("agama") String Agama,
//            @Field("pekerjaan") String Pekerjaan,
//            @Field("alamat") String Alamat,
//            @Field("aempat_kerja") String Tempat_Kerja,
//            @Field("bagian") String Bagian,
//            @Field("tanggal") String Tanggal,
//            @Field("alasan") String Alasan
//    );

    @Multipart
    @POST("surat/surat_ijin.php")
    Call<ResponSuratijin> suratijin(
            @Part("username") RequestBody username,
            @Part("nama") RequestBody nama,
            @Part("nik") RequestBody nik,
            @Part("jenis_kelamin") RequestBody jenis_kelamin,
            @Part("tempat_tanggal_lahir") RequestBody tempat_tanggal_lahir,
            @Part("kewarganegaraan") RequestBody Kewarganegaraan,
            @Part("agama") RequestBody Agama,
            @Part("pekerjaan") RequestBody Pekerjaan,
            @Part("alamat") RequestBody Alamat,
            @Part("tempat_kerja") RequestBody Tempat_Kerja,
            @Part("bagian") RequestBody Bagian,
            @Part("tanggal") RequestBody Tanggal,
            @Part("alasan") RequestBody Alasan,
            @Part MultipartBody.Part file
    );

//    @FormUrlEncoded
//    @POST("surat/sktm.php")
//    Call<ResponSktm> sktm(
//            @Field("username") String username,
//            //bapak
//            @Field("nama_bapak") String nama_bapak,
//            @Field("tempat_tanggal_lahir_bapak") String tempat_tanggal_lahir_bapak,
//            @Field("pekerjaan_bapak") String pekerjaan_bapak,
//            @Field("alamat_bapak") String alamat_bapak,
//
//            //ibu
//            @Field("nama_ibu") String nama_ibu,
//            @Field("tempat_tanggal_lahir_ibu") String tempat_tanggal_lahir_ibu,
//            @Field("pekerjaan_ibu") String pekerjaan_ibu,
//            @Field("alamat_ibu") String alamat_ibu,
//
//            //anak
//            @Field("nama") String nama_anak,
//            @Field("tempat_tanggal_lahir_anak") String tempat_tanggal_lahir_anak,
//            @Field("jenis_kelamin_anak") String jenis_kelamin_anak,
//            @Field("alamat") String alamat_anak
//
//    );

    @Multipart
    @POST("surat/sktm.php")
    Call<ResponSktm> sktm(
            @Part("username") RequestBody username,

            //bapak
            @Part("nama_bapak") RequestBody nama_bapak,
            @Part("tempat_tanggal_lahir_bapak") RequestBody tempat_tanggal_lahir_bapak,
            @Part("pekerjaan_bapak") RequestBody pekerjaan_bapak,
            @Part("alamat_bapak") RequestBody alamat_bapak,

            //ibu
            @Part("nama_ibu") RequestBody nama_ibu,
            @Part("tempat_tanggal_lahir_ibu") RequestBody tempat_tanggal_lahir_ibu,
            @Part("pekerjaan_ibu") RequestBody pekerjaan_ibu,
            @Part("alamat_ibu") RequestBody alamat_ibu,

            //anak
            @Part("nama") RequestBody nama_anak,
            @Part("tempat_tanggal_lahir_anak") RequestBody tempat_tanggal_lahir_anak,
            @Part("jenis_kelamin_anak") RequestBody jenis_kelamin_anak,
            @Part("alamat") RequestBody alamat,
            @Part MultipartBody.Part file
    );

    @FormUrlEncoded
    @POST("update_akun.php")
    Call<ResponUpdate> update_akun(
            @Field("username") String username,
            @Field("email") String email,
            @Field("password") String password,
            @Field("nama") String nama
    );

    @Multipart
    @POST("update_akun.php")
    Call<ResponUpdate> updateAkunWithImage(
            @Part("username") RequestBody username,
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("nama") RequestBody nama,
            @Part MultipartBody.Part profile_image // Menambahkan bagian gambar
    );

    @FormUrlEncoded
    @POST("dashboard.php")
    Call<StatusDasboardRespon> dashboard(
            @Field("username") String usernmae
    );

    @FormUrlEncoded
    @POST("logingoogle.php")
    Call<ResponLogin> logingoogle(
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("updatesurat/skck.php")
    Call<ResponSkck> ambilskck(
            @Field("no_pengajuan") String no,
            @Field("kode") String kode
    );
    @FormUrlEncoded
    @POST("updatesurat/skck.php")
    Call<ResponSkck> updateskck(
            @Field("no_pengajuan") String no,
            @Field("kode") String kode,
            @Field("nama") String nama,
            @Field("nik") String nik,
            @Field("tempat_tanggal_lahir") String tempat_tanggal_lahir,
            @Field("kebangsaan") String kebangsaan,
            @Field("agama") String agama,
            @Field("status_perkawinan") String status,
            @Field("pekerjaan") String pekerjaan,
            @Field("tempat_tinggal") String tinggal,
            @Field("jenis_kelamin") String jenis_kelamin
    );

    @FormUrlEncoded
    @POST("updatesurat/sktm.php")
    Call<ResponSktm> ambilsktm(
            @Field("no_pengajuan") String no,
            @Field("kode") String kode
    );
    @FormUrlEncoded
    @POST("updatesurat/sktm.php")
    Call<ResponSktm> updatesktm(
            @Field("no_pengajuan") String no,
            @Field("kode") String kode,

            //bapak
            @Field("nama_bapak") String nama_bapak,
            @Field("tempat_tanggal_lahir_bapak") String tempat_tanggal_lahir_bapak,
            @Field("pekerjaan_bapak") String pekerjaan_bapak,
            @Field("alamat_bapak") String alamat_bapak,

            //ibu
            @Field("nama_ibu") String nama_ibu,
            @Field("tempat_tanggal_lahir_ibu") String tempat_tanggal_lahir_ibu,
            @Field("pekerjaan_ibu") String pekerjaan_ibu,
            @Field("alamat_ibu") String alamat_ibu,

            //anak
            @Field("nama") String nama_anak,
            @Field("tempat_tanggal_lahir_anak") String tempat_tanggal_lahir_anak,
            @Field("jenis_kelamin_anak") String jenis_kelamin_anak,
            @Field("alamat") String alamat_anak
    );

    @FormUrlEncoded
    @POST("updatesurat/surat_ijin.php")
    Call<ResponSuratijin> ambilsuratijin(
            @Field("no_pengajuan") String no,
            @Field("kode") String kode
    );
    @FormUrlEncoded
    @POST("updatesurat/surat_ijin.php")
    Call<ResponSuratijin> updatesuratijin(
            @Field("no_pengajuan") String no,
            @Field("kode") String kode,
            @Field("nama") String Nama,
            @Field("nik") String NIK,
            @Field("jenis_kelamin") String Jenis_kelamin,
            @Field("tempat_tanggal_lahir") String Tempat_tanggal_lahir,
            @Field("kewarganegaraan") String Kewarganegaraan,
            @Field("agama") String Agama,
            @Field("pekerjaan") String Pekerjaan,
            @Field("alamat") String Alamat,
            @Field("tempat_Kerja") String Tempat_Kerja,
            @Field("bagian") String Bagian,
            @Field("tanggal") String Tanggal,
            @Field("alasan") String Alasan
    );

    @FormUrlEncoded
    @POST("notifikasi.php")
    Call<ResponNotifikasi> notif(
            @Field("username") String username
    );
    @FormUrlEncoded
    @POST("notifikasi_popup.php")
    Call<ResponNotifikasi> notifikasi_popup(
            @Field("username") String username
    );
};