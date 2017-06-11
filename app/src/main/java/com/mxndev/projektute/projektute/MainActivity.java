package com.mxndev.projektute.projektute;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.mxndev.projektute.projektute.Interfaces.*;
import com.mxndev.projektute.projektute.Models.*;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String BASE_URL_ORANGE = "https://apitest.orange.pl";
    public static final String BASE_URL_GOOGLE = "https://maps.googleapis.com";
    public static final String TELEPHONE_NUMBER = "48500667905";
    public static final String API_KEY_ORANGE = "qr1d7R3Ag3gop06s1bzRuySh7fxukfSA";
    public static final String API_KEY_GOOGLE = " AIzaSyCTLCNDK0QllsYzOHtd7f-4UXRUov0w_o0";
    Retrofit orangeRetrofit, googleRetrofit;
    String stringLatitude, stringLongitude;
    GoogleMap mMap;
    ArrayList<Marker> googleMapMarkers;
    ArrayList<NearbyPlaces> placesList;

    @BindView(R.id.latitudeTextView)
    TextView latitude;

    @BindView(R.id.longitudeTextView)
    TextView longitude;

    @BindView(R.id.countGalleries)
    TextView countGalleries;

    @BindView(R.id.countMuseum)
    TextView countMuseum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        placesList = new ArrayList<>();
        googleMapMarkers = new ArrayList<>();

        orangeRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_ORANGE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        googleRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_GOOGLE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getLatLongFromGeoLocalization(null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;
    }

    void getLatLongFromGeoLocalization(View view)
    {
        runOnUiThread (new Thread(new Runnable() {
            public void run() {
                latitude.setText("Szerokość geograficzna: ładowanie...");
                longitude.setText("Długość geograficzna: ładowanie...");
                countGalleries.setText("...");
                countMuseum.setText("...");
            }
        }));
        OrangeAPIInterface apiService = orangeRetrofit.create(OrangeAPIInterface.class);
        Call<GeoLocation> call = apiService.getLocation(TELEPHONE_NUMBER, API_KEY_ORANGE);
        call.enqueue(new Callback<GeoLocation>() {
            @Override
            public void onResponse(Call<GeoLocation> call, final Response<GeoLocation> response) {
                runOnUiThread (new Thread(new Runnable() {
                    public void run() {
                        latitude.setText("52.222556");//"Szerokość geograficzna: " + response.body().getLatitude());
                        longitude.setText("21.016731");//"Długość geograficzna: " + response.body().getLongitude());
                    }
                }));

                /*if(response.body().getLatitude().toString().contains("N"))
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
                }*/
                stringLatitude = "52.222556";
                stringLongitude = "21.016731";

                LatLng center = new LatLng(Double.parseDouble(stringLatitude),Double.parseDouble(stringLongitude));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 13.5f));
                mMap.addMarker(new MarkerOptions().position(center).title("Nasza pozycja"));

                placesList.clear();
                geDataArtGalleries(null);
                getDataMuseum(null);
            }

            @Override
            public void onFailure(Call<GeoLocation> call, Throwable t) {
            }
        });
    }

    void geDataArtGalleries(View view)
    {
        GooglePlacesAPIInterface apiService = googleRetrofit.create(GooglePlacesAPIInterface.class);
        Call<NearbyPlacesList> call = apiService.getPlacesByType(stringLatitude + "," + stringLongitude, "1000", "art_gallery", API_KEY_GOOGLE);
        call.enqueue(new Callback<NearbyPlacesList>() {
            @Override
            public void onResponse(Call<NearbyPlacesList> call, final Response<NearbyPlacesList> response) {
                runOnUiThread (new Thread(new Runnable() {
                    public void run() {
                        countGalleries.setText(Integer.toString(response.body().getResults().size()));
                        if(response.body().getResults() != null){
                            placesList.addAll(response.body().getResults());
                        }
                        showMarksOnMap();

                    }
                }));
            }

            @Override
            public void onFailure(Call<NearbyPlacesList> call, Throwable t) {
                int fdsf = 0;
            }
        });
    }

    void getDataMuseum(View view)
    {
        GooglePlacesAPIInterface apiService = googleRetrofit.create(GooglePlacesAPIInterface.class);
        Call<NearbyPlacesList> call = apiService.getPlacesByType(stringLatitude + "," + stringLongitude, "1000", "museum", API_KEY_GOOGLE);
        call.enqueue(new Callback<NearbyPlacesList>() {
            @Override
            public void onResponse(Call<NearbyPlacesList> call, final Response<NearbyPlacesList> response) {
                runOnUiThread (new Thread(new Runnable() {
                    public void run() {
                        countMuseum.setText(Integer.toString(response.body().getResults().size()));
                        if(response.body().getResults() != null) {
                            placesList.addAll(response.body().getResults());
                        }
                        showMarksOnMap();
                    }
                }));
            }

            @Override
            public void onFailure(Call<NearbyPlacesList> call, Throwable t) {
            }
        });
    }

    void showMarksOnMap()
    {
        for(Marker marker : googleMapMarkers)
        {
            marker.remove();
        }
        for(NearbyPlaces placeMarker : placesList)
        {
            LatLng center = new LatLng(Double.parseDouble(placeMarker.getGeometry().getLocation().getLat()),Double.parseDouble(placeMarker.getGeometry().getLocation().getLng()));
            googleMapMarkers.add(mMap.addMarker(new MarkerOptions().position(center).title(placeMarker.getName())));
        }
    }
}
