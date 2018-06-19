package com.example.miqa3893.LineManager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Double.parseDouble;
public class DetailActivity extends AppCompatActivity{
    private static ProgressDialog waitDialog;
    //設定を読み込む
    private SharedPreferences preferences;
    protected String dateTime;
    DateFormat dateFormat;
    private String selectedText,selectedItem;
    private ListView listView;
    //グラフ描画用
    protected ArrayList<Float> pieData = new ArrayList<>();
    protected ArrayList<Entry> entry = new ArrayList<>();
    //通信用
    RequestQueue mQueue;
    JSONObject jsonObject;
    Monitor monitor;

    //学内用
    final static String HISTORY_CONNECTION_URL = "http://10.111.1.160:3939/api/history/get/";
    final static String MEASURE_CONNECTION_URL = "http://10.111.1.160:3939/api/monitor/get/";
    final static String DROP_CONNECTION_URL = "http://10.111.1.160:3939/api/delete/drop/";

    //自宅用
    //final protected String HISTORY_CONNECTION_URL = "http://192.168.0.8:3939/api/history/get/";
    //final protected String DATA_CONNECTION_URL = "http://192.168.0.8:3939/api/monitor/get/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Activityスタート時に、HomeActivityから渡された部品パラメータを取得
        Intent intent = getIntent();
        selectedText = intent.getStringExtra("key");
        selectedItem = intent.getStringExtra("item");
        Log.d("DetailActivity::","Activity Start.");
        Log.d("DetailActivity::","Selected Text >> " + selectedText);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //mQueueを初期化
        mQueue = Volley.newRequestQueue(getApplicationContext());

        //日付のフォーマット設定
        Date d = new Date();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //ActionBarの変更
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        setTitle(selectedItem + "詳細\t\t" + dateFormat.format(d) + "　検査状況");

        //各種View IDの取得
        listView = (ListView) findViewById(R.id.lv_detail);
        FloatingActionButton fb_update = (FloatingActionButton)findViewById(R.id.fb_update);

        //dateTimeをパラメータにしてAPIへアクセス
        dateTime = dateFormat.format(d).toString();
        //最初の1回実行
        updateData();

