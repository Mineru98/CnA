package com.cna.mineru.cna.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cna.mineru.cna.DTO.ExamData;
import com.cna.mineru.cna.R;

import java.util.ArrayList;

public class RcExamAdapter extends  RecyclerView.Adapter<RcExamAdapter.ViewHolder> {
    private ArrayList items;

    public RcExamAdapter(Context context, int layout, ArrayList<ExamData> list) {
        items = new ArrayList();
        items=list;
        LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView  title;

        public ViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);

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

    public RcExamAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
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
        holder.title.setText(data.title);
    }

    private void removeItemView(int position) {
        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size()); // 지워진 만큼 다시 채워넣기.
    }

}
