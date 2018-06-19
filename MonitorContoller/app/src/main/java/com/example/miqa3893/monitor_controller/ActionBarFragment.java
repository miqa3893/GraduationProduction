package com.example.miqa3893.monitor_controller;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActionBarFragment extends Fragment {
    View rootView;
    protected long waitTime = 1000;

    public ActionBarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return rootView = inflater.inflate(R.layout.fragment_action_bar, container, false);
    }

    //Viewが生成し終わった時に呼ばれるメソッド
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        final TextView lb_Datetime = rootView.findViewById(R.id.lb_datetime);
        //Typeface font = Typeface.createFromAsset(getActivity().getAssets(),"monitorText.otf");
        //lb_Datetime.setTypeface(font);


        final Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                lb_Datetime.setText(getNowFormattedTime());
                handler.postDelayed(this,waitTime);
            }
        };

        handler.postDelayed(r,waitTime);

    }

    //フォーマットされた日付を返却します
    public static String getNowFormattedTime(){
        Date d = new Date();
        DateFormat defaultDateFormat = new SimpleDateFormat("yyyy年M月dd日  H:mm:ss 現在");
        return defaultDateFormat.format(d).toString();
    }

}
