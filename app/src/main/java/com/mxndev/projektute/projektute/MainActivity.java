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
    public static final String BASE_URL_WARSAW = "https://api.um.warszawa.pl";
    public static final String TELEPHONE_NUMBER = "48500667905";
    public static final String API_KEY_ORANGE = "qr1d7R3Ag3gop06s1bzRuySh7fxukfSA";
    public static final String API_KEY_GOOGLE = " AIzaSyCTLCNDK0QllsYzOHtd7f-4UXRUov0w_o0";
    public static final String API_WARSAW = "00459014-f14c-455d-b226-f6ddd96d5a9c";
    public static final String ID_WARSAW = "e26218cb-61ec-4ccb-81cc-fd19a6fee0f8";
    Retrofit orangeRetrofit, googleRetrofit, warsawRetrofit;
    String stringLatitude, stringLongitude;
    GoogleMap mMap;
    ArrayList<Marker> googleMapMarkers;
    ArrayList<PlacesBase> placesList;
    ArrayList<String> listItems;
    ArrayAdapter<String> adapter;
    int circle = 1000;

    @BindView(R.id.latitudeTextView)
    TextView latitude;

    @BindView(R.id.longitudeTextView)
    TextView longitude;

    @BindView(R.id.countGalleries)
    TextView countGalleries;

    @BindView(R.id.countThreaters)
    TextView countTheatres;

    @BindView(R.id.placesListView)
    ListView placesListView;

    @BindView(R.id.seekBarTextView)
    TextView seekBarTextView;

    @BindView(R.id.placesListViewLayout)
    FrameLayout placesListViewLayout;

    @BindView(R.id.switchGallery)
    Switch switchGallery;

    @BindView(R.id.switchThreaters)
    Switch switchThreaters;

    @BindView(R.id.seekBarCircle)
    SeekBar seekBarCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        listItems = new ArrayList<String>();

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

        warsawRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_WARSAW)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        placesListView.setAdapter(adapter);

        switchGallery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showMarksOnMap();
                refreshPlacesList();
            }
        });
        switchThreaters.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showMarksOnMap();
                refreshPlacesList();
            }
        });

        seekBarCircle.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                circle = (progress + 1)*1000;
                seekBarTextView.setText((progress + 1)+" km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

        });

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
                countTheatres.setText("...");
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
                    }
                }));

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
                //stringLatitude = "52.222556";
                //stringLongitude = "21.016731";

                LatLng center = new LatLng(Double.parseDouble(stringLatitude),Double.parseDouble(stringLongitude));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 13.5f - (0.37f*(circle / 1000))));
                mMap.addMarker(new MarkerOptions().position(center).title("Nasza pozycja"));

                placesList.clear();
                getDataArtGalleries();
                getDataTheatres();
            }

            @Override
            public void onFailure(Call<GeoLocation> call, Throwable t) {
            }
        });
    }

    void getDataArtGalleries()
    {
        GooglePlacesAPIInterface apiService = googleRetrofit.create(GooglePlacesAPIInterface.class);
        Call<NearbyPlacesList> call = apiService.getPlacesByType(stringLatitude + "," + stringLongitude, Integer.toString(circle), "art_gallery", API_KEY_GOOGLE);
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
                        refreshPlacesList();

                    }
                }));
            }

            @Override
            public void onFailure(Call<NearbyPlacesList> call, Throwable t) {
            }
        });
    }

    void getDataTheatres()
    {
        WarsawApiInterface apiService = warsawRetrofit.create(WarsawApiInterface.class);
        Call<WarsawResult> call = apiService.getPlacesByType(ID_WARSAW, stringLongitude + "," + stringLatitude + "," + Integer.toString(circle), API_WARSAW);
        call.enqueue(new Callback<WarsawResult>() {
            @Override
            public void onResponse(Call<WarsawResult> call, final Response<WarsawResult> response) {
                runOnUiThread (new Thread(new Runnable() {
                    public void run() {
                        countTheatres.setText(Integer.toString(response.body().getResult().getFeatureMemberList().size()));
                        if(response.body().getResult() != null) {
                            placesList.addAll(response.body().getResult().getFeatureMemberList());
                        }
                        showMarksOnMap();
                        refreshPlacesList();
                    }
                }));
            }

            @Override
            public void onFailure(Call<WarsawResult> call, Throwable t) {
                int i = 0;
            }
        });
    }

    void showMarksOnMap()
    {
        for(Marker marker : googleMapMarkers)
        {
            marker.remove();
        }
        for(PlacesBase placeMarker : placesList)
        {
            if(placeMarker instanceof NearbyPlaces )
            {
                if(switchGallery.isChecked()) {
                    LatLng center = new LatLng(Double.parseDouble(((NearbyPlaces) placeMarker).getGeometry().getLocation().getLat()), Double.parseDouble(((NearbyPlaces) placeMarker).getGeometry().getLocation().getLng()));
                    googleMapMarkers.add(mMap.addMarker(new MarkerOptions().position(center).title(((NearbyPlaces) placeMarker).getName())));
                }
            }
            else if (placeMarker instanceof WarsawFeatureList){
                if(switchThreaters.isChecked()) {
                    LatLng center = new LatLng(Double.parseDouble(((WarsawFeatureList) placeMarker).getGeometry().getCoordinates().get(0).getLatidute()),
                            Double.parseDouble(((WarsawFeatureList) placeMarker).getGeometry().getCoordinates().get(0).getLongidute()));
                    googleMapMarkers.add(mMap.addMarker(new MarkerOptions().position(center).title(getOrangeElementName(((WarsawFeatureList) placeMarker)))));
                }
            }

        }
    }

    void refreshPlacesList()
    {
        listItems.clear();
        for(PlacesBase placeMarker : placesList)
        {
            if(placeMarker instanceof NearbyPlaces )
            {
                if(switchGallery.isChecked()) {
                    listItems.add(((NearbyPlaces) placeMarker).getName());
                }
            }
            else if (placeMarker instanceof WarsawFeatureList)
            {
                if(switchThreaters.isChecked()) {
                    listItems.add(getOrangeElementName((WarsawFeatureList) placeMarker));
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    void showHideListOfPlaces(View view)
    {
        runOnUiThread (new Thread(new Runnable() {
            public void run() {
                if(placesListViewLayout.getVisibility() == View.VISIBLE)
                {
                    placesListViewLayout.setVisibility(View.GONE);
                }
                else if(placesListViewLayout.getVisibility() == View.GONE)
                {
                    placesListViewLayout.setVisibility(View.VISIBLE);
                }
            }
        }));
    }

    String getOrangeElementName(WarsawFeatureList warsawFeatureList){

        ArrayList<WarsawProperties> warsawProperties = new ArrayList<>();

        warsawProperties = warsawFeatureList.getProperties();
        for(WarsawProperties warsawProperty : warsawProperties){
            if(warsawProperty.getKey().equals("OPIS")){
                return warsawProperty.getValue();
            }
        }

        return "Teatr";
    }
}
