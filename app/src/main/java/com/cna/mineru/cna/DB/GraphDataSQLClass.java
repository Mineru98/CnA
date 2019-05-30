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
                    "isSubsection "+ "BOOLEAN DEFAULT 0," +
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

    public ArrayList<ClassData> set_title(int classId, int isSubsection){
        ArrayList<ClassData> list = new ArrayList<>();
        Cursor cursor = null;
        if(sqliteDb != null){
            String sqlQueryTb1 = "SELECT Tag, Title FROM ClassData WHERE ClassId = "+  classId +" AND isSubsection = " + isSubsection + ";";
            System.out.println(sqlQueryTb1);
            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            for(int i=0;i<cursor.getCount();i++){
                cursor.moveToNext();
                list.add(new ClassData(cursor.getInt(0),cursor.getString(1)));
            }
        }
        return list;
    }

    public int get_size(int classId, int isSubsection) {
        int result=0;
        Cursor cursor = null;
        if(sqliteDb != null){
            String sqlQueryTb1 = "SELECT Title FROM ClassData WHERE ClassId = "+  classId +" AND isSubsection = " + isSubsection + ";";
            System.out.println(sqlQueryTb1);
            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            for(int i=0;i<cursor.getCount();i++){
                cursor.moveToNext();
                result++;
            }
        }
        return result;
    }

    public void first(){
        ArrayList<ClassData> s_list = new ArrayList<>();
        //초등학교 1학년
        s_list.add(new ClassData(11,0,"1. 9까지의 수",1,0));
        s_list.add(new ClassData(11,0,"1-1. 몇일까요?(1) - 1부터 5까지의 수",1,1));
        s_list.add(new ClassData(11,0,"1-2. 몇일까요?(2) - 6부터 9까지의 수",1,1));
        s_list.add(new ClassData(11,0,"1-3. 몇번째일까요 - 첫째~아홉째",1,1));
        s_list.add(new ClassData(11,0,"1-4. 수의 순서를 알아볼까요 - 수의 순서 쓰기, 수의 순서대로 연결하기",1,1));
        s_list.add(new ClassData(11,0,"1-5. 1 큰 수와 1 작은 수는 무엇일까요",1,1));
        s_list.add(new ClassData(11,0,"1-6. 어떤 수가 더 클까요",1,1));
        s_list.add(new ClassData(11,0,"1-7. 수 놀이를 해요",1,1));
        s_list.add(new ClassData(11,0,"1-8. 숫자 숨바꼭질을 해 볼까요",1,1));

        s_list.add(new ClassData(11,0,"2. 여러가지모양",2,0));
        s_list.add(new ClassData(11,0,"2-1. 여러 가지 모양을 찾아볼가요 - 입체모양",2,1));
        s_list.add(new ClassData(11,0,"2-2. 여러 가지 모양을 찾아볼가요",2,1));
        s_list.add(new ClassData(11,0,"2-3. 여러 가지 모양으로 만들어 볼까요",2,1));
        s_list.add(new ClassData(11,0,"2-4. 모양 찾기 놀이를 해요",2,1));
        s_list.add(new ClassData(11,0,"2-5. 마을을 만들어 볼까요",2,1));

        s_list.add(new ClassData(11,0,"3. 덧셈과 뺄셈",3,0));
        s_list.add(new ClassData(11,0,"3-1. 모으기와 가르기를 해볼까요(1) - 5까지의 수",3,1));
        s_list.add(new ClassData(11,0,"3-2. 모으기와 가르기를 해볼까요(1) - 9까지의 수",3,1));
        s_list.add(new ClassData(11,0,"3-3. 더하기를 만들어 볼까요",3,1));
        s_list.add(new ClassData(11,0,"3-4. 더하기는 어떻게 나타낼까요",3,1));
        s_list.add(new ClassData(11,0,"3-5. 더하면 얼마일까요(1)",3,1));
        s_list.add(new ClassData(11,0,"3-6. 더하면 얼마일까요(2)",3,1));
        s_list.add(new ClassData(11,0,"3-7. 덧셈 놀이를 해요",3,1));
        s_list.add(new ClassData(11,0,"3-8. 빼기는 어떻게 나타낼까요",3,1));
        s_list.add(new ClassData(11,0,"3-9. 빼면 얼마일까요(1)",3,1));
        s_list.add(new ClassData(11,0,"3-10. 빼면 얼마일까요(2)",3,1));
        s_list.add(new ClassData(11,0,"3-11. 0을 더하거나 빼면 어떻게 될까요",3,1));
        s_list.add(new ClassData(11,0,"3-12. 덧셈과 뺄셈을 해 볼까요",3,1));
        s_list.add(new ClassData(11,0,"3-13. 덧셈식과 뺄셈식을 만들어요",3,1));

        s_list.add(new ClassData(11,0,"4. 비교하기",4,0));
        s_list.add(new ClassData(11,0,"4-1. 어느 것이 더 길까요",4,1));
        s_list.add(new ClassData(11,0,"4-2. 비교하기 놀이를 해요 - 키 비교",4,1));
        s_list.add(new ClassData(11,0,"4-3. 어느 것이 더 무거울까요",4,1));
        s_list.add(new ClassData(11,0,"4-4. 어느 것이 더 넓을까요",4,1));
        s_list.add(new ClassData(11,0,"4-5. 어느 것이 더 많이 담을 수 있을까요",4,1));
        s_list.add(new ClassData(11,0,"4-6. 어떻게 비교할까요 - 높이 비교(쌓기)",4,1));

        s_list.add(new ClassData(11,0,"5. 50까지의 수",5,0));
        s_list.add(new ClassData(11,0,"5-1. 9 다음 수는 무엇일까요",5,1));
        s_list.add(new ClassData(11,0,"5-2. 십 몇을 알아볼까요",5,1));
        s_list.add(new ClassData(11,0,"5-3. 모으기와 가르기를 해 볼까요 - 19까지의 수",5,1));
        s_list.add(new ClassData(11,0,"5-4. 10개씩 묶어 세어 볼까요",5,1));
        s_list.add(new ClassData(11,0,"5-5. 50까지의 수를 세어 볼까요",5,1));
        s_list.add(new ClassData(11,0,"5-6. 수 놀이를 해요",5,1));
        s_list.add(new ClassData(11,0,"5-7. 수의 순서를 알아볼까요",5,1));
        s_list.add(new ClassData(11,0,"5-8. 어떤 수가 더 클까요",5,1));
        s_list.add(new ClassData(11,0,"5-9. 수를 세어 보아요",5,1));

        s_list.add(new ClassData(11,1,"1. 100까지의 수",1,0));
        s_list.add(new ClassData(11,1,"2. 덧셈과 뺄셈",2,0));
        s_list.add(new ClassData(11,1,"3. 여러가지모양",3,0));
        s_list.add(new ClassData(11,1,"4. 덧셈과 뺄셈",4,0));
        s_list.add(new ClassData(11,1,"5. 시계보기와 규칙찾기",5,0));
        s_list.add(new ClassData(11,1,"6. 덧셈과 뺄셈",6,0));

        //초등학교 2학년
        s_list.add(new ClassData(12,0,"1. 세 자리 수",1,0));
        s_list.add(new ClassData(12,0,"2. 여러가지 도형",1,0));
        s_list.add(new ClassData(12,0,"3. 덧셈과 뺄셈",1,0));
        s_list.add(new ClassData(12,0,"4. 길이재기",1,0));
        s_list.add(new ClassData(12,0,"5. 분류하기",1,0));
        s_list.add(new ClassData(12,0,"6. 곱셈",1,0));

        s_list.add(new ClassData(12,1,"1. 네 자리 수",1,0));
        s_list.add(new ClassData(12,1,"2. 곱셈구구",1,0));
        s_list.add(new ClassData(12,1,"3. 길이재기",1,0));
        s_list.add(new ClassData(12,1,"4. 시각과 시간",1,0));
        s_list.add(new ClassData(12,1,"5. 표와 그래프",1,0));
        s_list.add(new ClassData(12,1,"6. 규칙 찾기",1,0));

        //초등학교 3학년
        s_list.add(new ClassData(13,0,"1. 덧셈과 뺄셈",1,0));
        s_list.add(new ClassData(13,0,"2. 평면도형",1,0));
        s_list.add(new ClassData(13,0,"3. 나눗셈",1,0));
        s_list.add(new ClassData(13,0,"4. 곱셈",1,0));
        s_list.add(new ClassData(13,0,"5. 길이와 시간",1,0));
        s_list.add(new ClassData(13,0,"6. 분수와 소수",1,0));

        s_list.add(new ClassData(13,1,"1. 곱셈",1,0));
        s_list.add(new ClassData(13,1,"2. 나눗셈",1,0));
        s_list.add(new ClassData(13,1,"3. 원",1,0));
        s_list.add(new ClassData(13,1,"4. 분수",1,0));
        s_list.add(new ClassData(13,1,"5. 들이와 물게",1,0));
        s_list.add(new ClassData(13,1,"6. 자료의 정리",1,0));

        //초등학교 4학년
        s_list.add(new ClassData(14,0,"1. 큰 수",1,0));
        s_list.add(new ClassData(14,0,"2. 각도",1,0));
        s_list.add(new ClassData(14,0,"3. 곱셈과 나눗셈",1,0));
        s_list.add(new ClassData(14,0,"4. 평면도형의 이동",1,0));
        s_list.add(new ClassData(14,0,"5. 막대그래프",1,0));
        s_list.add(new ClassData(14,0,"6. 규칙찾기",1,0));

        s_list.add(new ClassData(14,1,"1. 분수의 덧셈과 뺄셈",1,0));
        s_list.add(new ClassData(14,1,"2. 삼각형",1,0));
        s_list.add(new ClassData(14,1,"3. 소수의 덧셈과 뺄셈",1,0));
        s_list.add(new ClassData(14,1,"4. 사격형",1,0));
        s_list.add(new ClassData(14,1,"5. 꺽은선 그래프",1,0));
        s_list.add(new ClassData(14,1,"6. 다각형",1,0));

        //초등학교 5학년
        s_list.add(new ClassData(15,0,"1. 자연수의 혼합계산",1,0));
        s_list.add(new ClassData(15,0,"2. 약수와 배수",1,0));
        s_list.add(new ClassData(15,0,"3. 규칙과 대응",1,0));
        s_list.add(new ClassData(15,0,"4. 약분과 통분",1,0));
        s_list.add(new ClassData(15,0,"5. 분수의 덧셈과 뺄셈",1,0));
        s_list.add(new ClassData(15,0,"6. 다각형의 둘레와 넓이",1,0));

        s_list.add(new ClassData(15,1,"1. 소수의 곱셈",1,0));
        s_list.add(new ClassData(15,1,"2. 합동과 대칭",1,0));
        s_list.add(new ClassData(15,1,"3. 분수의 나눗셈",1,0));
        s_list.add(new ClassData(15,1,"4. 소수의 나눗셈",1,0));
        s_list.add(new ClassData(15,1,"5. 여러가지 단위",1,0));
        s_list.add(new ClassData(15,1,"6. 자료의 표현",1,0));

        //초등학교 6학년
        s_list.add(new ClassData(16,0,"1. 분수의 나눗셈",1,0));
        s_list.add(new ClassData(16,0,"2. 각기둥과 각뿔",1,0));
        s_list.add(new ClassData(16,0,"3. 소수의 나눗셈",1,0));
        s_list.add(new ClassData(16,0,"4. 비와 비율",1,0));
        s_list.add(new ClassData(16,0,"5. 여러가지 그래프",1,0));
        s_list.add(new ClassData(16,0,"6. 직육면체의 부피와 겉넓이",1,0));

        s_list.add(new ClassData(16,1,"1. 쌓기나무",1,0));
        s_list.add(new ClassData(16,1,"2. 비례식과 비례배분",1,0));
        s_list.add(new ClassData(16,1,"3. 원기둥, 원형, 구",1,0));
        s_list.add(new ClassData(16,1,"4. 비율그래프",1,0));
        s_list.add(new ClassData(16,1,"5. 정비례와 반비례",1,0));
        s_list.add(new ClassData(16,1,"6. 여러가지 문제",1,0));

        //중학교 1학년
        s_list.add(new ClassData(21,0,"1. 자연수의 성질",1,0));
        s_list.add(new ClassData(21,0,"2. 정수와 유리수",1,0));
        s_list.add(new ClassData(21,0,"3. 문자와 식",1,0));
        s_list.add(new ClassData(21,0,"4. 함수",1,0));

        s_list.add(new ClassData(21,1,"1. 기본 도형",1,0));
        s_list.add(new ClassData(21,1,"2. 평면 도형",1,0));
        s_list.add(new ClassData(21,1,"3. 입체 도형",1,0));
        s_list.add(new ClassData(21,1,"4. 통계",1,0));

        //중학교 2학년
        s_list.add(new ClassData(22,0,"1. 유리수와 소수",1,0));
        s_list.add(new ClassData(22,0,"2. 식의 계산",1,0));
        s_list.add(new ClassData(22,0,"3. 연립방정식",1,0));
        s_list.add(new ClassData(22,0,"4. 부등식",1,0));
        s_list.add(new ClassData(22,0,"5. 일차함수",1,0));

        s_list.add(new ClassData(22,1,"1. 도형의 성질",1,0));
        s_list.add(new ClassData(22,1,"2. 도형의 닮음",1,0));
        s_list.add(new ClassData(22,1,"3. 피타고라스의 정리",1,0));
        s_list.add(new ClassData(22,1,"4. 확률",1,0));

        //중학교 3학년
        s_list.add(new ClassData(23,0,"1. 실수와 그 계산",1,0));
        s_list.add(new ClassData(23,0,"2. 식의 계산",1,0));
        s_list.add(new ClassData(23,0,"3. 이차방정식",1,0));
        s_list.add(new ClassData(23,0,"4. 이차함수",1,0));

        s_list.add(new ClassData(23,1,"1. 삼각비",1,0));
        s_list.add(new ClassData(23,1,"2. 원의 성질",1,0));
        s_list.add(new ClassData(23,1,"3. 통계",1,0));

        //공통 수학
        s_list.add(new ClassData(31,3,"다항식의 연산",1,0));
        s_list.add(new ClassData(31,3,"항등식과 나머지정리",1,0));
        s_list.add(new ClassData(31,3,"인수분해",1,0));
        s_list.add(new ClassData(31,3,"복소수와 이차방정식",1,0));
        s_list.add(new ClassData(31,3,"이차방정식과 이차함수",1,0));
        s_list.add(new ClassData(31,3,"여러가지 방정식과 부등식",1,0));
        s_list.add(new ClassData(31,3,"평면좌표",1,0));
        s_list.add(new ClassData(31,3,"직선의 방정식",1,0));
        s_list.add(new ClassData(31,3,"원의 방정식",1,0));
        s_list.add(new ClassData(31,3,"도형의 이동",1,0));

        s_list.add(new ClassData(31,3,"집합",1,0));
        s_list.add(new ClassData(31,3,"명제",1,0));
        s_list.add(new ClassData(31,3,"함수",1,0));
        s_list.add(new ClassData(31,3,"유리함수와 무리함수",1,0));
        s_list.add(new ClassData(31,3,"경우의 수",1,0));
        s_list.add(new ClassData(31,3,"순열과 조합",1,0));

        //수학 I
        s_list.add(new ClassData(32,3,"지수와 로그",1,0));
        s_list.add(new ClassData(32,3,"지수함수와 로그함수",1,0));
        s_list.add(new ClassData(32,3,"삼각함수",1,0));
        s_list.add(new ClassData(32,3,"사인법칙과 코사인법칙",1,0));
        s_list.add(new ClassData(32,3,"등차수열과 등비수열",1,0));
        s_list.add(new ClassData(32,3,"수열의 합",1,0));
        s_list.add(new ClassData(32,3,"수학적 귀납법",1,0));

        //수학 II
        s_list.add(new ClassData(33,3,"함수의 극한",1,0));
        s_list.add(new ClassData(33,3,"함수의 연속",1,0));
        s_list.add(new ClassData(33,3,"미분계수와 도함수",1,0));
        s_list.add(new ClassData(33,3,"도함수의 활용",1,0));
        s_list.add(new ClassData(33,3,"부정적분과 정적분",1,0));
        s_list.add(new ClassData(33,3,"정적분의 활용",1,0));

        //미적분
        s_list.add(new ClassData(34,3,"수열의 극한",1,0));
        s_list.add(new ClassData(34,3,"급수",1,0));
        s_list.add(new ClassData(34,3,"여러가지 함수의 미분",1,0));
        s_list.add(new ClassData(34,3,"여러가지 미분법",1,0));
        s_list.add(new ClassData(34,3,"도함수의 활용",1,0));
        s_list.add(new ClassData(34,3,"여러가지 적분법",1,0));
        s_list.add(new ClassData(34,3,"정적분의 활용",1,0));

        //확률과 통계
        s_list.add(new ClassData(35,3,"순열과 조합",1,0));
        s_list.add(new ClassData(35,3,"이항정리",1,0));
        s_list.add(new ClassData(35,3,"확률의 뜻과 활용",1,0));
        s_list.add(new ClassData(35,3,"조건부확률",1,0));
        s_list.add(new ClassData(35,3,"확률분포",1,0));
        s_list.add(new ClassData(35,3,"통계적 추정",1,0));

        //기하
        s_list.add(new ClassData(36,3,"이차곡선",1,0));
        s_list.add(new ClassData(36,3,"벡터의 연산",1,0));
        s_list.add(new ClassData(36,3,"평면벡터의 성분과 내적",1,0));
        s_list.add(new ClassData(36,3,"직선과 평면",1,0));
        s_list.add(new ClassData(36,3,"정사영",1,0));
        s_list.add(new ClassData(36,3,"공간좌표",1,0));

        //수학과제탐구
        s_list.add(new ClassData(37,3,"수학과제 탐구의 의미와 필요성",1,0));
        s_list.add(new ClassData(37,3,"과제 탐구 방법과 절차",1,0));
        s_list.add(new ClassData(37,3,"연구 윤리",1,0));
        s_list.add(new ClassData(37,3,"탐구 주제 선정",1,0));
        s_list.add(new ClassData(37,3,"탐구 계획 수립",1,0));
        s_list.add(new ClassData(37,3,"탐구 수행",1,0));
        s_list.add(new ClassData(37,3,"탐구 결과 정리 및 발표",1,0));
        s_list.add(new ClassData(37,3,"반성 및 평가",1,0));

        //경제 수학
        s_list.add(new ClassData(38,3,"경제지표",1,0));
        s_list.add(new ClassData(38,3,"환율",1,0));
        s_list.add(new ClassData(38,3,"세금",1,0));
        s_list.add(new ClassData(38,3,"이자와 원리합계",1,0));
        s_list.add(new ClassData(38,3,"연속복리",1,0));
        s_list.add(new ClassData(38,3,"연금",1,0));
        s_list.add(new ClassData(38,3,"함수와 경제현상",1,0));
        s_list.add(new ClassData(38,3,"함수의 활용",1,0));
        s_list.add(new ClassData(38,3,"미분",1,0));
        s_list.add(new ClassData(38,3,"미분과 경제문제",1,0));

        //실용수학
        s_list.add(new ClassData(39,3,"식과 규칙",1,0));
        s_list.add(new ClassData(39,3,"도형과 규칙",1,0));
        s_list.add(new ClassData(39,3,"도형의 관찰",1,0));
        s_list.add(new ClassData(39,3,"도형의 표현",1,0));
        s_list.add(new ClassData(39,3,"자료의 정리",1,0));
        s_list.add(new ClassData(39,3,"자료의 해석",1,0));

        for(int i=0;i<s_list.size();i++){
            SQLiteStatement p = sqliteDb.compileStatement("INSERT INTO ClassData (ClassId, Term, Title, Tag, isSubsection) VALUES (?,?,?,?,?);");
            p.bindLong(1, s_list.get(i).ClassId);
            p.bindLong(2, s_list.get(i).Term);
            p.bindString(3, s_list.get(i).Title);
            p.bindLong(4, s_list.get(i).Tag);
            p.bindLong(5, s_list.get(i).isSubsection);
            System.out.println(p);
            p.execute();
        }
    }
}
