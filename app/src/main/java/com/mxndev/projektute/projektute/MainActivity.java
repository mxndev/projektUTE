package com.mxndev.projektute.projektute;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.mxndev.projektute.projektute.Interfaces.OrangeAPIInterface;
import com.mxndev.projektute.projektute.Models.GeoLocation;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String BASE_URL = "https://apitest.orange.pl/";
    public static final String TELEPHONE_NUMBER = "48500667905";
    public static final String API_KEY = "qr1d7R3Ag3gop06s1bzRuySh7fxukfSA";
    Retrofit retrofit;

    @BindView(R.id.latitudeTextView)
    TextView latitude;

    @BindView(R.id.longitudeTextView)
    TextView longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
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
            }
        }));
        OrangeAPIInterface apiService = retrofit.create(OrangeAPIInterface.class);
        Call<GeoLocation> call = apiService.getLocation(TELEPHONE_NUMBER, API_KEY);
        call.enqueue(new Callback<GeoLocation>() {
            @Override
            public void onResponse(Call<GeoLocation> call, final Response<GeoLocation> response) {
                runOnUiThread (new Thread(new Runnable() {
                    public void run() {
                        latitude.setText("Szerokość geograficzna: " + response.body().getLatitude());
                        longitude.setText("Długość geograficzna: " + response.body().getLongitude());
                    }
                }));
            }

            @Override
            public void onFailure(Call<GeoLocation> call, Throwable t) {
            }
        });
    }
}
