package com.example.miqa3893.LineManager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;
//TotalLines:154
//ViewPager用（ホーム画面）のFragment
public class HomeFragment extends android.support.v4.app.Fragment{
    private final static String BACKGROUND_COLOR = "background_color";
    RequestQueue mQueue;

    //自宅用
    //final String CONNECTION_URL = "http://192.168.0.8:3939/api/item/get/";    //API接続用文字列（URL）
    final String CONNECTION_URL = "http://10.111.1.160:3939/api/item/get/";    //API接続用文字列（URL）

    private final List itemList = new ArrayList<>();//検査対象物の配列
    private final List<String> itemCodeList = new ArrayList<>();        //検査対象物とリンクする型式番号
    protected View rootView;
    OnClickListener _clickListener;

    public static HomeFragment newInstance(@ColorRes int IdRes) {
        HomeFragment frag = new HomeFragment();
        Bundle b = new Bundle();
        b.putInt(BACKGROUND_COLOR, IdRes);
        frag.setArguments(b);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,//ホーム画面
                             Bundle savedInstanceState) {
        //Log.e("HomeFragment::","View Created.");
        return rootView = inflater.inflate(R.layout.fragment_home, null);//フラグメントの指定;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = (ListView) rootView.findViewById(R.id.lv_item);//リストの定義
        setListView(listView);
        //Log.e("HomeFragment::","ViewList Set.");
    }

    //ListViewにアイテムをセットします
    private void setListView(ListView lv) {
        final ListView listView = lv;
        mQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        mQueue.add(new JsonObjectRequest(Request.Method.GET, CONNECTION_URL
                , null  //jsonRequest（リクエスト先へ一緒に渡すJSON、なければnull指定）
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                addDataToList(response);
            }

            //Parse
            private void addDataToList(JSONObject object) {
                String name;
                String id;

                //JSONパース用コード
                //lengthの長さ分だけ処理を行わないと、1つ少なくなってしまう
                try {
                    JSONArray jsonArray = object.getJSONArray("item");
                    for(int i = 0; i < jsonArray.length() ;i++) {
                        JSONObject temp = (JSONObject) jsonArray.get(i);
                        id = temp.getString("id");
                        name = temp.getString("name");
                        itemCodeList.add(id);
                        itemList.add(name);
                    }
                    listView.setAdapter(new ItemArrayListAdapter(
                            getActivity().getApplicationContext(),
                            R.layout.layout_item_listview,
                            itemList));

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selectedText = itemCodeList.get(position).toString();
                            String selectedItem = itemList.get(position).toString();
                            Log.i("LIST::::","List position >> " + position);
                            Log.i("LIST::::","List content >> " + selectedText);
                            _clickListener.onClick(selectedText,selectedItem);
                        }
                    });//ListViewがタップされたときに認識するためのListener

                    Log.i("RawJSON:::",object.toString());

                } catch (JSONException e) {                     //データの取得ができなかった場合の確認
                    e.printStackTrace();
                    Log.e("debug",object.toString());       //errorならデータベース側で問題がある
                }
            }

        } //Listener
                ,new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Response Failed",error.toString());
                Log.e("volley error",error.toString());
                Toast.makeText(getActivity().getApplicationContext(),"APIを取得できなかったよ！",Toast.LENGTH_SHORT).show();
            }
        }));
    }

    public void onAttach(Context context){
        super.onAttach(context);

        try {
            _clickListener = (OnClickListener)context;
        }catch (ClassCastException e){

        }

    }

    //HomeActivityに挙動を渡す
    public interface OnClickListener{
        void onClick(String selectedText,String selectedItem);
    }
}
