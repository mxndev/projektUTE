package com.mxndev.projektute.projektute.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mixxe on 08.06.2017.
 */

public class GeoLocation {

    @SerializedName("msisdn")
    String mMsisdn;

    public String getMsisdn() {
        return mMsisdn;
    }

    public String getResult() {
        return mResult;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public String getRadius() {
        return mRadius;
    }

    public String getDistanceUnit() {
        return mDistanceUnit;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

    public String getTimezone() {
        return mTimezone;
    }

    @SerializedName("result")
    String mResult;

    @SerializedName("latitude")
    String mLatitude;

    @SerializedName("longitude")
    String mLongitude;

    @SerializedName("radius")
    String mRadius;

    @SerializedName("distanceUnit")
    String mDistanceUnit;

    @SerializedName("timestamp")
    String mTimestamp;

    @SerializedName("timezone")
    String mTimezone;

    public GeoLocation(String msisdn, String result, String latitude, String longitude, String radius, String distanceUnit, String timestamp, String timezone)
    {
        this.mMsisdn = msisdn;
        this.mResult = result;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mRadius = radius;
        this.mDistanceUnit = distanceUnit;
        this.mTimestamp = timestamp;
        this.mTimezone = timezone;
    }
}
