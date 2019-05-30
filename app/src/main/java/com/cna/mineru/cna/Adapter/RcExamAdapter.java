package com.cna.mineru.cna.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cna.mineru.cna.DTO.ExamData;
import com.cna.mineru.cna.ExamResultActivity;
import com.cna.mineru.cna.R;

import java.util.ArrayList;

/*
    RcExamAdapter의 각 Item 요소 표현을 위한 Adapter
    ListExamFragment
 */

public class RcExamAdapter extends RecyclerView.Adapter<RcExamAdapter.ViewHolder> {
    private ArrayList items;

    public RcExamAdapter(Context context, int layout, ArrayList<ExamData> list) {
        items = new ArrayList();
        items = list;
        LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView  title;
        RelativeLayout layout;

        public ViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            layout = (RelativeLayout) itemView.findViewById(R.id.layout);
        }
    }

    public RcExamAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exam_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount(){
        return items.size();
    }

    public void addItem(ExamData item){
        items.add(0,item);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ExamData data = (ExamData) items.get(position);

        // 데이터 결합
        holder.title.setText(data.ExamTitle);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ExamResultActivity.class);
                i.putExtra("RoomId",data.RoomId);
                v.getContext().startActivity(i);
            }
        });

        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

    private void removeItemView(int position) {
        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size()); // 지워진 만큼 다시 채워넣기.
    }

}
