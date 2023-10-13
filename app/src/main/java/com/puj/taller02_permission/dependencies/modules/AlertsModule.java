package com.puj.taller02_permission.dependencies.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import com.puj.taller02_permission.utils.AlertUtils;

@Module
public class AlertsModule {
    @Singleton
    @Provides
    public AlertUtils provideAlertHelper() {
        return new AlertUtils();
    }
}
