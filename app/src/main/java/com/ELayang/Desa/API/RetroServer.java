package com.ELayang.Desa.API;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroServer {
    public static String server = "192.168.137.10";
//    private static final String baseURL = "http://" + server + "/coding/ELaDes%20WEB/DatabaseMobile/";
    private static final String finalurl = "http://" + server + "/elades/DatabaseMobile/";
    public static final String API_IMAGE ="http://" + server + "/elades/uploads/";
    private static Retrofit retro;
//    public static final String API_IMAGE = finalurl + "/elades/uploads/";
    public String getUrlImage(){
        return API_IMAGE;
    }
    public static Retrofit konekRetrofit() {
        if (retro == null) {  

            // Tambahkan logging interceptor
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            // Inisialisasi Retrofit dengan interceptor
            retro = new Retrofit.Builder()
                    .baseUrl(finalurl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retro;
    }
}
