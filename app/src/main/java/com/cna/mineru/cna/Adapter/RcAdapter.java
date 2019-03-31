package com.cna.mineru.cna.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cna.mineru.cna.DTO.PlanData;
import com.cna.mineru.cna.R;

import java.util.ArrayList;

/*
    RcAdapter의 각 Item 요소 표현을 위한 Adapter
    PlanFragment

 */

public class RcAdapter extends  RecyclerView.Adapter<RcAdapter.ViewHolder> {
    private ArrayList items;

    public RcAdapter(Context context, int layout, ArrayList<PlanData> list) {
        items = new ArrayList();
        items=list;
        LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView  title;
        RelativeLayout layout;
        public ViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            layout = (RelativeLayout) itemView.findViewById(R.id.layout);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return false;
                }
            });
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount(){
        return items.size();
    }

    public void addItem(PlanData item){
        items.add(0,item);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        PlanData data = (PlanData) items.get(position);

        // 데이터 결합
        holder.title.setText(data.title);
    }

    private void removeItemView(int position) {
        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size()); // 지워진 만큼 다시 채워넣기.
    }

}
