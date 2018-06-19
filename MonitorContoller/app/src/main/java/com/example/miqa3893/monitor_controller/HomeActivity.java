package com.example.miqa3893.monitor_controller;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.squareup.leakcanary.LeakCanary;

public class HomeActivity extends AppCompatActivity implements HomeFragment.OnCountClickListener,AlertFragment.OnBackClickListener{

    //警告画面と通常画面を切り替えるスレッショルド
    public static double threshold = 6.5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        LeakCanary.install(getApplication());

        //Fragment実装
        HomeFragment homeFragment = new HomeFragment();
        ActionBarFragment actionBarFragment = new ActionBarFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.main_container,homeFragment,"Alert");
        transaction.add(R.id.actionbar_container,actionBarFragment,"ActionBar");
        transaction.commit();
    }

    //Fragmentメモリ解放
    public static final void cleanupView(View view){
        if(view instanceof PieChart){
            ((PieChart) view).setData(null);

        }

        if(view instanceof ViewGroup){
            ViewGroup viewGroup = (ViewGroup)view;
            int size = viewGroup.getChildCount();
            for(int i=0; i<size; i++){
                cleanupView(viewGroup.getChildAt(i));
            }
        }
    }

    //HomeFragment
    @Override
    public void onClick() {
        HandlerThread handlerThread = new HandlerThread("toAlert");
        handlerThread.start();

        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(new Runnable(){
            @Override
            public void run() {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.remove(new HomeFragment());
                transaction.replace(R.id.main_container,new AlertFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    //AlertFragment
    @Override
    public void onClick(int id) {
        HandlerThread handlerThread = new HandlerThread("toHome");
        handlerThread.start();

        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(new Runnable(){
            @Override
            public void run() {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.remove(new AlertFragment());
                transaction.replace(R.id.main_container,new HomeFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }
}
