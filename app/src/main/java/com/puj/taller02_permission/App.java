package com.puj.taller02_permission;

import android.app.Application;

import com.puj.taller02_permission.dependencies.components.ApplicationComponent;
import com.puj.taller02_permission.dependencies.components.DaggerApplicationComponent;
import com.puj.taller02_permission.dependencies.modules.GeocoderModule;
import com.puj.taller02_permission.dependencies.modules.LocationModule;
import com.puj.taller02_permission.dependencies.modules.GeoInfoModule;

import lombok.Getter;

@Getter
public class App extends Application {
    ApplicationComponent appComponent = DaggerApplicationComponent.builder()
            .locationModule(new LocationModule(this))
            .geocoderModule(new GeocoderModule(this))
            .geoInfoModule(new GeoInfoModule(this))
            .build();
}