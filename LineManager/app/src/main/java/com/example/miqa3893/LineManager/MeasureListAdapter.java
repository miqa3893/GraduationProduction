package com.example.miqa3893.LineManager;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;
//TotalLines:57
public class MeasureListAdapter extends ArrayAdapter {

    private int mResource;
    private List<MeasureData> measureData;
    private LayoutInflater mInflater;

    public MeasureListAdapter(@NonNull Context context, int resource, List<MeasureData> item) {
        super(context, resource,item);
        mResource = resource;
        measureData = item;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent){
        View view;

        if (convertView != null) {
            view = convertView;
        }
        else {
            view = mInflater.inflate(mResource, null);
        }

        // リストビューに表示する要素を取得
        MeasureData data = measureData.get(pos);

        //データをセット
        TextView list_point = (TextView)view.findViewById(R.id.li_lb_point);
        TextView list_result = (TextView)view.findViewById(R.id.li_lb_result);
        TextView list_correct = (TextView)view.findViewById(R.id.li_lb_correct);
        TextView list_measure = (TextView)view.findViewById(R.id.li_lb_measure);

        list_point.setText(String.valueOf(data.getPoint()));
        list_result.setText(data.getResult());
        list_correct.setText(String.valueOf(data.getCorrect()));
        list_measure.setText(String.valueOf(data.getMeasure()));

        return view;
    }
}
