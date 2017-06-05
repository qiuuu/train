package com.pangge.traintest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pangge.traintest.model.Train;
import com.pangge.traintest.model.Train.DataBean.QueryLeftNewDTOBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iuuu on 17/3/17.
 */

public class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.RecyclerViewHolder> {

   // QueryLeftNewDTOBean
    private String strNum1;
    private String strNum2;
    private String strNum3;
    private String strNum4;
    private List<String> arrayName = new ArrayList<>();

    private List<QueryLeftNewDTOBean> trainList;
    public TrainAdapter(List<QueryLeftNewDTOBean> trainList){
        this.trainList = trainList;


    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.train_list_row, parent, false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    //Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        QueryLeftNewDTOBean train = trainList.get(position);
       // Log.i("adapter", train.getData().get());
      //  QueryLeftNewDTOBean info = train.getArrive_time();
        holder.from.setText(train.getFrom_station_name());
        holder.to.setText(train.getTo_station_name());
        holder.trainCode.setText((train.getStation_train_code()));
        holder.start.setText((train.getStart_time()));
        holder.arrive.setText(train.getArrive_time());
        holder.time.setText(train.getLishi());
        //display the num and price
       // while (train.)
        getSeat(train);
        holder.name1.setText("开");
        holder.name2.setText("个");
        holder.name3.setText("玩");
        holder.name4.setText("笑");

        //num
        holder.num1.setText(arrayName.get(0));
        holder.num2.setText(arrayName.get(1));
        holder.num3.setText(arrayName.get(2));
        holder.num4.setText(arrayName.get(3));



    }

    @Override
    public int getItemCount() {
        return trainList.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        public TextView from, to, trainCode, start, arrive, time,
                name1, name2, name3, name4, num1, num2, num3,num4;
        public RecyclerViewHolder(View view){
            super(view);
            from = (TextView) view.findViewById(R.id.from);
            to = (TextView) view.findViewById(R.id.to);
            trainCode = (TextView) view.findViewById(R.id.train_code);
            start = (TextView)view.findViewById(R.id.start);
            arrive = (TextView)view.findViewById(R.id.arrive);
            time = (TextView)view.findViewById(R.id.time);
            //seatname
            name1 = (TextView)view.findViewById(R.id.seat1);
            name2 = (TextView)view.findViewById(R.id.seat2);
            name3 = (TextView)view.findViewById(R.id.seat3);
            name4 = (TextView)view.findViewById(R.id.seat4);
            //num and price
            num1 = (TextView)view.findViewById(R.id.seat1_pri);
            num2 = (TextView)view.findViewById(R.id.seat2_pri);
            num3 = (TextView)view.findViewById(R.id.seat3_pri);
            num4 = (TextView)view.findViewById(R.id.seat4_pri);



        }
    }

    private void getSeat(QueryLeftNewDTOBean train){


        if(train.getGg_num()!="--"){
           // getNum("gg");
            arrayName.add(train.getGg_num());

        }
        if(train.getGr_num()!="--"){
            //getNum("gr");
            arrayName.add(train.getGr_num());

        }
        if(train.getQt_num()!="--"){
           // getNum("qt");
            arrayName.add(train.getQt_num());
        }
        if(train.getRw_num()!="--"){
            //getNum("rw");
            arrayName.add(train.getRw_num());
        }
        if(train.getRz_num()!="--"){
            //getNum("rz");
            arrayName.add(train.getRz_num());
        }
        if(train.getTz_num()!="--"){
           // getNum("tz");
            arrayName.add(train.getTz_num());
        }
        if(train.getWz_num()!="--"){
          //  getNum("wz");
            arrayName.add(train.getWz_num());
        }
        if(train.getYb_num()!="--"){
           // getNum("yb");
            arrayName.add(train.getYb_num());
        }
        if(train.getYw_num()!="--"){
          //  getNum("yw");
            arrayName.add(train.getYw_num());
        }
        if(train.getYz_num()!="--"){
            //getNum("yz");
            arrayName.add(train.getYz_num());
        }
        if(train.getZe_num()!="--"){
            //getNum("ze");
            arrayName.add(train.getZe_num());
        }
        if(train.getZy_num()!="--"){
           // getNum("zy");
            arrayName.add(train.getZy_num());
        }
        if(train.getSwz_num()!="--"){
           // getNum("swz");
            arrayName.add(train.getSwz_num());
        }

    }
/*
    private void getNum(String s){
        String seatName;
        switch (s){
            case "gg":
                seatName = "";
                break;
            case "gr":
                seatName = "";
                break;
            case "qt":
                seatName = "";
                break;
            case "rw":
                seatName = "";
                break;
            case "rz":
                seatName = "";
                break;
            case "tz":
                seatName = "";
                break;
            case "wz":
                seatName = "";
                break;
            case "yb":
                seatName = "";
                break;
            case "yw":
                seatName = "";
                break;
            case "yz":
                seatName = "";
                break;
            case "ze":
                seatName = "";
                break;
            case "zy":
                seatName = "";
                break;
            case "swz":
                seatName = "";
                break;
            default:
                break;
        }
    }
    */
}
