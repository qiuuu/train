package com.pangge.traintest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.pangge.traintest.network.RequestInterface;

import org.greenrobot.greendao.rx.RxDao;
import org.greenrobot.greendao.rx.RxQuery;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import okhttp3.OkHttpClient;

import retrofit2.Response;
import retrofit2.Retrofit;


import retrofit2.converter.scalars.ScalarsConverterFactory;

import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;


public class MainActivity extends AppCompatActivity{
    public static final String BASE_URL = "https://kyfw.12306.cn/otn/";
    private CompositeDisposable compositeDisposable;
    private EditText fromStation;
    private EditText toStation;
    private Button query;
    private TextView calendar;

    private String rtCode;
    private String today;

   /* private String code;
    private String fromCode;
    private String toCode;
   // StationName stationName;
   // private StationDao stationDao;
   // StationDB stationDB;*/
  // private StationDao stationDao;

    private RxDao<Station, Long> stationDao;
    private RxQuery<Station> stationRxQuery;
    private DaoSession daoSession;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get the Rx variant of the station DAO
        daoSession = ((MyApplication)getApplication()).getDaoSession();
        stationDao = daoSession.getStationDao().rx();
        //rx(): greendao
        //traintest.stationdao

       // stationDao = daoSession.getStationDao();

        compositeDisposable = new CompositeDisposable();

        fromStation = (EditText)findViewById(R.id.from_station);
        toStation = (EditText)findViewById(R.id.to_station);
        calendar = (TextView)findViewById(R.id.calendar);
        query = (Button)findViewById(R.id.ticket_query);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

        today = sdf.format(new Date());
        calendar.setText(today);
        DisplayActivity.train.setStart_train_date(today);

        getStation();


        //initEditText();
    // Example of a call to a native method
       // TextView tv = (TextView) findViewById(R.id.sample_text);
      //  tv.setText(stringFromJNI());
    }

    @Override
    protected void onResume() {
        super.onResume();
        initEditText();
        initCalendar();
        Log.i("subbbbsc－－－Resume","dd");



    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MainActivity-pause","不能destroy");

        compositeDisposable.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MainActivity-destrooy","destroy");
        compositeDisposable.clear();
    }

//------------------Get Station names from web----------------
    private void getStation(){
        Log.i("begin---","in station");
        OkHttpClient okHttpClient = OkHttpClientManager.client;
        RequestInterface requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(okHttpClient)
                .build().create(RequestInterface.class);
        compositeDisposable.add(requestInterface.register()
                .map(response -> response.body().toString())


                .flatMap(new Function<String, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull String stations) throws Exception {
                        //去掉所有空格 海 口－>海口
                        //@hkd|海  口东|KEQ|haikoudong


                        stations = stations.replaceAll("\\s+", "");
                       // Log.i("sT",sT);
                        Pattern pName = Pattern.compile("[\u4e00-\u9fa5]+");
                        Pattern pUpper = Pattern.compile("[A-Z]+");
                        Matcher mName = pName.matcher(stations);
                        Matcher mUpper = pUpper.matcher(stations);
                        Station station;

                        while(mName.find() && mUpper.find()){

                            String name = mName.group(0);
                            String upper = mUpper.group(0);
                            //  temp_station_object = new Station(null,name,upper);
                            // db.addStation(name, upper);
                            //   db.SaveToSQL(temp_station_object);
                            // System.out.println(name);
                            //System.out.println(upper);
                            //Log.i("go",name);


                            station = new Station(null, name, upper);
                            //String s = station.getName();
                            //用的是traintest.StationDao
                           // Log.i("go---",s);
                            stationDao.getDao().insert(station);
                           // stationDao.insert(station);

                        }

                       /* int s = stationDao
                                .getDao()
                                .queryBuilder()
                                .where(StationDao.Properties.Code.eq("北京"))
                                .list()
                                .size();
                                //.get(0)
                               // .getCode();
                       // Log.i("---go---",s);
                        System.out.println(s);*/
                        return Observable.just("ok!!!--sucess to save");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handlerResponse,this::handleError));


    }

    private void handlerResponse(Object stations){
        Log.i("处理站名成功了吗！！！---",stations.toString());

    }
    private void handleError(Throwable error){
       // Toast.makeText(this, "Error" + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        Log.i("Error---",error.getLocalizedMessage());
    }

    //--------------ADD Data to DATABASE-----------------
