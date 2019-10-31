package com.cna.mineru.cna.Utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputLayout

import com.cna.mineru.cna.DB.UserSQLClass
import com.cna.mineru.cna.R

import java.util.Objects
import kotlinx.android.synthetic.main.custom_dialog2.*
@SuppressLint("ValidFragment")
class CustomDialog : DialogFragment {
    private var mDialogResult: OnMyDialogResult? = null
    private val loadingDialog: LoadingDialog? = null

    private var btn_ok: TextView? = null
    private var btn_cancel: TextView? = null

    private var layout_email: TextInputLayout? = null
    private var et_email: AppCompatEditText? = null

    private var str_tv_title = ""
    private var str_tv_subtitle = ""

    @SuppressLint("ValidFragment")
    constructor(code: Int, time: Int) {
        if (code == 5) {
            str_tv_title = "알림"
            str_tv_subtitle = "" + time + "분간 시험이 진행됩니다.\n이대로 진행 하시겠습니까?"
        }
    }

    @SuppressLint("ValidFragment")
    constructor(code: Int) {
        if (code == 1) {
            str_tv_title = "로그아웃"
            str_tv_subtitle = "로그아웃 하시겠습니까?"
        } else if (code == 2) {
            str_tv_title = "회원탈퇴"
            str_tv_subtitle = "회원탈퇴 하시겠습니까?"
        } else if (code == 3) {
            str_tv_title = "비밀번호 변경요청"
            str_tv_subtitle = "이메일을 입력해주시면 해당 메일로 변경메일이 날라갑니다."
        } else if (code == 4) {
            str_tv_title = "인터넷 연결 오류"
            str_tv_subtitle = "인터넷 연결 상태를 확인해 주세요.\n인터넷 설정으로 이동하시겠습니까?"
        } else if (code == 5) {
            str_tv_title = "알림"
            str_tv_subtitle = "분간 시험이 진행됩니다.\n이대로 진행 하시겠습니까?"
        } else if (code == 6) {
            str_tv_title = "오답노트 삭제"
            str_tv_subtitle = "정말로 삭제하시겠습니까?"
        } else if (code == 7) {
            str_tv_title = "쿠폰 추가 등록"
            str_tv_subtitle = "이미 등록 된 쿠폰이 있습니다.\n그래도 등록하시겠습니까?"
        } else if (code == 8) {
            str_tv_title = "입력 오류"
            str_tv_subtitle = "비밀번호가 일치하지 않습니다."
        } else if (code == 9) {
            str_tv_title = "시험 종료"
            str_tv_subtitle = "시험을 완료하시겠습니까?\n'예'를 누르면 시험이 종료됩니다."
        } else if (code == 10) {
            str_tv_title = "종료"
            str_tv_subtitle = "이대로 종료하시면 정답을 매기지 않는 모든 문제는 오답처리가 됩니다.\n그래도 종료하시겠습니까?"
        } else if (code == 11) {
            str_tv_title = "시험 시간 종료"
            str_tv_subtitle = "할당된 시간이 다 되었습니다.\n시험을 종료합니다."
        } else if (code == 12) {
            str_tv_title = "시험 종료"
            str_tv_subtitle = "주어진 문제를 모두 풀었습니다.\n시험을 종료합니다."
        } else if (code == 13) {
            str_tv_title = "입력 오류"
            str_tv_subtitle = "단원을 입력해주세요."
        } else if (code == 14) {
            str_tv_title = "시험 종료"
            str_tv_subtitle = "예를 누르시면 시험 결과로 바로 넘어갑니다.\n아니오를 누르시면 시험 결과는 저장되고 결과는 나중에 계속 볼 수 있습니다."
        } else if (code == 15) {
            str_tv_title = "종료"
            str_tv_subtitle = "이대로 종료하시면 시험 결과가 저장되지 않습니다.\n그래도 종료하시겠습니까?"
        } else if (code == 16) {
            str_tv_title = "프로필 설정"
            str_tv_subtitle = "사용자 이름을 변경해 주세요."
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = Objects.requireNonNull(activity)?.layoutInflater
        if (str_tv_title == "비밀번호 변경요청") {
            val dig = inflater?.inflate(R.layout.custom_dialog2, null)

            tv_title.text = str_tv_title
            tv_subtitle.text = str_tv_subtitle

            btn_ok!!.text = "전송"
            btn_cancel!!.text = "취소"

            btn_ok!!.setOnClickListener {
                mDialogResult!!.finish(1, et_email!!.getText().toString())
                this@CustomDialog.dismiss()
            }

            btn_cancel!!.setOnClickListener {
                mDialogResult!!.finish(0, "")
                this@CustomDialog.dismiss()
            }
            builder.setView(dig)
        } else if (str_tv_title == "프로필 설정") {
            val dig = inflater?.inflate(R.layout.custom_dialog2, null)
            tv_title.text = str_tv_title
            tv_subtitle.text = str_tv_subtitle
            val db = UserSQLClass(context)
            et_email!!.hint = db._Name
            et_email!!.inputType = InputType.TYPE_CLASS_TEXT
            layout_email!!.hint = "이름"
            btn_ok!!.text = "완료"
            btn_cancel!!.text = "취소"

            btn_ok!!.setOnClickListener {
                mDialogResult!!.finish(1, et_email!!.getText().toString())
                this@CustomDialog.dismiss()
            }

            btn_cancel!!.setOnClickListener {
                mDialogResult!!.finish(0, "")
                this@CustomDialog.dismiss()
            }
            builder.setView(dig)
        } else if (str_tv_title == "인터넷 연결 오류") {
            val dig = inflater?.inflate(R.layout.custom_dialog, null)
            val tv_title = dig?.findViewById(R.id.tv_title) as TextView
            val tv_subtitle = dig.findViewById(R.id.tv_subtitle) as TextView
            tv_title.text = str_tv_title
            tv_subtitle.text = str_tv_subtitle

            btn_ok = dig.findViewById(R.id.btn_ok)
            btn_cancel = dig.findViewById(R.id.btn_cancel)

            btn_ok!!.setOnClickListener {
                mDialogResult!!.finish(1)
                this@CustomDialog.dismiss()
            }

            btn_cancel!!.setOnClickListener {
                mDialogResult!!.finish(0)
                this@CustomDialog.dismiss()
            }
            builder.setView(dig)
        } else if (str_tv_subtitle == "비밀번호가 일치하지 않습니다." || str_tv_subtitle == "단원을 입력해주세요." || str_tv_title == "시험 시간 종료") {
            val dig = inflater?.inflate(R.layout.custom_dialog, null)
            val tv_title = dig?.findViewById(R.id.tv_title) as TextView
            val tv_subtitle = dig.findViewById(R.id.tv_subtitle) as TextView
            tv_title.text = str_tv_title
            tv_subtitle.text = str_tv_subtitle

            btn_ok = dig.findViewById(R.id.btn_ok)
            btn_cancel = dig.findViewById(R.id.btn_cancel)
            btn_ok!!.visibility = View.INVISIBLE
            btn_cancel!!.text = "확인"

            btn_cancel!!.setOnClickListener {
                mDialogResult!!.finish(0)
                this@CustomDialog.dismiss()
            }
            builder.setView(dig)
        } else {
            val dig = inflater?.inflate(R.layout.custom_dialog, null)
            val tvTitle = dig?.findViewById(R.id.tv_title) as TextView
            val tvSubtitle = dig.findViewById(R.id.tv_subtitle) as TextView
            tvTitle.text = str_tv_title
            tvSubtitle.text = str_tv_subtitle

            btn_ok = dig.findViewById(R.id.btn_ok)
            btn_cancel = dig.findViewById(R.id.btn_cancel)

            btn_ok!!.setOnClickListener {
                mDialogResult!!.finish(1)
                this@CustomDialog.dismiss()
            }

            btn_cancel!!.setOnClickListener {
                mDialogResult!!.finish(0)
                this@CustomDialog.dismiss()
            }
            builder.setView(dig)
        }

        return builder.create()
    }

    fun setDialogResult(dialogResult: OnMyDialogResult) {
        mDialogResult = dialogResult
    }

    interface OnMyDialogResult {
        fun finish(result: Int)
        fun finish(result: Int, email: String)
    }

}
