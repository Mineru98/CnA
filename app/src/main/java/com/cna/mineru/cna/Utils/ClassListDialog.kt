package com.cna.mineru.cna.Utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ExpandableListView
import android.widget.TextView
import androidx.fragment.app.DialogFragment

import com.cna.mineru.cna.Adapter.ExpandableListAdapter
import com.cna.mineru.cna.DB.GraphDataSQLClass
import com.cna.mineru.cna.DB.UserSQLClass
import com.cna.mineru.cna.DTO.ClassData
import com.cna.mineru.cna.R

import java.util.ArrayList
import java.util.Calendar
import java.util.HashMap
import java.util.Objects
import kotlinx.android.synthetic.main.classlist_dialog.*
@Suppress("DEPRECATED_IDENTITY_EQUALS")
class ClassListDialog : DialogFragment() {
    private var mDialogResult: OnMyDialogResult? = null
    private var lvExp: ExpandableListView? = null
    private var u_db: UserSQLClass? = null
    private var c_db: GraphDataSQLClass? = null
    private val ClassId: Int = 0
    private var month: Int = 0
    private var cal: Calendar? = null
    private var listAdapter: ExpandableListAdapter? = null
    private var listDataHeader: MutableList<String>? = null
    private var listDataChild: HashMap<String, List<String>>? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = Objects.requireNonNull(activity)?.layoutInflater

        val dig = inflater?.inflate(R.layout.classlist_dialog, null)

        u_db = UserSQLClass(this.getContext())
        c_db = GraphDataSQLClass(this.getActivity())

        cal = Calendar.getInstance()
        month = Calendar.getInstance().get(Calendar.MONTH) + 1
        prepareListData(u_db!!.classId)

        listAdapter = ExpandableListAdapter(getActivity(), listDataHeader, listDataChild)
        lvExp!!.setAdapter(listAdapter)

        btn_cancel.setOnClickListener { this@ClassListDialog.dialog?.cancel() }

        lvExp!!.setOnTouchListener { v, event -> false }

        lvExp!!.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            mDialogResult!!.finish(
                c_db!!.getTag(listDataChild!![listDataHeader!![groupPosition]]!![childPosition]),
                c_db!!.getSubTag(listDataChild!![listDataHeader!![groupPosition]]!![childPosition]),
                listDataChild!![listDataHeader!![groupPosition]]!![childPosition]
            )
            this@ClassListDialog.dismiss()
            false
        }

        lvExp!!.setOnGroupExpandListener { }

        lvExp!!.setOnGroupCollapseListener { }

        builder.setView(dig)
        return builder.create()
    }

    private fun prepareListData(classId: Int) {
        var sub_list: ArrayList<ArrayList<String>>? = null
        val t_list: ArrayList<ClassData>
        val t_list2: ArrayList<ClassData>
        var count = 1

        listDataHeader = ArrayList()
        listDataChild = HashMap()

        t_list = c_db!!.set_title(classId, 0, month)
        for (i in t_list.indices) {
            listDataHeader!!.add(t_list[i].Title)
        }

        t_list2 = c_db!!.set_title(classId, 1, month)
        sub_list = ArrayList(c_db!!.get_size(classId,1,month))
        for (i in sub_list.indices)
            sub_list[i] = ArrayList()

        var i = 0
        var j = 0
        while (c_db!!.get_size(classId, 0, month) > j) {
            if (c_db!!.get_size(classId, 1, month) - 1 === i) {
                sub_list[j].add(t_list2[i].Title)
                listDataChild!![listDataHeader!![j]] = sub_list[j]
                break
            }

            if (count == t_list2[i].Tag) {
                sub_list[j].add(t_list2[i].Title)
                i++
            } else {
                listDataChild!![listDataHeader!![j]] = sub_list[j]
                count++
                j++
            }
        }
    }

    fun setDialogResult(dialogResult: OnMyDialogResult) {
        mDialogResult = dialogResult
    }

    interface OnMyDialogResult {
        fun finish(Tag: Int, SubTag: Int, result: String)
    }
}
