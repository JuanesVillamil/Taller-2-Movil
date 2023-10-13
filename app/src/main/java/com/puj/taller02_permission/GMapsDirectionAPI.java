package com.puj.taller02_permission;

import com.puj.taller02_permission.models.Direction;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GMapsDirectionAPI {
    @GET("directions/json")
    Call<Direction> getDirection(@Query("origin") String origin,
                                 @Query("destination") String destination,
                                 @Query("key") String key);

    @GET("directions/json")
    Call<ResponseBody> getDirectionNew(@Query("origin") String origin,
                                    @Query("destination") String destination,
                                    @Query("key") String key);
}