/*
    private void addStation(String name, String node){
        Station station = new Station(null, name, node);
        //compositeDisposable.add(stationDao.insert(station))
        //insert -->io thread
        stationDao.insert(station)
                //.subscribeOn(rx.android.schedulers.Androi)
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe();
    }*/
    //-------------***END add data***--------------------

    //-----------------SQL QUERY Functions-----------------
    private Observable<String> getFromSQL(String s){

        Log.i(s,"----进入SQL observable");
        //s = "北京";
          //query all stations, sorted a-z by their text
        //stationRxQuery = daoSession.getStationDao().queryBuilder().orderAsc(StationDao.Properties.Id).rx();
       // return Observable.defer(() -> {

        //io.rx
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                //进来了，查询没有执行？？？

                //rx.Observation-->io.reactivex.Observation
                Log.i(s, "城市名字有传递进来－－");
                stationRxQuery = daoSession.getStationDao()
                        .queryBuilder()
                        .where(StationDao.Properties.Name.eq(s))
                        //.where(StationDao.Properties.Name.eq(to))
                        .rx();


                //以下是啥情况？？？

                stationRxQuery.list()
                        .filter(stationList -> stationList.size()>0)
                        .map(stationList -> stationList.get(0).getCode())

                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                e.onNext(s);
                                System.out.println("chenggongnngng");
                                Log.i(s,"mmm----");
                            }
                        });


            }
        }).subscribeOn(Schedulers.io());



        /*  .flatMap(new Func1<String, rx.Observable<String>>() {
                                     @Override
                                     public rx.Observable<String> call(String s) {
                                         System.out.println(s);
                                         rtCode = s;
                                         // e.onNext(rtCode);
                                         System.out.println("okoooooop");
                                         Log.i(rtCode,"whywhyhhhhh!");

                                         return rx.Observable.just("ok");
                                     }
                                 }
                        )*/
                    /*    .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())

                        .subscribe(new Subscriber<String>() {
                            @Override
                            public void onCompleted() {
                                //直接进这里是啥意思
                                //当没有onNext事件进入队列的时候 就触发onComplete ()
                                Log.i("无奈","do you");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i("error", e.getLocalizedMessage());

                            }

                            @Override
                            public void onNext(String s) {
                                Log.i(s,"kaiii --  erer");
                                e.onNext(s);

                            }
                        });*/
               // e.onNext(rtCode);
           // }
        //}).subscribeOn(Schedulers.io());


    }

    private void initEditText(){
        Log.i("editText--button","进入EditText observable");
        //Observable<String>


        compositeDisposable.add(RxTextView.textChanges(fromStation)
                .map(charSequence -> charSequence.toString())
                .filter(s -> s.length()>0)

                .flatMap(this::getFromSQL)

               // .doOnNext(s -> System.out.println(s))

                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                //.map() string-->train.fromStation-->传入getFromSQL
                 //.subscribe(this::getFromSQL,this::handleError));
                .subscribe(DisplayActivity.train::setFrom_station_telecode));
               // .subscribe(this::handlerResponse,this::handleError));

        compositeDisposable.add(RxTextView.textChanges(toStation)
                .map(charSequence -> charSequence.toString())
                .flatMap(this::getFromSQL)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
               // .subscribe(this::getFromSQL,this::handleError));
                .subscribe(DisplayActivity.train::setTo_station_telecode));

        compositeDisposable.add(RxView.clicks(query)
               // .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                //.observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(o1 -> getTrain()));
        //getFromSQL返回值放哪了！！
        /*
        Observable.combineLatest(fromObs, toObs, new BiFunction<String, String, Train>(){
            @Override
            public Train apply(@NonNull String from, @NonNull String to) throws Exception {

                Train train = new Train();
                train.setFrom_station_name(from);
                train.setTo_station_name(to);
                Log.i("bfromm",from);
                Log.i("btoo",to);
                if(train.getFrom_station_name().length()>1 && train.getTo_station_name().length()>1){
                    return train;
                }else {
                    return null;
                }
               return train;*/
/*
        Observable.combineLatest(fromObs, toObs, new BiFunction<String,String,String>() {
            @Override
            public String apply(@NonNull String s, @NonNull String s2) throws Exception {
                return s;
            }
        }).subscribe(this::getFromSQL, this::handleError);*/




    }

    private void getTrain(){
       // DisplayActivity.train.setFrom_station_telecode("BJP");
        //DisplayActivity.train.setTo_station_telecode("SHH");
        Log.i("ha----","douniwaner");

        Intent intent = new Intent(this, DisplayActivity.class);
        startActivity(intent);

               // .subscribe(this::handlerTrain,this::handleError));

    }

    private void initCalendar(){
        compositeDisposable.add(RxView.clicks(calendar)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.newThread())
                .subscribe(o -> editTime()));
    }

    private void editTime(){
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(requestCode+"","Intent");
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    String dt = data.getStringExtra("date");
                    Log.i("--ji000--date","result");
                    System.out.println(dt);


                    if(dt != null){
                        DisplayActivity.train.setStart_train_date(dt);
                        System.out.println(dt);
                        calendar.setText(dt);
                    }else {

                        DisplayActivity.train.setStart_train_date(today);
                        System.out.println(today);
                    }


                }
                break;
            default:

        }
    }

    /*
    @Override
    public void onClick(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{


                   // String from = fromCity.getText().toString();
                  //  String to = toCity.getText().toString();
                    //U url = new URL("https://kyfw.12306.cn/otn/leftTicket/log?leftTicketDTO.train_date=2017-03-20&leftTicketDTO.from_station=BJP&leftTicketDTO.to_station=ZZF&purpose_codes=ADULT");

                    Request request = new Request.Builder()
                            .get()
                            .tag(this)
                            .url("http://kyfw.12306.cn/otn/leftTicket/queryX?leftTicketDTO.train_date=2017-04-20&leftTicketDTO.from_station=TJP&leftTicketDTO.to_station=VNP&purpose_codes=ADULT")
                            .build();
                    Response response = OkHttpClientManager.client.newCall(request).execute();
                    if (!response.isSuccessful()) throw new IOException("unexpected" +
                            " response");
                    Headers responseHeaders = response.headers();
                    for (int i = 0; i < responseHeaders.size(); i++) {
                        Log.i(responseHeaders.name(i), responseHeaders.value(i));
                    }


                    String value = response.body().string();
                    Log.i("请求体", value);

                   // ArrayList<Train> trains = new ArrayList<Train>();
                    Gson gson = new Gson();
                    //Type type = new TypeToken<ArrayList<Train>>(){}.getType();
                    if(value.equals("")){
                        Log.i("ha","value false");
                    }else {
                        Log.i("hhh", "hello");

                        parseRoot(value);
                       // adapter.notifyDataSetChanged();


                        Log.i("hhh", "world");

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

    }*/
