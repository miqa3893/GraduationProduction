package com.example.miqa3893.LineManager;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
//TotalLines:24
public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content,new SettingFragment())
                .commit();
        Log.d("setting","isNotifiable>>" + preferences.getBoolean("notification",true));
        Log.d("setting","syncTime>>" + Integer.parseInt(preferences.getString("syncTime","30")));
        Log.d("setting","failureThreshold>>"+Double.parseDouble(preferences.getString("failureThreshold","6.5")));
        Log.d("setting","GraphFontSize>>"+Integer.parseInt(preferences.getString("graphFontSize","18")));
    }
}
