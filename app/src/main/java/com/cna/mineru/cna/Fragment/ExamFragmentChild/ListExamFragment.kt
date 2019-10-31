package com.cna.mineru.cna.Fragment.ExamFragmentChild

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.cna.mineru.cna.Adapter.RcExamAdapter
import com.cna.mineru.cna.DB.ExamSQLClass
import com.cna.mineru.cna.DTO.ExamData
import com.cna.mineru.cna.R

import java.util.ArrayList


class ListExamFragment : Fragment() {

    private var db: ExamSQLClass? = null

    lateinit var rv: RecyclerView
    lateinit var mAdapater: RcExamAdapter
    private var linearLayoutManager: LinearLayoutManager? = null

    private var list = ArrayList<ExamData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View? = inflater.inflate(R.layout.fragment_list_exam, container, false)
        rv = view!!.findViewById<View>(R.id.recyclerView) as RecyclerView
        linearLayoutManager = LinearLayoutManager(activity)
        rv.addItemDecoration(
            DividerItemDecoration(
                activity,
                linearLayoutManager!!.orientation
            )
        )
        rv.layoutManager = linearLayoutManager

        db = ExamSQLClass(activity)
        list = db!!.load_values()

        mAdapater = RcExamAdapter(context, R.layout.exam_list_item, list)

        rv.adapter = mAdapater
        return view
    }

    override fun onResume() {
        super.onResume()
        list = db!!.load_values()
        mAdapater = RcExamAdapter(context, R.layout.exam_list_item, list)
        rv.adapter = mAdapater
    }
}
