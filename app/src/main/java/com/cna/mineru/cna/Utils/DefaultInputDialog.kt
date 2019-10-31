package com.cna.mineru.cna.Utils

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView

import com.cna.mineru.cna.Adapter.DefaultInputAdapter
import com.cna.mineru.cna.DB.UserSQLClass
import com.cna.mineru.cna.DTO.ListViewBtnItem
import com.cna.mineru.cna.R

import java.util.ArrayList
import java.util.Objects

import android.content.Context.MODE_PRIVATE
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.defaultinput_dialog.*
class DefaultInputDialog : DialogFragment() {
    private var mDialogResult: OnMyDialogResult? = null
    private var lv: ListView? = null
    private var mAdapater: DefaultInputAdapter? = null
    private var mode: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = Objects.requireNonNull(activity)?.getLayoutInflater()

        val dig = inflater?.inflate(R.layout.defaultinput_dialog, null)

        val db = UserSQLClass(context)

        val pref = activity?.getSharedPreferences("isClassConfig", MODE_PRIVATE)
        if (pref != null) {
            mode = pref.getInt("isClassConfig", 0)
        }
        val items = ArrayList<ListViewBtnItem>()
        loadItemsFromDB(items)

        if (dig != null) {
            mAdapater = DefaultInputAdapter(dig.context, R.layout.default_input_item, items)
        }
        lv!!.adapter = mAdapater

        btn_cancel.setOnClickListener {
            val editor = pref?.edit()
            editor?.putInt("isClassConfig", 0)
            editor?.apply()
            this@DefaultInputDialog.dialog?.cancel()
        }

        lv!!.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            if (mode == 0) {
                when (position) {
                    0 -> {
                        val editor1 = pref?.edit()
                        editor1?.putInt("isClassConfig", 1)
                        editor1?.apply()
                        mDialogResult!!.finish(1)
                    }
                    1 -> {
                        val editor2 = pref?.edit()
                        editor2?.putInt("isClassConfig", 2)
                        editor2?.apply()
                        mDialogResult!!.finish(2)
                    }
                    2 -> {
                        val editor3 = pref?.edit()
                        editor3?.putInt("isClassConfig", 3)
                        editor3?.apply()
                        mDialogResult!!.finish(3)
                    }
                }
                this@DefaultInputDialog.dialog?.cancel()
            } else if (mode == 1) {
                when (position) {
                    0 -> {
                        mDialogResult!!.finish(1)
                        db.classId(11)
                        this@DefaultInputDialog.dialog?.cancel()
                    }
                    1 -> {
                        db.classId(12)
                        this@DefaultInputDialog.dialog?.cancel()
                    }
                    2 -> {
                        db.classId(13)
                        this@DefaultInputDialog.dialog?.cancel()
                    }
                    3 -> {
                        db.classId(14)
                        this@DefaultInputDialog.dialog?.cancel()
                    }
                    4 -> {
                        db.classId(15)
                        this@DefaultInputDialog.dialog?.cancel()
                    }
                    5 -> {
                        db.classId(16)
                        this@DefaultInputDialog.dialog?.cancel()
                    }
                }
                val editor = pref?.edit()
                editor?.putInt("isClassConfig", 0)
                editor?.apply()
                this@DefaultInputDialog.dialog?.cancel()
            } else if (mode == 2) {
                when (position) {
                    0 -> {
                        db.classId(21)
                        this@DefaultInputDialog.dialog?.cancel()
                    }
                    1 -> {
                        db.classId(22)
                        this@DefaultInputDialog.dialog?.cancel()
                    }
                    2 -> {
                        db.classId(23)
                        this@DefaultInputDialog.dialog?.cancel()
                    }
                }
                val editor = pref?.edit()
                editor?.putInt("isClassConfig", 0)
                editor?.apply()
                this@DefaultInputDialog.dialog?.cancel()
            } else if (mode == 3) {
                when (position) {
                    0 -> {
                        db.classId(31)
                        this@DefaultInputDialog.dialog?.cancel()
                    }
                    1 -> {
                        db.classId(32)
                        this@DefaultInputDialog.dialog?.cancel()
                    }
                }//                        case 2:
                //                            db.setClassId(33);
                //                            DefaultInputDialog.this.getDialog().cancel();
                //                            break;
                val editor = pref?.edit()
                editor?.putInt("isClassConfig", 0)
                editor?.apply()
                this@DefaultInputDialog.dialog?.cancel()
            }
        }

        builder.setView(dig)
        return builder.create()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = activity?.getSharedPreferences("isClassConfig", MODE_PRIVATE)
        if (pref != null) {
            mode = pref.getInt("isClassConfig", 0)
        }
        if (mode == 1 || mode == 2 || mode == 3) {
            val editor = pref?.edit()
            editor?.putInt("isClassConfig", 0)
            editor?.apply()
        }
    }

    fun loadItemsFromDB(list: ArrayList<ListViewBtnItem>?): Boolean {
        var list = list
        var item: ListViewBtnItem

        if (list == null) {
            list = ArrayList<ListViewBtnItem>()
        }

        if (mode == 0) {
            item = ListViewBtnItem()
            item.text("초등학생")
            list.add(item)
            item = ListViewBtnItem()
            item.text("중학생")
            list.add(item)
            item = ListViewBtnItem()
            item.text("고등학생")
            list.add(item)
        } else if (mode == 1) {
            item = ListViewBtnItem()
            item.text("초등학생 1학년")
            list.add(item)
            item = ListViewBtnItem()
            item.text("초등학생 2학년")
            list.add(item)
            item = ListViewBtnItem()
            item.text("초등학생 3학년")
            list.add(item)
            item = ListViewBtnItem()
            item.text("초등학생 4학년")
            list.add(item)
            item = ListViewBtnItem()
            item.text("초등학생 5학년")
            list.add(item)
            item = ListViewBtnItem()
            item.text("초등학생 6학년")
            list.add(item)
        } else if (mode == 2) {
            item = ListViewBtnItem()
            item.text("중학생 1학년")
            list.add(item)
            item = ListViewBtnItem()
            item.text("중학생 2학년")
            list.add(item)
            item = ListViewBtnItem()
            item.text("중학생 3학년")
            list.add(item)
        } else if (mode == 3) {
            item = ListViewBtnItem()
            item.text("고등학생 1학년")
            list.add(item)
            item = ListViewBtnItem()
            item.text("고등학생 2학년")
            list.add(item)
            //            item = new ListViewBtnItem() ;
            //            item.setText("고등학생 3학년") ;
            //            list.add(item) ;
        }
        return true
    }

    fun setDialogResult(dialogResult: OnMyDialogResult) {
        mDialogResult = dialogResult
    }

    interface OnMyDialogResult {
        fun finish(_class: Int)
    }
}

private operator fun Int.invoke(i: Int) {

}
