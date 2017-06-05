package com.pangge.traintest;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.util.Log;

import android.widget.CalendarView;


import com.andexert.calendarlistview.library.DayPickerView;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.pangge.traintest.network.RequestInterface;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by iuuu on 17/4/19.
 */

public class CalendarActivity extends AppCompatActivity{
    private DayPickerView dayPickerView;
    private CalendarView calendarView;
    private CompositeDisposable compositeDisposable;
    private Long today;
    private Long dt;
    //private SimpleMonthAdapter simpleMonthAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        compositeDisposable = new CompositeDisposable();
        calendarView = (CalendarView)findViewById(R.id.calendarView);
        dt = calendarView.getDate();

        initCalendar();
    }

    @Override
    protected void onResume() {
        super.onResume();
       // getCalendar();
    }


    private void initCalendar(){
        Log.i("rili","ssss---");
        //todayDate;

        OkHttpClient okHttpClient = OkHttpClientManager.client;
        RequestInterface requestInterface = new Retrofit.Builder()
                .baseUrl(MainActivity.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(okHttpClient)
                .build().create(RequestInterface.class);
        compositeDisposable.add(requestInterface.register()

                .flatMap(new Function<Response, ObservableSource<?>>() {
                    @Override
                    public Observable<?> apply(@NonNull Response res) throws Exception {
                        Headers rh = res.headers();
                       /* for (int i = 0; i < rh.size(); i++) {
                            Log.i(rh.name(i), rh.value(i));
                        }*/
                        Date date = rh.getDate("Date");
                        today = date.getTime();
                        System.out.println("today--");
                        System.out.println(date);





                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                        String s = sdf.format(date);
                        Log.i(s, "----");
                        return Observable.just(today);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())


                .subscribeWith(new DisposableObserver<Object>(){
                    @Override
                    public void onNext(Object o) {
                        String s = o.toString();
                        Log.i(s,"--起始日期---");
                        //起始日期

                        calendarView.setMinDate(today-1000);
                        Calendar calendar = Calendar.getInstance();
                        Date current = new Date(today);
                        calendar.setTime(current);
                        Log.i("ww","ddd－－结束日期");

                        //结束日期

                        calendar.add(Calendar.DATE, 30);
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                        calendar.set(Calendar.SECOND, 59);
                        Long lateTime = calendar.getTimeInMillis();
                        Date dt3 = new Date(lateTime);
                        //String d3 = dt3.toString();
                        System.out.println("可不是五月吗－－");
                        System.out.println(dt3);
                       // DatePicker datePicker =
                        //datePicker.setMinDate(today-1000);


                        calendarView.setMaxDate(lateTime);


                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }));


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            @Override
            public void onSelectedDayChange(CalendarView view, int i, int i1, int i2) {
                if(calendarView.getDate() != dt){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                    //Long date1 = Long.valueOf(i+"-"+i1+"-"+i2);
                    Long date2 = calendarView.getDate();
                    Date dt2 = new Date(date2);
                    String s = sdf.format(dt2);
                    System.out.println(s);



                  //  Date d = new Date(date2);
                  //  String date = sdf.format(d);



                  //  Log.i(date+"", "1--ooo--error??");
                    // Log.i(i1+"", "2--ooo--error??");
                    //Log.i(i2+"", "3--ooo--error??");

                    goBack(s);
                }


            }
        });



    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("hello--","---world");
        compositeDisposable.clear();
    }
    /* private void getCalendar(){
        RxView.clicks(calendarView)
      //  .map(o ->
                .flatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull Object o) throws Exception {
                        System.out.println(calendarView.getDate());
                      //  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        //sdf.f

                        return null;
                    }
                })
        .subscribe();
    }*/

    private void goBack(String date){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("date", date);
        setResult(RESULT_OK,intent);

        Log.i(date, "---go back MainActivity--");
        //startActivity(intent);
        finish();
    }


}
