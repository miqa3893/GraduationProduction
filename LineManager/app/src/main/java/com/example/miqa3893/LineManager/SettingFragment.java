package com.example.miqa3893.LineManager;


import android.os.Bundle;
import android.preference.PreferenceFragment;

//TotalLInes:23
//設定画面の実装用
public class SettingFragment extends PreferenceFragment {


    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }

}
