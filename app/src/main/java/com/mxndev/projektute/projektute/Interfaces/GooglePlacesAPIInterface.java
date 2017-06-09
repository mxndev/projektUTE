package com.mxndev.projektute.projektute.Interfaces;

import com.mxndev.projektute.projektute.Models.*;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by mixxe on 09.06.2017.
 */

public interface GooglePlacesAPIInterface
{
    @GET("/maps/api/place/nearbysearch/json")
    Call<NearbyPlacesList> getArtGalleries(@Query("location") String location, @Query("radius") String radius, @Query("types") String type, @Query("key") String apikey);
}
