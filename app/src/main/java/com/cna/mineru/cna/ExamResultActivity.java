package com.cna.mineru.cna;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cna.mineru.cna.DB.ExamSQLClass;
import com.cna.mineru.cna.DTO.ExamData;

import java.util.ArrayList;

public class ExamResultActivity extends AppCompatActivity {

    private ExamSQLClass db;

    private int RoomId;
    private int ExamNum;
    private int[] ExamIdArr;
    private int[] ResultCheckList;
    private long[] ResultExamArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_result);

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        TextView btn_cancel = (TextView) findViewById(R.id.btn_cancel);
        ArrayList<ExamData> list = new ArrayList<>();
        RoomId = getIntent().getIntExtra("RoomId",0);
        db = new ExamSQLClass(this);
        list = db.get_point_values(RoomId);

        for(int i=0;i<list.size();i++){
            Log.d("TAG","Mineru NoteId : " + list.get(i).NoteId + ", Title :"+ list.get(i).Title + ", TimeToSolve : "+ list.get(i).TTS +", isSolved :"+ list.get(i).isSolved);
        }
//        ExamNum = getIntent().getIntExtra("ExamNum", 0);
//        ExamIdArr = getIntent().getIntArrayExtra("ExamIdArr");
//        ResultExamArr = getIntent().getLongArrayExtra("ResultExamArr");
//        ResultCheckList = getIntent().getIntArrayExtra("ResultCheckList");

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
