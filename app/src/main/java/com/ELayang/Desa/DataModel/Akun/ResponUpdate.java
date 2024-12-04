package com.ELayang.Desa.DataModel.Akun;

import com.google.gson.annotations.SerializedName;

public class ResponUpdate {
    @SerializedName("kode")
    private boolean kode;
    @SerializedName("pesan")
    private String pesan;
    private String profile_image;
    // Constructor default
    public ResponUpdate() {}

    // Constructor untuk mempermudah instansiasi
    public ResponUpdate(boolean kode, String pesan) {
        this.kode = kode;
        this.pesan = pesan;
    }


    public boolean isKode() {
        return kode;
    }

    public void setKode(boolean kode) {
        this.kode = kode;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }
    @Override
    public String toString() {
        return "ResponUpdate{" +
                "kode=" + kode +
                ", pesan='" + pesan + '\'' +
                '}';
    }
    // Getter dan Setter
    public String getProfileImage() {
        return profile_image;
    }

    public void setProfileImage(String profile_image) {
        this.profile_image = profile_image;
    }
}
