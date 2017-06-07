package com.tedmob.merchants.model;

import com.google.gson.annotations.SerializedName;

import java.net.URL;
import java.util.List;

/**
 * Created by wiaam on 06-Jun-17.
 */

public class Merchant {


    @SerializedName("image_paths")
    public List<URL> image_paths;

    @SerializedName("logo_path")
    public URL logo_path;

    @SerializedName("title")
    public String title;

    @SerializedName("contact_number")
    public String contact_number;

    @SerializedName("contact_email")
    public String contact_email;

    public Merchant(List<URL> image_paths, URL logo_path, String title,String contact_number,String contact_email) {
        this.image_paths=image_paths;
        this.logo_path=logo_path;
        this.title=title;
        this.contact_number=contact_number;
        this.contact_email=contact_email;
    }

    public List<URL> getImage_paths() {
        return image_paths;
    }

    public URL getLogo_path() {
        return logo_path;
    }

    public String getTitle() {
        return title;
    }

    public String getContact_number() {
        return contact_number;
    }

    public String getContact_email() {
        return contact_email;
    }
}
