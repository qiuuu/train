package com.pangge.traintest.network;



import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;

/**
 * Created by iuuu on 17/4/1.
 */

public interface RequestInterface {
    @GET("resources/js/framework/station_name.js?station_version=1.9001")
    Observable<Response<String>> register();


}
