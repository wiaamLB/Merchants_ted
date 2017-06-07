package com.tedmob.merchants.request;


import com.tedmob.merchants.model.APIResponseJSON;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


/**
 * Created by wiaam on 06-Jun-17.
 */
public interface MerchantService {
    String SERVICE_ENDPOINT = "https://api.mybookqatar.com";

    @POST("/merchants/get_merchants")
    @FormUrlEncoded
    Call<APIResponseJSON> getMerchants(@Field("page") int page, @Field("rows") int rows, @Field("category_id") int category_id);
}