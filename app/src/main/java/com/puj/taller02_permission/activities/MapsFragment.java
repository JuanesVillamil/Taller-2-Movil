package com.puj.taller02_permission.activities;

import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.maps.android.PolyUtil;
import com.puj.taller02_permission.App;
import com.puj.taller02_permission.GMapsDirectionAPI;
import com.puj.taller02_permission.models.Direction;
import com.puj.taller02_permission.models.Poly;
import com.puj.taller02_permission.models.Step;
import com.puj.taller02_permission.utils.BitmapUtils;
import com.puj.taller02_permission.services.GeocoderService;
import com.puj.taller02_permission.services.GeoInfoFromJsonService;
import com.puj.taller02_permission.R;
import com.puj.taller02_permission.databinding.FragmentMapsBinding;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener{

    @Inject
    GeocoderService geocoderService;

    FragmentMapsBinding binding;
    @Inject
    GeoInfoFromJsonService geoInfoFromJsonService;
    //Map interaction variables
    GoogleMap googleMap;
    static final int INITIAL_ZOOM_LEVEL = 18;
    private final LatLng BOGOTA = new LatLng(4.63062, -74.065527);
    Marker userPosition;
    Polyline userRoute;

    //Light sensor variables
    final static float LIGHT_LIMIT = 1500.0f;
    SensorManager sensorManager;
    Sensor lightSensor;
    SensorEventListener lightSensorEventListener;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {


        @Override
        public void onMapReady(GoogleMap mMap) {
            googleMap = mMap;
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(INITIAL_ZOOM_LEVEL));
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            // Change map style
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_day_style));
            userPosition = googleMap.addMarker(new MarkerOptions()
                    .position(BOGOTA)
                    .icon(BitmapUtils.getBitmapDescriptor(getContext(), R.drawable.icon_current_position))
                    .anchor(0.5f, 0.5f)
                    .zIndex(1.0f));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BOGOTA,10));
            //Setup the route line
            userRoute = googleMap.addPolyline(new PolylineOptions()
                    .color(R.color.light_blue_400)
                    .width(15.0f)
                    .geodesic(true)
                    .zIndex(0.5f));
            loadGeoInfo();

            googleMap.setOnMapLongClickListener(onLongClickMapSettins());

        }
    };

    private GoogleMap.OnMapLongClickListener onLongClickMapSettins() {
        return new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                googleMap.addMarker(new MarkerOptions()
                        .position(latLng));
                try {
                    final LatLng[] newPosition = new LatLng[1];
                    List<Address> results = geocoderService.finPlacesByNameInRadious("marcador",latLng);
                    results.forEach(address -> googleMap.addMarker(new MarkerOptions()
                            .icon(BitmapUtils.getBitmapDescriptor(getContext(), R.drawable.icon_position_find))
                            .position(newPosition[0] = new LatLng(address.getLatitude(), address.getLongitude()))
                            .title(address.getAddressLine(0))
                            .snippet(address.getAddressLine(0) != null ? address.getAddressLine(0) : ""))
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((App) requireActivity().getApplicationContext()).getAppComponent().inject(this);
        binding = FragmentMapsBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lightSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(@NotNull SensorEvent sensorEvent) {
                if (googleMap != null) {
                    if (sensorEvent.values[0] < LIGHT_LIMIT)
                        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(),R.raw.map_night_style));
                    //userRoute.setColor(R.color.light_blue_400);
                        //googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_night_style));
                    else
                        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(),R.raw.map_day_style));
                    //           .setMapStyle
                    //userRoute.setColor(R.color.light_blue_100);
                        //googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_day_style));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        binding.textInput.setOnClickListener(view1 -> findPlaces(binding.filledTextField.getEditText().getText().toString()));
        binding.filledTextField.getEditText().setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        binding.filledTextField.getEditText().setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_SEARCH){
                findPlaces(binding.filledTextField.getEditText().getText().toString());
                return true;
            }
            return false;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        sensorManager.registerListener(lightSensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onStop() {
        super.onStop();
        List<LatLng> points = userRoute.getPoints();
        points.clear();
        userRoute.setPoints(points);
        sensorManager.unregisterListener(lightSensorEventListener);
    }

    public void updateUserPositionOnMap(@NotNull LocationResult locationResult) {
        userPosition.setPosition(new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()));
        List<LatLng> points = userRoute.getPoints();
        points.add(userPosition.getPosition());
        userRoute.setPoints(points);
        if (binding.isCameraFixedToUser.isChecked()) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userPosition.getPosition(),16));
            //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userPosition.getPosition(), INITIAL_ZOOM_LEVEL));
        }
    }

    private void loadGeoInfo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            geoInfoFromJsonService.getGeoInfoList().forEach(geoInfo -> {
                MarkerOptions newMarker = new MarkerOptions();
                newMarker.position(new LatLng(geoInfo.getLat(), geoInfo.getLng()));
                newMarker.title(geoInfo.getTitle());
                newMarker.snippet(geoInfo.getContent());
                if (geoInfo.getImageBase64() != null) {
                    byte[] pinImage = Base64.decode(geoInfo.getImageBase64(), Base64.DEFAULT);
                    Bitmap decodedPin = BitmapFactory.decodeByteArray(pinImage, 0, pinImage.length);
                    Bitmap smallPin = Bitmap.createScaledBitmap(decodedPin, 200, 200, false);
                    newMarker.icon(BitmapDescriptorFactory.fromBitmap(smallPin));
                }
                googleMap.addMarker(newMarker);
            });
        }
    }
    public void findPlaces(String search) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                final LatLng[] newPosition = new LatLng[1];
                //List<Address> results = geocoderService.finPlacesByNameInRadious(search, userPosition.getPosition());
                List<Address> results = geocoderService.findPlacesByName(search);
                Log.d("TAG", "findPlaces: results = " + results.size());
                results.forEach(address -> googleMap.addMarker(new MarkerOptions()
                        .icon(BitmapUtils.getBitmapDescriptor(getContext(), R.drawable.icon_position_find))
                        .position(newPosition[0] = new LatLng(address.getLatitude(), address.getLongitude()))
                        .title(address.getAddressLine(0))
                        .snippet(address.getAddressLine(0) != null ? address.getAddressLine(0) : ""))
                );
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPosition[0],16));
                //callDirectionsAPI(newPosition[0].toString());
