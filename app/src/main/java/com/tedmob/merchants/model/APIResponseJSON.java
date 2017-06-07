package com.tedmob.merchants.model;


import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by wiaam on 06-Jun-17.
 */

public class APIResponseJSON {



    @SerializedName("merchants")
    public  List<Merchant> message;


    public APIResponseJSON( List<Merchant> message) {
        this.message = message;
    }


}