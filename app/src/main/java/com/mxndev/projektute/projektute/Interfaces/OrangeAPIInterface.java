package com.mxndev.projektute.projektute.Interfaces;

import com.mxndev.projektute.projektute.Models.GeoLocation;

import retrofit2.*;
import retrofit2.http.*;

/**
 * Created by mixxe on 08.06.2017.
 */

public interface OrangeAPIInterface
{
    @GET("/Localization/v1/GeoLocation")
    Call<GeoLocation> getLocation(@Query("msisdn") String number, @Query("apikey") String api);
}
