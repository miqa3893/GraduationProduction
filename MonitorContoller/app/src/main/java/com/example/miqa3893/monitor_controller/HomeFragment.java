package com.example.miqa3893.monitor_controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.util.Objects.isNull;

public class HomeFragment extends android.support.v4.app.Fragment {
    //デバッグ用ダミー
    protected static Integer count = 0,correct = 0,failure = 0;
    //更新周期
    public static long waitTime = 30000;

    //学内用
    final public static String DATA_CONNECTION_URL = "http://10.111.1.160:3939/api/monitor/get/";

    //自宅用
    //final public static String DATA_CONNECTION_URL = "http://192.168.0.8:3939/api/monitor/get/";
    //final String GRAPH_CONNECTION_URL = "http://192.168.0.8:3939/api/graph/get/";

    RequestQueue mQueue;
    Monitor monitor;      //独自クラス
    View rootView;
    OnCountClickListener _clickListener;
    JSONObject jsonObject;
    Handler handler;
    Runnable r;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //親クラスのコンストラクタ,Viewの生成
        super.onCreateView(inflater,container,savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_home,container,false);
        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            _clickListener = (OnCountClickListener)context;
        }catch(ClassCastException e){
            Log.e("CastError",e.toString());
        }
    }

    //Viewが生成し終わった時に呼ばれるメソッド
    //Viewを参照することで、このFragmentのViewを参照できる
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        HandlerThread handlerThread = new HandlerThread("drawGraph");
        handlerThread.start();
        final Date d = new Date();
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        monitor = new Monitor();
        mQueue = Volley.newRequestQueue(getActivity());

        init_text();

        getData(d,df);

        Handler pieHandler = new Handler(handlerThread.getLooper());
        pieHandler.post(new Runnable(){
            @Override
            public void run() {
                createPie();
            }
        });

        Log.i("Information::","HomeFragment Start.");

        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                getData(d,df);
                handler.postDelayed(this,waitTime);
            }
        };
        handler.postDelayed(r,waitTime);
    }

    //不良率5%越えた？
    //データを一回以上取得してから
    private void isFailure() {
        double temp;
        if(monitor.getTotal() < 1) {
            temp = 0;
        }else{
            temp = monitor.getFailure() *100.0 / monitor.getTotal();
        }
        Log.d("HomeRate::",String.valueOf(temp));
        if(temp>HomeActivity.threshold){
            handler.removeCallbacks(r);
            _clickListener.onClick();
        }else{
            createPie();
        }
    }

    //円グラフを作る
    private void createPie() {
        ArrayList<Float> pieData = new ArrayList<>();
        ArrayList<Entry> entry = new ArrayList<>();
        PieChart pieChart = rootView.findViewById(R.id.pieChart);

        //まず、pieDataとentryを初期化
        pieData.clear();
        entry.clear();

        //デフォルトデータのセット
        pieData.add((float)monitor.getCorrect());
        pieData.add((float)monitor.getFailure());

        //円グラフのラベルをセットする
        ArrayList<String> label = new ArrayList<>();
        label.add("");
        label.add("");

        //円グラフ用データを用意する
        //Entryクラスには(float data,int index)のコンストラクタ以外は入らない
        entry.add(new Entry(pieData.get(0),0));
        entry.add(new Entry(pieData.get(1),1));

        //データを初期化　labelはデータラベル
        PieDataSet dataSet = new PieDataSet(entry,"");

        //グラフの色を設定
        //setColorsにはint[]を渡しているため、Colorクラスのintを渡すとその色になる
        //数が多すぎると多分例外吐く
        //dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        //今回は「スカイブルー」と「オレンジレッド」
        dataSet.setColors(new int[]{Color.rgb(135,206,235),Color.rgb(255,69,0)});

        //パーセンテージを利用するかどうか、グラフの説明　をそれぞれセット
        //パーセンテージを利用すると、勝手にパーセンテージを計算してくれる
        pieChart.setDescription("");
        pieChart.setUsePercentValues(true);

        //データの初期化とセット
        PieData pie = new PieData(label,dataSet);
        pie.setValueTextSize(45f);
        pie.setValueFormatter(new PercentFormatter());
        pieChart.setData(pie);

        //ドーナツホール
        pieChart.setDrawHoleEnabled(true);              //ドーナツを作るかどうか
        pieChart.setTransparentCircleRadius(32f);       //半透明なドーナツの直径
        pieChart.setHoleRadius(30f);                    //透明なドーナツの直径


        //表示される際のアニメーション効果時間をミリ秒で設定
        pieChart.animateY(10);
        //pieChart.animateX(1500);
    }

    //APIからデータを取得
    private void getData(final Date d, final DateFormat df) {
        String url = DATA_CONNECTION_URL + df.format(d) + "/all";

        mQueue.add(new JsonObjectRequest(Request.Method.GET, url
                , null  //jsonRequest（リクエスト先へ一緒に渡すJSON、なければnull指定）
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                jsonObject = response;      //取得できたら、レスポンスを変数に代入
                if(jsonObject.length()>0){
                    parseJSON(jsonObject);
                    setViewer();
                    isFailure();
                }
            }

        } //Listener
                ,new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response Failed::",error.toString());
                Log.e("volley error::",error.toString());
            }
        }));
    }

    //JSONデータをパースする
    private void parseJSON(JSONObject jsonObject){
        try {
            JSONArray array = jsonObject.getJSONArray("monitor");
            JSONObject object = (JSONObject)array.get(0);
            monitor.setTotal(Float.parseFloat(object.getString("total")));
            monitor.setCorrect(Float.parseFloat(object.getString("correct")));
            monitor.setFailure(Float.parseFloat(object.getString("failure")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Viewにデータをセットする
    private void setViewer() {
        TextView lb_total = rootView.findViewById(R.id.total);
        TextView lb_correct = rootView.findViewById(R.id.correct);
        TextView lb_failure = rootView.findViewById(R.id.failure);
        lb_total.setText(String.valueOf((int)monitor.getTotal()));
        lb_correct.setText(String.valueOf((int)monitor.getCorrect()));
        lb_failure.setText(String.valueOf((int)monitor.getFailure()));
    }


    //テキスト・フォントとかの初期設定
    private void init_text(){
        monitor = new Monitor();
        TextView lbDataTotal = rootView.findViewById(R.id.total);
        TextView lbDataCorrect = rootView.findViewById(R.id.correct);
        TextView lbDataFailure = rootView.findViewById(R.id.failure);
        ConstraintLayout layout = rootView.findViewById(R.id.home_layout);

        //lbTotal.setTypeface(labelTypeFace);
        lbDataTotal.setText(String.valueOf(count));
        //lbCorrect.setTypeface(labelTypeFace);
        lbDataCorrect.setText(String.valueOf(correct));
        //lbFailure.setTypeface(labelTypeFace);
        lbDataFailure.setText(String.valueOf(failure));
        //lbDataTotal.setTypeface(dataTypeFace);
        //lbDataCorrect.setTypeface(dataTypeFace);
        //lbDataFailure.setTypeface(dataTypeFace);
    }

    //リソースの開放
    @Override
    public void onStop() {
        super.onStop();
        Log.i("Information::","HomeFragment is Stopped.");
        handler.removeCallbacks(r);
        HomeActivity.cleanupView(rootView);
        monitor = null;
        jsonObject = null;
        mQueue = null;
        System.gc();
    }

    public interface OnCountClickListener{
        void onClick();
    }
}
