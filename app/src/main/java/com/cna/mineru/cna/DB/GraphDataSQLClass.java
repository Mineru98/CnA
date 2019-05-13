package com.cna.mineru.cna.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AppCompatActivity;

import com.cna.mineru.cna.DTO.ClassData;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class GraphDataSQLClass extends AppCompatActivity {

    SQLiteDatabase sqliteDb;
    Context context;

    public GraphDataSQLClass(Context context) {
        this.context = context;
        sqliteDb = init_database(context);
        init_Tables();
    }

    private SQLiteDatabase init_database(Context context){
        SQLiteDatabase db = null;
        File file = new File(context.getFilesDir(), "CnA.db");
        System.out.println("PATH : " + file.toString());
        try{
            db = SQLiteDatabase.openOrCreateDatabase(file,null);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        if( db == null){
            System.out.println("DB createion failed. " + file.getAbsolutePath() );
        }
        return db;
    }

    // Exam 테이블 생성 메소드
    // Methods to create the Exam table
    private void init_Tables(){
        if(sqliteDb != null){
            String sqlCreateTb = "CREATE TABLE IF NOT EXISTS ClassData (" +
                    "Id "          + "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "ClassId "     + "INTEGER DEFAULT 1," +
                    "Term "        + "INTEGER DEFAULT 0," +
                    "Title "       + "TEXT DEFAULT ''," +
                    "Tag "         + "INTEGER DEFAULT 0);";
            System.out.println(sqlCreateTb);
            sqliteDb.execSQL(sqlCreateTb);
        }
        /*
            ClassID = 학년
            Term = 학기
            Title = 단원명
            Tag = 수학이 아닌 다른 과목으로 확장 가능하게 하는 과목 코드
         */
    }

    public ArrayList<String> get_title(int classId){
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = null;
        if(sqliteDb != null){
            String sqlQueryTb1 = "SELECT Title FROM ClassData WHERE ClassId = "+  classId +";";
            System.out.println(sqlQueryTb1);
            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            for(int i=0;i<cursor.getCount();i++){
                cursor.moveToNext();
                list.add(cursor.getString(0));
            }
        }
        return list;
    }

    public void first(){
        ArrayList<ClassData> s_list = new ArrayList<>();
        //초등학교 1학년
        s_list.add(new ClassData(11,0,"23까지의 수",1));
        s_list.add(new ClassData(11,0,"여러가지모양",1));
        s_list.add(new ClassData(11,0,"덧셈과 뺄셈",1));
        s_list.add(new ClassData(11,0,"비교하기",1));
        s_list.add(new ClassData(11,0,"50까지의 수",1));

        s_list.add(new ClassData(11,1,"100까지의 수",1));
        s_list.add(new ClassData(11,1,"덧셈과 뺄셈",1));
        s_list.add(new ClassData(11,1,"여러가지모양",1));
        s_list.add(new ClassData(11,1,"덧셈과 뺄셈",1));
        s_list.add(new ClassData(11,1,"시계보기와 규칙찾기",1));
        s_list.add(new ClassData(11,1,"덧셈과 뺄셈",1));

        //초등학교 2학년
        s_list.add(new ClassData(12,0,"세 자리 수",1));
        s_list.add(new ClassData(12,0,"여러가지 도형",1));
        s_list.add(new ClassData(12,0,"덧셈과 뺄셈",1));
        s_list.add(new ClassData(12,0,"길이재기",1));
        s_list.add(new ClassData(12,0,"분류하기",1));
        s_list.add(new ClassData(12,0,"곱셈",1));

        s_list.add(new ClassData(12,1,"네 자리 수",1));
        s_list.add(new ClassData(12,1,"곱셈구구",1));
        s_list.add(new ClassData(12,1,"길이재기",1));
        s_list.add(new ClassData(12,1,"시각과 시간",1));
        s_list.add(new ClassData(12,1,"표와 그래프",1));
        s_list.add(new ClassData(12,1,"규칙 찾기",1));

        //초등학교 3학년
        s_list.add(new ClassData(13,0,"덧셈과 뺄셈",1));
        s_list.add(new ClassData(13,0,"평면도형",1));
        s_list.add(new ClassData(13,0,"나눗셈",1));
        s_list.add(new ClassData(13,0,"곱셈",1));
        s_list.add(new ClassData(13,0,"길이와 시간",1));
        s_list.add(new ClassData(13,0,"분수와 소수",1));

        s_list.add(new ClassData(13,1,"곱셈",1));
        s_list.add(new ClassData(13,1,"나눗셈",1));
        s_list.add(new ClassData(13,1,"원",1));
        s_list.add(new ClassData(13,1,"분수",1));
        s_list.add(new ClassData(13,1,"들이와 물게",1));
        s_list.add(new ClassData(13,1,"자료의 정리",1));

        //초등학교 4학년
        s_list.add(new ClassData(14,0,"큰 수",1));
        s_list.add(new ClassData(14,0,"각도",1));
        s_list.add(new ClassData(14,0,"곱셈과 나눗셈",1));
        s_list.add(new ClassData(14,0,"평면도형의 이동",1));
        s_list.add(new ClassData(14,0,"막대그래프",1));
        s_list.add(new ClassData(14,0,"규칙찾기",1));

        s_list.add(new ClassData(14,1,"분수의 덧셈과 뺄셈",1));
        s_list.add(new ClassData(14,1,"삼각형",1));
        s_list.add(new ClassData(14,1,"소수의 덧셈과 뺄셈",1));
        s_list.add(new ClassData(14,1,"사격형",1));
        s_list.add(new ClassData(14,1,"꺽은선 그래프",1));
        s_list.add(new ClassData(14,1,"다각형",1));

        //초등학교 5학년
        s_list.add(new ClassData(15,0,"자연수의 혼합계산",1));
        s_list.add(new ClassData(15,0,"약수와 배수",1));
        s_list.add(new ClassData(15,0,"규칙과 대응",1));
        s_list.add(new ClassData(15,0,"약분과 통분",1));
        s_list.add(new ClassData(15,0,"분수의 덧셈과 뺄셈",1));
        s_list.add(new ClassData(15,0,"다각형의 둘레와 넓이",1));

        s_list.add(new ClassData(15,1,"소수의 곱셈",1));
        s_list.add(new ClassData(15,1,"합동과 대칭",1));
        s_list.add(new ClassData(15,1,"분수의 나눗셈",1));
        s_list.add(new ClassData(15,1,"소수의 나눗셈",1));
        s_list.add(new ClassData(15,1,"여러가지 단위",1));
        s_list.add(new ClassData(15,1,"자료의 표현",1));

        //초등학교 6학년
        s_list.add(new ClassData(16,0,"분수의 나눗셈",1));
        s_list.add(new ClassData(16,0,"각기둥과 각뿔",1));
        s_list.add(new ClassData(16,0,"소수의 나눗셈",1));
        s_list.add(new ClassData(16,0,"비와 비율",1));
        s_list.add(new ClassData(16,0,"여러가지 그래프",1));
        s_list.add(new ClassData(16,0,"직육면체의 부피와 겉넓이",1));

        s_list.add(new ClassData(16,1,"쌓기나무",1));
        s_list.add(new ClassData(16,1,"비례식과 비례배분",1));
        s_list.add(new ClassData(16,1,"원기둥, 원형, 구",1));
        s_list.add(new ClassData(16,1,"비율그래프",1));
        s_list.add(new ClassData(16,1,"정비례와 반비례",1));
        s_list.add(new ClassData(16,1,"여러가지 문제",1));

        //중학교 1학년
        s_list.add(new ClassData(21,0,"자연수의 성질",1));
        s_list.add(new ClassData(21,0,"정수와 유리수",1));
        s_list.add(new ClassData(21,0,"문자와 식",1));
        s_list.add(new ClassData(21,0,"함수",1));

        s_list.add(new ClassData(21,1,"기본 도형",1));
        s_list.add(new ClassData(21,1,"평면 도형",1));
        s_list.add(new ClassData(21,1,"입체 도형",1));
        s_list.add(new ClassData(21,1,"통계",1));

        //중학교 2학년
        s_list.add(new ClassData(22,0,"유리수와 소수",1));
        s_list.add(new ClassData(22,0,"식의 계산",1));
        s_list.add(new ClassData(22,0,"연립방정식",1));
        s_list.add(new ClassData(22,0,"부등식",1));
        s_list.add(new ClassData(22,0,"일차함수",1));

        s_list.add(new ClassData(22,1,"도형의 성질",1));
        s_list.add(new ClassData(22,1,"도형의 닮음",1));
        s_list.add(new ClassData(22,1,"피타고라스의 정리",1));
        s_list.add(new ClassData(22,1,"확률",1));

        //중학교 3학년
        s_list.add(new ClassData(23,0,"실수와 그 계산",1));
        s_list.add(new ClassData(23,0,"식의 계산",1));
        s_list.add(new ClassData(23,0,"이차방정식",1));
        s_list.add(new ClassData(23,0,"이차함수",1));

        s_list.add(new ClassData(23,1,"삼각비",1));
        s_list.add(new ClassData(23,1,"원의 성질",1));
        s_list.add(new ClassData(23,1,"통계",1));

        //공통 수학
        s_list.add(new ClassData(31,3,"다항식의 연산",1));
        s_list.add(new ClassData(31,3,"항등식과 나머지정리",1));
        s_list.add(new ClassData(31,3,"인수분해",1));
        s_list.add(new ClassData(31,3,"복소수와 이차방정식",1));
        s_list.add(new ClassData(31,3,"이차방정식과 이차함수",1));
        s_list.add(new ClassData(31,3,"여러가지 방정식과 부등식",1));
        s_list.add(new ClassData(31,3,"평면좌표",1));
        s_list.add(new ClassData(31,3,"직선의 방정식",1));
        s_list.add(new ClassData(31,3,"원의 방정식",1));
        s_list.add(new ClassData(31,3,"도형의 이동",1));

        s_list.add(new ClassData(31,3,"집합",1));
        s_list.add(new ClassData(31,3,"명제",1));
        s_list.add(new ClassData(31,3,"함수",1));
        s_list.add(new ClassData(31,3,"유리함수와 무리함수",1));
        s_list.add(new ClassData(31,3,"경우의 수",1));
        s_list.add(new ClassData(31,3,"순열과 조합",1));

        //수학 I
        s_list.add(new ClassData(32,3,"지수와 로그",1));
        s_list.add(new ClassData(32,3,"지수함수와 로그함수",1));
        s_list.add(new ClassData(32,3,"삼각함수",1));
        s_list.add(new ClassData(32,3,"사인법칙과 코사인법칙",1));
        s_list.add(new ClassData(32,3,"등차수열과 등비수열",1));
        s_list.add(new ClassData(32,3,"수열의 합",1));
        s_list.add(new ClassData(32,3,"수학적 귀납법",1));

        //수학 II
        s_list.add(new ClassData(33,3,"함수의 극한",1));
        s_list.add(new ClassData(33,3,"함수의 연속",1));
        s_list.add(new ClassData(33,3,"미분계수와 도함수",1));
        s_list.add(new ClassData(33,3,"도함수의 활용",1));
        s_list.add(new ClassData(33,3,"부정적분과 정적분",1));
        s_list.add(new ClassData(33,3,"정적분의 활용",1));

        //미적분
        s_list.add(new ClassData(34,3,"수열의 극한",1));
        s_list.add(new ClassData(34,3,"급수",1));
        s_list.add(new ClassData(34,3,"여러가지 함수의 미분",1));
        s_list.add(new ClassData(34,3,"여러가지 미분법",1));
        s_list.add(new ClassData(34,3,"도함수의 활용",1));
        s_list.add(new ClassData(34,3,"여러가지 적분법",1));
        s_list.add(new ClassData(34,3,"정적분의 활용",1));

        //확률과 통계
        s_list.add(new ClassData(35,3,"순열과 조합",1));
        s_list.add(new ClassData(35,3,"이항정리",1));
        s_list.add(new ClassData(35,3,"확률의 뜻과 활용",1));
        s_list.add(new ClassData(35,3,"조건부확률",1));
        s_list.add(new ClassData(35,3,"확률분포",1));
        s_list.add(new ClassData(35,3,"통계적 추정",1));

        //기하
        s_list.add(new ClassData(36,3,"이차곡선",1));
        s_list.add(new ClassData(36,3,"벡터의 연산",1));
        s_list.add(new ClassData(36,3,"평면벡터의 성분과 내적",1));
        s_list.add(new ClassData(36,3,"직선과 평면",1));
        s_list.add(new ClassData(36,3,"정사영",1));
        s_list.add(new ClassData(36,3,"공간좌표",1));

        //수학과제탐구
        s_list.add(new ClassData(37,3,"수학과제 탐구의 의미와 필요성",1));
        s_list.add(new ClassData(37,3,"과제 탐구 방법과 절차",1));
        s_list.add(new ClassData(37,3,"연구 윤리",1));
        s_list.add(new ClassData(37,3,"탐구 주제 선정",1));
        s_list.add(new ClassData(37,3,"탐구 계획 수립",1));
        s_list.add(new ClassData(37,3,"탐구 수행",1));
        s_list.add(new ClassData(37,3,"탐구 결과 정리 및 발표",1));
        s_list.add(new ClassData(37,3,"반성 및 평가",1));

        //경제 수학
        s_list.add(new ClassData(38,3,"경제지표",1));
        s_list.add(new ClassData(38,3,"환율",1));
        s_list.add(new ClassData(38,3,"세금",1));
        s_list.add(new ClassData(38,3,"이자와 원리합계",1));
        s_list.add(new ClassData(38,3,"연속복리",1));
        s_list.add(new ClassData(38,3,"연금",1));
        s_list.add(new ClassData(38,3,"함수와 경제현상",1));
        s_list.add(new ClassData(38,3,"함수의 활용",1));
        s_list.add(new ClassData(38,3,"미분",1));
        s_list.add(new ClassData(38,3,"미분과 경제문제",1));

        //실용수학
        s_list.add(new ClassData(39,3,"식과 규칙",1));
        s_list.add(new ClassData(39,3,"도형과 규칙",1));
        s_list.add(new ClassData(39,3,"도형의 관찰",1));
        s_list.add(new ClassData(39,3,"도형의 표현",1));
        s_list.add(new ClassData(39,3,"자료의 정리",1));
        s_list.add(new ClassData(39,3,"자료의 해석",1));

        for(int i=0;i<s_list.size();i++){
            SQLiteStatement p = sqliteDb.compileStatement("INSERT INTO ClassData (ClassId, Term, Title, Tag) VALUES (?,?,?,?);");
            p.bindLong(1, s_list.get(i).ClassId);
            p.bindLong(2, s_list.get(i).Term);
            p.bindString(3, s_list.get(i).Title);
            p.bindLong(4, s_list.get(i).Tag);
            System.out.println(p);
            p.execute();
        }
    }
}
