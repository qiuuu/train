package com.example;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;

public class GreenDaoGenerate {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.pangge.traintest");
        addStation(schema);
        new DaoGenerator().generateAll(schema, "../TrainTest/app/src/main/java-gen");
    }

    private static void addStation(Schema schema){
        Entity station = schema.addEntity("Station");
        //station.addIdProperty().primaryKey();
        station.addIdProperty();
        station.addStringProperty("name").notNull();
        station.addStringProperty("code");
    }
}
