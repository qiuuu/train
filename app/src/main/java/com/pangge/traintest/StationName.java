package com.pangge.traintest;


import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by iuuu on 17/3/21.
 * 也可以删了
 */

public class StationName extends AsyncTask<Integer, Integer, Void> {
   // StationDB db;
   // private Station temp_station_object;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        try{
            Request req = new Request.Builder()
                    .get()
                    .tag(this)
                    .url("https://kyfw.12306.cn/otn/resources/js/framework/station_name.js?station_version=1.9001")
                    .build();
            Response res = OkHttpClientManager.client.newCall(req).execute();
            if (!res.isSuccessful()) throw new IOException("unexpected" +
                    " response");
            Headers rh = res.headers();
            for (int i = 0; i < rh.size(); i++) {
                Log.i(rh.name(i), rh.value(i));
            }
            String station_names = res.body().string();
            Log.i("sss", "station_names");
            //setupDatabase();
           // db = new StationDB();
           // db.setupDatabase();

            //正则表达式解析出所需数据块

            Pattern pName = Pattern.compile("[\u4e00-\u9fa5]+");
            Pattern pUpper = Pattern.compile("[A-Z]+");
            Matcher mName = pName.matcher(station_names);
            Matcher mUpper = pUpper.matcher(station_names);

            while(mName.find() && mUpper.find()){
                Log.i("go","in");
                String name = mName.group(0);
                String upper = mUpper.group(0);
                System.out.println(name);
                System.out.println(upper);


                //Log.i("22", e);

            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;

    }
/*
    private void setupDatabase(){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(stati, "stationName-db", null);

    }*/


}
