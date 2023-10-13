package com.puj.taller02_permission.dependencies.components;

import com.puj.taller02_permission.activities.BasicActivity;
import com.puj.taller02_permission.activities.MapsFragment;
import com.puj.taller02_permission.dependencies.modules.AlertsModule;
import com.puj.taller02_permission.dependencies.modules.GeoInfoModule;
import com.puj.taller02_permission.dependencies.modules.GeocoderModule;
import com.puj.taller02_permission.dependencies.modules.LocationModule;
import com.puj.taller02_permission.dependencies.modules.PermissionModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {PermissionModule.class,GeocoderModule.class, GeoInfoModule.class, LocationModule.class})
public interface ApplicationComponent {
    void inject(BasicActivity activity);

    void inject(MapsFragment fragment);
}
