package com.cna.mineru.cna.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cna.mineru.cna.DTO.HomeData;
import com.cna.mineru.cna.R;

import java.util.ArrayList;

/*
    HomeFragment의 각 Item 요소 표현을 위한 Adapter
    HomeFragment

 */

public class GridAdapter extends BaseAdapter {
    Context context;
    int layout;
    LayoutInflater inf;

    ArrayList<HomeData> items;
    public GridAdapter(Context context, int layout, ArrayList<HomeData> list) {
        items = new ArrayList();
        this.context = context;
        this.layout = layout;
        items=list;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(HomeData item){
        items.add(0,item);
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView==null)
            convertView = inf.inflate(layout, null);
        TextView tv = (TextView)convertView.findViewById(R.id.titleText);
        HomeData item = items.get(position);
        tv.setText(item.title_text);

        return convertView;
    }
}
