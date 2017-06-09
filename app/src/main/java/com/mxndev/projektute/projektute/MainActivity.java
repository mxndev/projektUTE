package com.mxndev.projektute.projektute;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.google.gson.*;
import com.mxndev.projektute.projektute.Interfaces.*;
import com.mxndev.projektute.projektute.Models.*;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String BASE_URL_ORANGE = "https://apitest.orange.pl";
    public static final String BASE_URL_GOOGLE = "https://maps.googleapis.com";
    public static final String TELEPHONE_NUMBER = "48500667905";
    public static final String API_KEY_ORANGE = "qr1d7R3Ag3gop06s1bzRuySh7fxukfSA";
    public static final String API_KEY_GOOGLE = " AIzaSyCTLCNDK0QllsYzOHtd7f-4UXRUov0w_o0";
    Retrofit orangeRetrofit, googleRetrofit;
    String stringLatitude, stringLongitude;

    @BindView(R.id.latitudeTextView)
    TextView latitude;

    @BindView(R.id.longitudeTextView)
    TextView longitude;

    @BindView(R.id.countGalleries)
    TextView countGalleries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        orangeRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_ORANGE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        googleRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_GOOGLE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        getLatLongFromGeoLocalization(null);
    }

    void getLatLongFromGeoLocalization(View view)
    {
        runOnUiThread (new Thread(new Runnable() {
            public void run() {
                latitude.setText("Szerokość geograficzna: ładowanie...");
                longitude.setText("Długość geograficzna: ładowanie...");
                countGalleries.setText("...");
            }
        }));
        OrangeAPIInterface apiService = orangeRetrofit.create(OrangeAPIInterface.class);
        Call<GeoLocation> call = apiService.getLocation(TELEPHONE_NUMBER, API_KEY_ORANGE);
        call.enqueue(new Callback<GeoLocation>() {
            @Override
            public void onResponse(Call<GeoLocation> call, final Response<GeoLocation> response) {
                runOnUiThread (new Thread(new Runnable() {
                    public void run() {
                        latitude.setText("Szerokość geograficzna: " + response.body().getLatitude());
                        longitude.setText("Długość geograficzna: " + response.body().getLongitude());

                        if(response.body().getLatitude().toString().contains("N"))
                        {
                            stringLatitude = response.body().getLatitude().toString().substring(0,  response.body().getLatitude().toString().length() - 2);
                        }
                        else if(response.body().getLatitude().toString().contains("S"))
                        {
                            stringLatitude = "-"+ response.body().getLatitude().toString().substring(0,  response.body().getLatitude().toString().length() - 2);
                        }

                        if(response.body().getLongitude().toString().contains("E"))
                        {
                            stringLongitude = response.body().getLongitude().toString().substring(0,  response.body().getLongitude().toString().length() - 2);
                        }
                        else if(response.body().getLongitude().toString().contains("W"))
                        {
                            stringLongitude = "-"+ response.body().getLongitude().toString().substring(0,  response.body().getLongitude().toString().length() - 2);
                        }
                    }
                }));
                geDataArtGalleries(null);
            }

            @Override
            public void onFailure(Call<GeoLocation> call, Throwable t) {
            }
        });
    }

    void geDataArtGalleries(View view)
    {
        GooglePlacesAPIInterface apiService = googleRetrofit.create(GooglePlacesAPIInterface.class);
        Call<NearbyPlacesList> call = apiService.getArtGalleries(stringLatitude + "," + stringLongitude, "3000", "art_gallery", API_KEY_GOOGLE);
        call.enqueue(new Callback<NearbyPlacesList>() {
            @Override
            public void onResponse(Call<NearbyPlacesList> call, final Response<NearbyPlacesList> response) {
                runOnUiThread (new Thread(new Runnable() {
                    public void run() {
                        countGalleries.setText(Integer.toString(response.body().getResults().size()));
                    }
                }));
            }

            @Override
            public void onFailure(Call<NearbyPlacesList> call, Throwable t) {
            }
        });
    }
}