/*
    private void parseRoot(String json){
        try{
            Gson gson = new Gson();
            Log.i("code", json);

            //解析JSON数据，获取data块下数据

            JsonParser parser = new JsonParser();
            JsonObject object = (JsonObject)parser.parse(json);
            JsonElement element = object.get("data");
            Log.i("value", element.toString());
            //解析JSON数组
            //System.out.println(element.toString());
            String data = element.toString();
            JsonArray array = (JsonArray) parser.parse(data);
            Iterator it = array.iterator();
            while (it.hasNext()){
                //获取一组JSON数据
                JsonElement e = (JsonElement)it.next();
                Log.i("va", e.toString());

                JsonObject trainObject = (JsonObject)parser.parse(e.toString());
                JsonElement eQuery = trainObject.get("queryLeftNewDTO");

                Log.i("ro",eQuery.toString());

                //Type type = new TypeToken<List<Train>>(){}.getType();
                QueryLeftNewDTOBean train = gson.fromJson(eQuery, QueryLeftNewDTOBean.class);
                String co = train.getStation_train_code();
                Log.i("hhhhh**", co);
                arrayList.add(train);



                JsonObject queryObject = (JsonObject)parser.parse(eQuery.toString());
                JsonElement eFrom = queryObject.get("from_station_name");
                JsonElement eTo = queryObject.get("to_station_name");
                JsonElement eCode = queryObject.get("station_train_code");
                JsonElement eStart = queryObject.get("start_time");
                JsonElement eArrive = queryObject.get("arrive_time");
                Log.i("from", eFrom.toString());


            }*/
          /*  runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });*/


           // JsonElement element1 = object1.get("queryLeftNewDTO");
           // Log.i("value", element1.toString());





            //List<Data> data = root.getData();
            //Log.i("data", root.toString());
            //List<Data> datas = root.getData();

           // String trains = data.get(1).getQueryLeftNewDTO();
            //parseData(data);
           /* int i = 1;
            for(String code : trainCode){
                //Train train = new Train(from[i],to[i],start[i],arrive[i],time[i],code);

              //  from[i] = trains.get(i).getFrom();
              //  to[i] = trains.get(i).getTo();

               // trainCode[i] = trains.get(i).getTrainCode();
                Log.i("code", trainCode[i]);
                //  arrayList.add(train);
                i++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/






    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