        fb_update.setOnClickListener(new View.OnClickListener() {//ボタンで更新
            @Override
            public void onClick(View v) {
                updateData();
            }
        });
    }

    //更新用処理まとめ
    private void updateData(){
        setWait();          //検査履歴更新処理

        //初期化
        List<IHistory> errorList = new ArrayList<>();
        listView.setAdapter(new ErrorHistoryListAdapter(
                getApplicationContext(),
                R.layout.layout_listview_errorhistory,
                errorList));

        //UIスレッド上で動くようにバックグラウンド処理
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                getDataFromApi(selectedText,dateTime);
                getErrorDataFromApi(selectedText,dateTime);
            }
        });
    }

    //通信を行い、データを取得する
    //これはNG検査履歴用
    private void getErrorDataFromApi(String selectedText, String dateTime) {
        //詳細に出すList
        final List<IHistory> errorList = new ArrayList<>();

        //アクセス用アドレス
        //EX: http://10.111.1.39:3939/api/history/get/2017-08-31/A001
        String url = HISTORY_CONNECTION_URL + dateTime + "/" + selectedText;
        Log.d("ConnectionURL",url);

        //Volley通信テンプレ　ここから
        mQueue.add(new JsonObjectRequest(Request.Method.GET, url
                , null  //jsonRequest（リクエスト先へ一緒に渡すJSON、なければnull指定）
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                addDataToList(response);
            }

            //Parse
            private void addDataToList(JSONObject object) {
                String date,serial,name,result;
                int point;
                int machineId;

                double correct;
                double measure;

                DecimalFormat df = new DecimalFormat("###.0000");

                Log.d("RawJSON:::",object.toString());

                //JSONパース用コード
                //lengthの長さ分だけ処理を行わないと、1つ少なくなってしまう
                try {
                    JSONArray jsonArray = object.getJSONArray("history");
                    for(int i = 0; i < jsonArray.length() ;i++) {
                        List<MeasureData> list = new ArrayList<>();
                        JSONObject temp = (JSONObject) jsonArray.get(i);      //変換可能にするため、JSONArrayをJSONObjectに変換する
                        serial = temp.getString("serial");
                        date = temp.getString("date");
                        name = temp.getString("name");
                        machineId = Integer.parseInt(temp.getString("machine"));

                        JSONArray jsonMeasureArray = temp.getJSONArray("measures");
                        //詳細データを入手する
                        for(int j = 0; j < jsonMeasureArray.length(); j++){
                            //list.clear();
                            JSONObject w = (JSONObject) jsonMeasureArray.get(j);
                            point = Integer.parseInt(w.getString("point"));
                            correct = Double.parseDouble(df.format(Double.parseDouble(w.getString("correct"))));
                            measure = Double.parseDouble(df.format(Double.parseDouble(w.getString("measure"))));
                            result = w.getString("result");
                            list.add(j,new MeasureData(point,correct,measure,result));
                        }
                        //データを入れ、一時的リスト（measureList）を初期化
                        errorList.add(new IHistory(serial,date,name,machineId,list));
                    }

                    //独自にカスタマイズしたArrayAdapterを用いてビューをセット
                    listView.setAdapter(new ErrorHistoryListAdapter(
                            getApplicationContext(),
                            R.layout.layout_listview_errorhistory,
                            errorList));
                    waitDialog.dismiss();

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //ダイアログボックスで詳細情報を見せる
                            LayoutInflater inflater = (LayoutInflater) getApplication().getSystemService(LAYOUT_INFLATER_SERVICE);
                            final View layout = inflater.inflate(R.layout.dialog_item_detail_layout,(ViewGroup) findViewById(R.id.detailListDialog));//すべての検査ポイントのリストダイアログ

                            final AlertDialog.Builder alertDlg = new AlertDialog.Builder(DetailActivity.this);
                            //dialogのタイトルをリストから直接指定
                            //ex. A001-01-0001 詳細データ
                            alertDlg.setTitle(errorList.get(position).getSerial() + " 計測詳細データ");
                            alertDlg.setView(layout);//カスタマイズしたダイアログをセット

                            TextView dl_worker = (TextView)layout.findViewById(R.id.dl_lb_worker);
                            ListView dl_measureList = (ListView)layout.findViewById(R.id.dl_measureList);
                            //加工者と加工機IDのセット
                            dl_worker.setText(errorList.get(position).getName() + "／" + errorList.get(position).getMachineId());
                            dl_measureList.setAdapter(new MeasureListAdapter(
                                    getApplicationContext(),
                                    R.layout.layout_listview_error_item_detail,
                                    errorList.get(position).getList()
                            ));

                            //Clickされたときの処理…何も書かなければdismissと同じ
                            alertDlg.setPositiveButton("閉じる",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,int which) {

                                }
                            });
                            alertDlg.create().show();
                        }
                    });

                } catch (JSONException e) {                     //データの取得ができなかった場合の確認
                    e.printStackTrace();
                    Log.e("debug",object.toString());       //errorならデータベース側で問題がある
                    waitDialog.dismiss();
                }
            }

        } //Listener
                ,new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response Failed",error.toString());
                Log.e("volley error",error.toString());
                waitDialog.dismiss();
                Toast.makeText(getApplicationContext(),"APIを取得できなかったよ！",Toast.LENGTH_SHORT).show();
            }
        }));
        //Volley通信テンプレ　ここまで

    }

    //これは日付の検査数取得用
    private void getDataFromApi(String selectedText, final String dateTime){
        monitor = new Monitor();
        //URL例：http://10.111.1.39:3939/api/monitor/get/2017-11-17/A001
        mQueue.add(new JsonObjectRequest(Request.Method.GET, MEASURE_CONNECTION_URL + dateTime + "/" + selectedText
                , null  //jsonRequest（リクエスト先へ一緒に渡すJSON、なければnull指定）
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("RawJson:::",response.toString());
                jsonObject = response;      //取得できたら、レスポンスを変数に代入
                if(jsonObject.length()>0){
                    parseJSON(jsonObject);
                    createPie();

                    //ビューワーにデータをセット
                    TextView lb_total = (TextView)findViewById(R.id.lb_total);
                    TextView lb_correct = (TextView)findViewById(R.id.lb_correct);
                    TextView lb_failure = (TextView)findViewById(R.id.lb_failure);
                    setTitle(selectedItem + "詳細\t\t" + dateTime + "　生産状況");
                    lb_total.setText(String.valueOf((int)monitor.getTotal()));
                    lb_correct.setText(String.valueOf((int)monitor.getCorrect()));
                    lb_failure.setText(String.valueOf((int)monitor.getFailure()));
                }
            }

        } //Listener
                ,new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("volley error::",error.toString());
                Toast.makeText(DetailActivity.this,"データを取得できなかったよ！",Toast.LENGTH_SHORT).show();
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

    //円グラフを作る
    private void createPie() {
        PieChart pieChart = (PieChart)findViewById(R.id.pieChart);

        //まず、pieDataとentryを初期化
        pieData.clear();
        entry.clear();

        //デフォルトデータのセット
        pieData.add(monitor.getCorrect());
        pieData.add(monitor.getFailure());

        //円グラフのラベルをセットする
        ArrayList<String> label = new ArrayList<>();
        label.add("良品");
        label.add("不良品");

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
        PieData pieData = new PieData(label,dataSet);
        //データラベルのテキストサイズ
        float graphFontSize = Float.parseFloat(preferences.getString("graphFontSize","18"));
        pieData.setValueTextSize(graphFontSize);
        pieData.setValueFormatter(new PercentFormatter());
        //pieData.setValueTypeface(Typeface.createFromAsset(getAssets(),"graphFont.TTF"));
        pieChart.setData(pieData);

        //ドーナツホール
        pieChart.setDrawHoleEnabled(true);              //ドーナツを作るかどうか
        pieChart.setTransparentCircleRadius(42f);       //半透明なドーナツの直径
        pieChart.setHoleRadius(40f);                    //透明なドーナツの直径

        //表示される際のアニメーション効果時間をミリ秒で設定
        pieChart.animateY(1500);
    }

    //ProgressDialogを作る
    private void setWait() {
        //更新のダイアログ
        // プログレスダイアログの設定
        waitDialog = new ProgressDialog(this);        // プログレスダイアログのメッセージを設定します
        waitDialog.setMessage("更新中...");                    // 円スタイル（くるくる回るタイプ）に設定します
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);        // プログレスダイアログを表示
        waitDialog.show();
    }

    //OptionMenuを作る
    public boolean onCreateOptionsMenu(Menu menu) {//オプションメニューの表示
        getMenuInflater().inflate(R.menu.detail_menu,menu);//オプションメニューのセット
        return super.onCreateOptionsMenu(menu);
    }

    //アクションバーのオプションを選択した時の挙動
    public boolean onOptionsItemSelected(MenuItem item) {//オプションメニューが押された表示
        switch (item.getItemId()){
            case R.id.action_setting:
                Intent intent = new Intent(DetailActivity.this,SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.action_dateSetting:
                //今日の日付に設定する
                Calendar calendar = Calendar.getInstance();//カレンダー取得
                int year = calendar.get(Calendar.YEAR);//年の取得
                int month = calendar.get(Calendar.MONTH);//月の取得
                int day = calendar.get(Calendar.DAY_OF_MONTH);//日の取得
                DatePickerDialog datePickerDialog = new DatePickerDialog(this,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view,int year,int monthOfyear,int dayOfMonth) {//DateTimePickerが押されたら
                        //Pickerで取得した日付をパラメータにしてAPIへ接続
                        dateTime = year+ "-" + (monthOfyear+1) + "-" + dayOfMonth;
                        updateData();
                    }
                },year,month,day);
                datePickerDialog.show();//ピッカ表示
                break;
            case R.id.action_delete:
                final AlertDialog.Builder alertDlg = new AlertDialog.Builder(DetailActivity.this);
                alertDlg.setTitle(selectedItem + " 計測詳細の消去");
                alertDlg.setMessage("検査履歴を全消しします\n※この操作はもとに戻せません！");
                //全消し
                alertDlg.setPositiveButton("イグニッション！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dropDataFromApi(true);
                    }
                });
                alertDlg.setNeutralButton("今日の日付だけ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dropDataFromApi(false);
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
    private void dropDataFromApi(boolean flg) {
        String url;
        if(flg) url = DROP_CONNECTION_URL + "0000-00-00" + "/" + selectedText;
        else url = DROP_CONNECTION_URL + dateFormat.format(new Date()) + "/" + selectedText;

        //URL例：http://10.111.1.39:3939/api/delete/drop/2017-12-12/A001
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
}