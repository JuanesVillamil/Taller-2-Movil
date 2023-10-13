package com.puj.taller02_permission.services;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import com.puj.taller02_permission.utils.DistanceUtils;

public class GeocoderService {
    private static final String TAG = GeocoderService.class.getName();

    private static final int MAX_RESULTS = 50;
    private static final double DISTANCE_RADIUS_KM = 5.0d;
    private final Context context;
    private final Geocoder geocoder;

    public GeocoderService(Context context) {
        this.context = context;
        this.geocoder = new Geocoder(context);
    }

    public List<Address> findPlacesByName(String name) throws IOException {
        return geocoder.getFromLocationName(name, MAX_RESULTS);
    }

    public List<Address> finPlacesByNameInRadious(String name, LatLng centerPosition) throws IOException {
        LatLng upperLeftPosition = DistanceUtils.moveLatLngInKilometer(-DISTANCE_RADIUS_KM, -DISTANCE_RADIUS_KM, centerPosition);
        LatLng bottomRightPosition = DistanceUtils.moveLatLngInKilometer(DISTANCE_RADIUS_KM, DISTANCE_RADIUS_KM, centerPosition);
        return geocoder.getFromLocationName(name, MAX_RESULTS, upperLeftPosition.latitude, upperLeftPosition.longitude, bottomRightPosition.latitude, bottomRightPosition.longitude);
    }
}