package com.pangge.traintest;

import android.app.Activity;

import android.database.sqlite.SQLiteDatabase;



import java.util.List;

/**
 * Created by iuuu on 17/3/21.
 * 可以删了
 */

public class StationDB extends Activity {
    //Dao--->Data Access Object
    private StationDao stationDao; //Sql access object
     //used for creating a LOG object

    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
   // private Cursor cursor;
    //---------------------DB Setup Functions-----------------
    public StationDao setupDatabase(){
        //create database db file
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "stationName-db", null);
        //get the created database db file
        db = helper.getWritableDatabase();
        //create masterDao
        daoMaster = new DaoMaster(db);
        //create Session session
        daoSession = daoMaster.newSession();
        return daoSession.getStationDao();
    }
    //-----------------***End DB Setup Functions***-----------

    //-----------------SQL QUERY Functions-----------------
    public String getFromSQL(){
        List<Station> stationList = stationDao.queryBuilder().orderDesc(StationDao.Properties.Id).build().list();
        //get the list of all Stations in database in descending order

        if(stationList.size()>0){ //if list is not null
            return stationList.get(0).getCode();
            //get(0)--->1st object
            //getCode() is the function in Station class

        }
        return "";
    }

    public void SaveToSQL(String name, String code){
        Station station = new Station(null, name, code);
        stationDao.insert(station);
    }
    //------------------***END SQL QUERY***-----------------
/*
    public void addStation(String name, String code){
        Station station = new Station(null, name, code);
        daoSession.getStationDao().insert(station);




    }*/

}
