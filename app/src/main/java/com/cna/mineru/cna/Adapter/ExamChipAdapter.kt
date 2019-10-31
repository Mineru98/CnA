package com.cna.mineru.cna.Adapter

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.material.chip.Chip
//import android.support.design.chip.Chip
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast

import com.cna.mineru.cna.DTO.ChipData
import com.cna.mineru.cna.R

import java.util.ArrayList

class ExamChipAdapter(
    internal var mContext: Context,
    internal var layout: Int,
    list: ArrayList<ChipData>
) : BaseAdapter() {
    internal var inf: LayoutInflater
    internal var items: ArrayList<ChipData>

    init {
        items = ArrayList<ChipData>()
        items = list
        inf = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    fun addItem(item: ChipData) {
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
            convertView = inf.inflate(layout, null)
        val chip = convertView!!.findViewById<View>(R.id.chip) as Chip
        chip.setText(items[position].class_title)
        chip.setTextAppearanceResource(R.style.ChipTextStyle)
        chip.setOnCloseIconClickListener(View.OnClickListener {
            Toast.makeText(mContext, "제거", Toast.LENGTH_SHORT).show()
            items.removeAt(position)
            notifyDataSetInvalidated()
        })
        return convertView
    }
}
