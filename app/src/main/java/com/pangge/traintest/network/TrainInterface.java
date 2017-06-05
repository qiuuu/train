package com.pangge.traintest.network;
import io.reactivex.Observable;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

import com.google.gson.JsonObject;


/**
 * Created by iuuu on 17/4/13.
 */

public interface TrainInterface {
    //leftTicket/query?leftTicketDTO.train_date=2017-04-26&leftTicketDTO.from_station=TJP&leftTicketDTO.to_station=VNP&purpose_codes=ADULT
    @GET("leftTicket/query")
    Observable<Response<JsonObject>> query(
            @Query("leftTicketDTO.train_date") String date,
            @Query("leftTicketDTO.from_station") String from,
            @Query("leftTicketDTO.to_station") String to,
            @Query("purpose_codes") String code);
}
