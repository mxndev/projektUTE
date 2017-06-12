package com.mxndev.projektute.projektute.Interfaces;

import com.mxndev.projektute.projektute.Models.*;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by mixxe on 12.06.2017.
 */

public interface WarsawApiInterface {

    @GET("/api/action/wfsstore_get")
    Call<WarsawResult> getPlacesByType(@Query("id") String id, @Query("circle") String circle, @Query("apikey") String apikey);
}
