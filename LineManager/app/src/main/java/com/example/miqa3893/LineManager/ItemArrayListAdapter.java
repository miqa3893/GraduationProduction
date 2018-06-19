package com.example.miqa3893.LineManager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
//TotalLines:48
public class ItemArrayListAdapter extends ArrayAdapter {

    private int mResource;
    private List<String> mItems;
    private LayoutInflater mInflater;

    public ItemArrayListAdapter(@NonNull Context context, int resource, List<String> item) {
        super(context, resource,item);
        mResource = resource;
        mItems = item;
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
        String item = mItems.get(pos);

        // タイトルを設定
        TextView title = (TextView)view.findViewById(R.id.lb_itemList);
        title.setText(item);

        return view;
    }
}
