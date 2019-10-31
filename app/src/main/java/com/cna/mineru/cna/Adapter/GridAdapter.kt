package com.cna.mineru.cna.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

import com.cna.mineru.cna.DTO.HomeData
import com.cna.mineru.cna.R

import java.util.ArrayList

/*
    HomeFragment의 각 Item 요소 표현을 위한 Adapter
    HomeFragment

 */

class GridAdapter(
    internal var context: Context?,
    internal var layout: Int?,
    list: ArrayList<HomeData>?
) : BaseAdapter() {
    internal var inf: LayoutInflater
    internal var items: ArrayList<HomeData> = ArrayList()

    init {
        if (list != null) {
            items = list
        }
        inf = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    fun addItem(item: HomeData) {
        items.add(0, item)
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null)
            convertView = layout?.let { inf.inflate(it, null) }
        val tv = convertView!!.findViewById<View>(R.id.titleText) as TextView
        val btn_count = convertView.findViewById<View>(R.id.btn_count) as RelativeLayout
        val iv_count = convertView.findViewById<View>(R.id.iv_count) as ImageView
        val tv_count = convertView.findViewById<View>(R.id.tv_count) as TextView
        val item = items[position]
        tv.setText(item.title_text)
        //        tv_count.setText(""+db.getNoteCount(position+1));
        tv_count.text = ""
        return convertView
    }
}
