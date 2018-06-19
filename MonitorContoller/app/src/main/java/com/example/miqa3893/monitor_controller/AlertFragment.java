package com.example.miqa3893.monitor_controller;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.util.Objects.isNull;

//Fragmentにする？Activityにする…？　的な実験
public class AlertFragment extends Fragment {

    protected View rootView;
    protected long waitTime = 250;          //周期処理の周期時間
    RequestQueue mQueue;
    OnBackClickListener _clickListener;
    Runnable r,r2;
    JSONObject jsonObject;
    Handler handler;
    Monitor monitor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //親クラスのコンストラクタ,Viewの生成
        super.onCreateView(inflater,container,savedInstanceState);
        return rootView = inflater.inflate(R.layout.fragment_alert,container,false);
    }

    //Viewが生成し終わった時に呼ばれるメソッド
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        TextView lb_line = rootView.findViewById(R.id.failureLine);
        TextView lb_failure = rootView.findViewById(R.id.failureMessage);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),"monitorText.otf");
        lb_failure.setTypeface(font);
        lb_line.setTypeface(font);

        final Date d = new Date();
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        Log.i("Information::","AlertFragment Start.");
        mQueue = Volley.newRequestQueue(getActivity());

        monitor = new Monitor();
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                //背景色をチカチカさせる
                ConstraintLayout layout = rootView.findViewById(R.id.alert_layout);
                ColorDrawable drawable = (ColorDrawable) rootView.getBackground();
                int colorInt = drawable.getColor();
                if(colorInt == Color.YELLOW) layout.setBackgroundColor(Color.WHITE);
                else if(colorInt == Color.WHITE) layout.setBackgroundColor(Color.YELLOW);
                drawable =  null;

                handler.postDelayed(this,waitTime);
            }
        };

        r2 = new Runnable() {
            @Override
            public void run() {
                getData(d,df);
                handler.postDelayed(r2,HomeFragment.waitTime);
            }
        };

        handler.postDelayed(r,waitTime);
        handler.postDelayed(r2,HomeFragment.waitTime);

    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            _clickListener = (AlertFragment.OnBackClickListener)context;
        }catch(ClassCastException e){
            Log.e("CastError",e.toString());
        }
    }

    //APIからデータを取得
    private void getData(Date d,DateFormat df) {
        mQueue.add(new JsonObjectRequest(Request.Method.GET, HomeFragment.DATA_CONNECTION_URL + df.format(d) + "/all"
                , null  //jsonRequest（リクエスト先へ一緒に渡すJSON、なければnull指定）
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                jsonObject = response;      //取得できたら、レスポンスを変数に代入
                if(jsonObject.length()>0){
                    parseJSON();
                    isFailure();
                }
            }

        } //Listener
                ,new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response Failed::",error.toString());
                Log.e("volley Data error::",error.toString());
            }
        }));
    }

    //不良率5%下回った？
    //データを一回以上取得してから
    private void isFailure() {
        double temp = (double)monitor.getFailure() *100.0 / (double)monitor.getTotal();
        Log.d("AlertRate::",String.valueOf(temp));
        if(temp<=HomeActivity.threshold){
            handler.removeCallbacks(r);
            handler.removeCallbacks(r2);
            _clickListener.onClick(1);
        }
    }

    //JSONデータをパースする
    private void parseJSON() {
        try {
            monitor.setTotal(Integer.parseInt(jsonObject.getString("total")));
            monitor.setCorrect(Integer.parseInt(jsonObject.getString("correct")));
            monitor.setFailure(Integer.parseInt(jsonObject.getString("failure")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //リソースの開放
    @Override
    public void onStop() {
        super.onStop();
        Log.i("Information::","AlertFragment is Stopped.");
        handler.removeCallbacks(r);
        handler.removeCallbacks(r2);
        mQueue = null;
        monitor = null;
        jsonObject = null;
        System.gc();
    }

    public interface OnBackClickListener{
        void onClick(int id);
    }
}
