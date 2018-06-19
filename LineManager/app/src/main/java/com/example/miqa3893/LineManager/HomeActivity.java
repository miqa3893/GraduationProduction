package com.example.miqa3893.LineManager;

//九州職業能力開発大学校　応用課程　生産系開発課題
//三次元検査システムの開発（さんじげんちほー）
//モジュール名    ：管理者端末アプリケーション
//製作者           ：長住・永松
//更新日           ：2017-11-27
//合計ソース行数   ：1,291

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static com.example.miqa3893.LineManager.DetailActivity.DROP_CONNECTION_URL;
public class HomeActivity extends AppCompatActivity implements HomeFragment.OnClickListener {
    //通知を行う基準不良率
    public static double threshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ToolBarの初期化
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        setTitle("ホーム");

        //serviceの開始
        Intent intent = new Intent(getApplicationContext(),NotificationService.class);
        startService(intent);

        //ViewPagerの初期化
        FragmentManager manager = getSupportFragmentManager();
        FragmentPagerAdapter adapter = new MyFragmentPagerAdapter(manager);//アダプターの取得
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);//ViewPagerの指定
        viewPager.setAdapter(adapter);//アダプターのセット
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);//tabの指定
        tabLayout.setupWithViewPager(viewPager);
    }

    //OptionMenuを作る
    public boolean onCreateOptionsMenu(Menu menu) {//オプションメニューの表示
        getMenuInflater().inflate(R.menu.main_menu,menu);//オプションメニューのセット
        return super.onCreateOptionsMenu(menu);
    }

    //アクションバーのオプションを選択した時の挙動
    public boolean onOptionsItemSelected(MenuItem item) {//オプションメニューが押された表示
        switch (item.getItemId()){
            case R.id.action_setting:
                Intent intent = new Intent(HomeActivity.this,SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.action_allDelete:
                final AlertDialog.Builder alertDlg = new AlertDialog.Builder(HomeActivity.this);
                alertDlg.setTitle("計測詳細の消去");
                alertDlg.setMessage("検査履歴を全消しします\n※この操作はもとに戻せません！");
                //全消し
                alertDlg.setPositiveButton("イグニッション！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dropDataFromApi();
                    }
                });
                alertDlg.setNegativeButton("やっぱりやめる", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDlg.create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //データベースからデータを消去します
    private void dropDataFromApi() {
        String url = DROP_CONNECTION_URL + "0000-00-00" + "/all";
        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
        //URL例：http://10.111.1.39:3939/api/delete/drop/0000-00-00/all
        mQueue.add(new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        if(Objects.equals(response, "successed.")) Toast.makeText(getApplicationContext(),"消去しました!",Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", error.toString());
                    }
                }));
    }

    @Override
    public void onClick(String selectedText,String selectedItem) {
        Intent intent = new Intent(HomeActivity.this,DetailActivity.class);
        intent.putExtra("key",selectedText);
        intent.putExtra("item",selectedItem);
        startActivity(intent);

    }
}