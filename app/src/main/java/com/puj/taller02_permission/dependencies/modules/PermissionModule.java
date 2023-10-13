package com.puj.taller02_permission.dependencies.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import com.puj.taller02_permission.utils.PermissionHelper;

@Module
public class PermissionModule {

    @Singleton
    @Provides
    public PermissionHelper providePermissionHelper() {
        return new PermissionHelper();
    }
}