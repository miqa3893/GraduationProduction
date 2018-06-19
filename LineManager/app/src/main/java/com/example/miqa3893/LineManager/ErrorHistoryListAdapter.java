package com.example.miqa3893.LineManager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
//TotalLines:50
public class ErrorHistoryListAdapter extends ArrayAdapter {

    private int mResource;
    private List<IHistory> mIHistory;
    private LayoutInflater mInflater;

    public ErrorHistoryListAdapter(@NonNull Context context, int resource, List<IHistory> item) {
        super(context, resource,item);
        mResource = resource;
        mIHistory = item;
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
        IHistory IHistory = mIHistory.get(pos);

        //データをセット
        TextView lb_errorDate = (TextView)view.findViewById(R.id.lb_errorDate);
        TextView lb_serial = (TextView)view.findViewById(R.id.lb_serial);

        lb_errorDate.setText(IHistory.getDate());
        lb_serial.setText(IHistory.getSerial());

        return view;
    }
}
