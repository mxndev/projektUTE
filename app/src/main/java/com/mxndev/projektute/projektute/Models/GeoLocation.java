package com.mxndev.projektute.projektute.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mixxe on 08.06.2017.
 */

public class GeoLocation
{
    private String latitude;
    private String longitude;
    private String radius;

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getRadius() {
        return radius;
    }

    public GeoLocation(String latitude, String longitude, String radius)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }
}
