package com.cna.mineru.cna.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.TextView

import com.cna.mineru.cna.DTO.ListViewBtnItem
import com.cna.mineru.cna.R

import java.util.ArrayList

/*
    RcExamAdapter의 각 Item 요소 표현을 위한 Adapter
    ListExamFragment
 */

class DefaultInputAdapter(
    context: Context,
    internal var resourceId: Int,
    list: ArrayList<ListViewBtnItem>
) : ArrayAdapter<ListViewBtnItem>(context, resourceId, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val context = parent.context

        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.default_input_item, parent, false)
        }

        val tv_class = convertView!!.findViewById<View>(R.id.tv_class) as TextView
        val layout = convertView.findViewById<View>(R.id.layout) as RelativeLayout
        val listViewItem = getItem(position)
        tv_class.setText(listViewItem!!.text)

        return convertView
    }

}
