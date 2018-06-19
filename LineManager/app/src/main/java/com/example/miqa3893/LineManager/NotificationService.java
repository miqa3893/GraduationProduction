package com.example.miqa3893.LineManager;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//TotalLines:151
public class NotificationService extends IntentService {
    //設定を読み込むためのクラス変数
    private SharedPreferences preferences;
    //通知を行う基準不良率
    double threshold;
    RequestQueue mQueue;
    final public static String DATA_CONNECTION_URL = "http://10.111.1.160:3939/api/monitor/get/";
    //更新周期
    public long waitTime;
    JSONObject jsonObject;
    Monitor monitor;      //独自クラス

    public NotificationService() {
        super("notification");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        monitor = new Monitor();
        mQueue = Volley.newRequestQueue(getApplicationContext());
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Date d = new Date();
                //設定の反映
                waitTime = Long.parseLong(preferences.getString("syncTime","30"))*1000;
                getData(d,new SimpleDateFormat("yyyy-MM-dd"));
                handler.postDelayed(this,waitTime);
            }
        },waitTime);

    }

    //APIからデータを取得
    private void getData(final Date d, final DateFormat df) {
        String url = DATA_CONNECTION_URL + df.format(d) + "/all";
        //通知する設定ではないなら即時終了
        if(!preferences.getBoolean("notification",true)) return;

        mQueue.add(new JsonObjectRequest(Request.Method.GET, url
                , null  //jsonRequest（リクエスト先へ一緒に渡すJSON、なければnull指定）
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                jsonObject = response;      //取得できたら、レスポンスを変数に代入
                if(jsonObject.length()>0){
                    Log.d("RawJSONData",jsonObject.toString());
                    parseJSON(jsonObject);
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

    //不良率5%越えた？
    //データを一回以上取得してから
    private void isFailure() {
        if(monitor.getTotal() <= 0) return;
        double temp = monitor.getFailure() *100.0 / monitor.getTotal();
        Log.d("HomeRate::",String.valueOf(temp));
        Log.d("threshold::",String.valueOf(threshold));
        threshold = Double.parseDouble(preferences.getString("failureThreshold","6.5"));
        //不良率が一定を越えていたら通知を出す
        if(temp<threshold) return;
        else  notification();
    }

    //通知の設定
    public void notification() {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());        //NotificationCompat.Builder(通知)のオブジェクト生成
        //通知をタップすると開かれる画面へ移る
        Intent intent = new Intent(getApplicationContext(),DetailActivity.class);
        //PendingIntentに詰める
        final PendingIntent notificationIntent =
                PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        //builderの設定
        android.support.v4.app.NotificationCompat.Builder notification =
                builder.setSmallIcon(R.mipmap.ic_caution)
                        .setContentTitle("不良品発生")
                        .setContentText("タップして詳細を確認")
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                        .setAutoCancel(true)
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(notificationIntent);
        //通知を行う
        int mNotificationId = 001;
        NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(mNotificationId,notification.build());
    }

}
