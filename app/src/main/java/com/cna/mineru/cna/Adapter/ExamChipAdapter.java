package com.cna.mineru.cna.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.chip.Chip;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.cna.mineru.cna.DTO.ChipData;
import com.cna.mineru.cna.R;

import java.util.ArrayList;

public class ExamChipAdapter extends BaseAdapter {

    Context mContext;
    int layout;
    LayoutInflater inf;
    ArrayList<ChipData> items;

    public ExamChipAdapter(Context context, int layout, ArrayList<ChipData> list) {
        items = new ArrayList<>();
        mContext = context;
        this.layout = layout;
        items=list;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(ChipData item){
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

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = inf.inflate(layout, null);
        final Chip chip = (Chip) convertView.findViewById(R.id.chip);
        chip.setText(items.get(position).class_title);
        chip.setTextAppearanceResource(R.style.ChipTextStyle);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "제거", Toast.LENGTH_SHORT).show();
                items.remove(position);
                notifyDataSetInvalidated();
            }
        });
        return convertView;
    }
}
