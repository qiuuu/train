package com.pangge.traintest;


import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.pangge.traintest.model.Train.DataBean.QueryLeftNewDTOBean;
import com.pangge.traintest.network.TrainInterface;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by iuuu on 17/4/14.
 */

public class DisplayActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    List<QueryLeftNewDTOBean> arrayList = new ArrayList<QueryLeftNewDTOBean>();
    static QueryLeftNewDTOBean train = new QueryLeftNewDTOBean();
    private CompositeDisposable compositeDisposable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display);

        recyclerView = (RecyclerView) findViewById(R.id.rv);


        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new TrainAdapter(arrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        compositeDisposable = new CompositeDisposable();
        arrayList.clear();



    }

    @Override
    protected void onResume() {
        super.onResume();
        getResponst();
        Log.i("DisplayActivity-resume","唤醒");
    }

    @Override
    protected void onPause() {
        super.onPause();
        compositeDisposable.clear();
    }

    private void getResponst() {
        //Caused by: java.lang.NullPointerException: println needs a message
        //forget how to solve
        Log.i("jisonn--from", train.getFrom_station_telecode());
        Log.i("jiso--to", train.getTo_station_telecode());
        Log.i("jiso--date", train.getStart_train_date());



        OkHttpClient okHttpClient = OkHttpClientManager.client;
        TrainInterface trainInterface = new Retrofit.Builder()
                .baseUrl(MainActivity.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())

                .client(okHttpClient)
                .build().create(TrainInterface.class);
        compositeDisposable.add(trainInterface.query(
                train.getStart_train_date(),
                train.getFrom_station_telecode(),
                train.getTo_station_telecode(),
                "ADULT")

                //.map(jsonObjectResponse -> jsonObjectResponse.headers().getDate())
                // .map(trainResponse -> trainResponse.isSuccessful())
                // .map(trainResponse -> trainResponse.body())
                .map(jsonObjectResponse -> jsonObjectResponse.body().get("data").getAsJsonArray())
                .flatMap(this::handlerTrain)
                .subscribeOn(Schedulers.io())

                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<JsonArray>(){
                    @Override
                    public void onComplete() {
                        Log.i("con","ssuccessful");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(e.toString(),"error");

                    }

                    @Override
                    public void onNext(JsonArray array) {
                        Log.i("onNext","ssuccessful");
                        adapter.notifyDataSetChanged();
                    }
                }));
    }


    private Observable<JsonArray> handlerTrain(JsonArray array) {
        return Observable.create(new ObservableOnSubscribe<JsonArray>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<JsonArray> e) throws Exception {
                Iterator<JsonElement> it = array.iterator();
                Gson gson = new Gson();

                //compositeDisposable.add();

                while (it.hasNext()) {
                    //获取一组JSON数据
                   // JsonElement e = it.next();

                    JsonElement queryElement = it.next().getAsJsonObject().get("queryLeftNewDTO");


                    QueryLeftNewDTOBean train = gson.fromJson(queryElement, QueryLeftNewDTOBean.class);
                    String co = train.getStation_train_code();
                    Log.i("hhhhh**", co);
                    arrayList.add(train);


                }
                e.onNext(array);


              /*  runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });*/
            }
        }).subscribeOn(Schedulers.newThread());


    }

}
