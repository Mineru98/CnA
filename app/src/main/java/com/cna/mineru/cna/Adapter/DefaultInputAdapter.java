package com.cna.mineru.cna.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cna.mineru.cna.DTO.ExamData;
import com.cna.mineru.cna.DTO.ListViewBtnItem;
import com.cna.mineru.cna.MainActivity;
import com.cna.mineru.cna.R;
import com.cna.mineru.cna.Utils.DefaultInputDialog;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/*
    RcExamAdapter의 각 Item 요소 표현을 위한 Adapter
    ListExamFragment
 */

public class DefaultInputAdapter extends ArrayAdapter{
    int resourceId ;

    public DefaultInputAdapter(Context context, int resource, ArrayList<ListViewBtnItem> list) {
        super(context, resource, list) ;
        this.resourceId = resource ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item2, parent, false);
        }

        TextView tv_class = (TextView) convertView.findViewById(R.id.tv_class);
        RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.layout);
        ListViewBtnItem listViewItem = (ListViewBtnItem) getItem(position);
        tv_class.setText(listViewItem.getText());

        return convertView;
    }

}
