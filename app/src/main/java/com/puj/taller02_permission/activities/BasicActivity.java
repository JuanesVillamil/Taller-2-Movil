package com.puj.taller02_permission.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;

import com.puj.taller02_permission.App;
import com.puj.taller02_permission.models.GeoInfo;
import com.puj.taller02_permission.services.GeoInfoFromJsonService;
import com.puj.taller02_permission.services.GeocoderService;
import com.puj.taller02_permission.services.LocationService;
import com.puj.taller02_permission.utils.PermissionHelper;
import com.puj.taller02_permission.utils.AlertUtils;

public class BasicActivity extends AppCompatActivity {

    @Inject
    LocationService locationService;

    @Inject
    GeocoderService geocoderService;

    @Inject
    GeoInfoFromJsonService geoInfoFromJsonService;

    @Inject
    PermissionHelper permissionHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ((App) getApplicationContext()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
    }
}