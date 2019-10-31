package com.cna.mineru.cna.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.cna.mineru.cna.DTO.ExamData
import com.cna.mineru.cna.ExamResultActivity
import com.cna.mineru.cna.R

import java.util.ArrayList

/*
    RcExamAdapter의 각 Item 요소 표현을 위한 Adapter
    ListExamFragment
 */

class RcExamAdapter(context: Context?, layout: Int, list: ArrayList<ExamData>) :
    RecyclerView.Adapter<RcExamAdapter.ViewHolder>() {

    private var items: ArrayList<ExamData>? = null

    override fun getItemCount(): Int {
        return items!!.size
    }
    init {
        items = arrayListOf()
        items = list
        val inf = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById<View>(R.id.tv_title) as TextView
        var layout: RelativeLayout = itemView.findViewById<View>(R.id.layout) as RelativeLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RcExamAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.exam_list_item, parent, false)
        return ViewHolder(view)
    }

    fun addItem(item: ExamData) {
        items!!.add(0, item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = items!![position] as ExamData

        // 데이터 결합
        holder.title.setText(data.ExamTitle)
        holder.layout.setOnClickListener { v ->
            val i = Intent(v.context, ExamResultActivity::class.java)
            i.putExtra("RoomId", data.RoomId)
            v.context.startActivity(i)
        }

        holder.layout.setOnLongClickListener { false }
    }

    private fun removeItemView(position: Int) {
        items!!.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items!!.size) // 지워진 만큼 다시 채워넣기.
    }

}