//                googleMap.addPolyline(new PolylineOptions()
//                        .add(userPosition.getPosition(),newPosition[0])
//                        .width(5)
//                        .color(R.color.blue_100)
//                );
                //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userPosition.getPosition(),16));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void callDirectionsAPI(String destino) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GMapsDirectionAPI api = retrofit.create(GMapsDirectionAPI.class);

        Call<Direction> call = api.getDirection(String.valueOf(userPosition.getPosition()),destino,"AIzaSyAoQL0aSH8DfaDg7GyiwGEaJi6EcARlGXI");

        call.enqueue(new Callback<Direction>() {
            @Override
            public void onResponse(Call<Direction> call, Response<Direction> response) {
                boolean setMarkerFirstTime = false;
                for(Step step: response.body().getRoutes().get(0).getLegs().get(0).getSteps()){
                    Poly polyline = step.getPolyline();

                    List<LatLng> points = PolyUtil.decode(polyline.getPoints());
                    googleMap.addPolyline(new PolylineOptions().addAll(points).width(5).color(R.color.blue_100));
                    if(!setMarkerFirstTime){
                        googleMap.addMarker(new MarkerOptions().position(points.get(0)));
                        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(points.get(0),13));
                        setMarkerFirstTime=true;
                    }
                }
            }

            @Override
            public void onFailure(Call<Direction> call, Throwable t) {
                Log.e("error",t.getMessage());
            }
        });
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

    }
}