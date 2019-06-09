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
                    "ClassId "     + "INTEGER DEFAULT 10," +
                    "Term "        + "INTEGER DEFAULT 0," +
                    "Title "       + "TEXT DEFAULT ''," +
                    "Tag "         + "INTEGER DEFAULT 0," +
                    "SubTag "      + "INTEGER DEFAULT 0);";

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

    public ArrayList<String> get_title(int classId, int month){
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = null;
        if(sqliteDb != null){
            String sqlQueryTb1;
            if(month>7)
                sqlQueryTb1 = "SELECT Title FROM ClassData WHERE ClassId = "+  classId +" AND Term = 1;";
            else
                sqlQueryTb1 = "SELECT Title FROM ClassData WHERE ClassId = "+  classId +" AND Term = 0;";
            System.out.println(sqlQueryTb1);
            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            for(int i=0;i<cursor.getCount();i++){
                cursor.moveToNext();
                list.add(cursor.getString(0));
            }
        }
        return list;
    }

    public String getData(int Tag, int Subtag, int ClassId){
        String result = "";
        Cursor cursor = null;
        if(sqliteDb != null){
            String sqlQueryTb1 = "SELECT Title FROM ClassData WHERE Tag = "+  Tag + " AND Subtag = " + Subtag + " AND ClassId = " + ClassId + ";";
            System.out.println(sqlQueryTb1);
            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            cursor.moveToNext();
            result = cursor.getString(0);
        }
        return result;
    }

    public ArrayList<ClassData> set_title(int classId, int SubTag, int month){
        ArrayList<ClassData> list = new ArrayList<>();
        Cursor cursor = null;
        if(sqliteDb != null){
            String sqlQueryTb1;
            if(SubTag==0){
                if(month>7)
                    sqlQueryTb1 = "SELECT Tag, Title FROM ClassData WHERE ClassId = "+  classId +" AND SubTag = " + SubTag + " AND Term = 1;";
                else
                    sqlQueryTb1 = "SELECT Tag, Title FROM ClassData WHERE ClassId = "+  classId +" AND SubTag = " + SubTag + " AND Term = 0;";
            }else{
                if(month>7)
                    sqlQueryTb1 = "SELECT Tag, Title FROM ClassData WHERE ClassId = "+  classId +" AND SubTag != 0 AND Term = 1;";
                else
                    sqlQueryTb1 = "SELECT Tag, Title FROM ClassData WHERE ClassId = "+  classId +" AND SubTag != 0 AND Term = 0;";
            }

            System.out.println(sqlQueryTb1);
            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            for(int i=0;i<cursor.getCount();i++){
                cursor.moveToNext();
                list.add(new ClassData(cursor.getInt(0),cursor.getString(1)));
            }
        }
        return list;
    }

    public int getTag(String Title) {
        Cursor cursor = null;
        int result = 0;
        if (sqliteDb != null) {
            String sqlQueryTb1 = "SELECT Tag FROM ClassData WHERE Title = '" + Title + "';";
            System.out.println(sqlQueryTb1);
            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            cursor.moveToNext();
            result = cursor.getInt(0);
        }
        return result;
    }

    public int getSubTag(String Title) {
        Cursor cursor = null;
        int result = 0;
        if (sqliteDb != null) {
            String sqlQueryTb1 = "SELECT SubTag FROM ClassData WHERE Title = '" + Title + "';";
            System.out.println(sqlQueryTb1);
            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            cursor.moveToNext();
            result = cursor.getInt(0);
        }
        return result;
    }

    public int get_size(int classId, int SubTag, int month) {
        int result=0;
        Cursor cursor = null;
        if(sqliteDb != null){
            String sqlQueryTb1;
            if(SubTag==0){
                if(month>7)
                    sqlQueryTb1 = "SELECT Title FROM ClassData WHERE ClassId = "+  classId +" AND SubTag = " + SubTag + " AND Term = 1;";
                else
                    sqlQueryTb1 = "SELECT Title FROM ClassData WHERE ClassId = "+  classId +" AND SubTag = " + SubTag + " AND Term = 0;";
            }else{
                if(month>7)
                    sqlQueryTb1 = "SELECT Title FROM ClassData WHERE ClassId = "+  classId +" AND SubTag != 0 AND Term = 1;";
                else
                    sqlQueryTb1 = "SELECT Title FROM ClassData WHERE ClassId = "+  classId +" AND SubTag != 0 AND Term = 0;";
            }

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
        //초등학교 1학년 1학기
        s_list.add(new ClassData(11,0,"1. 9까지의 수",1,0));
        s_list.add(new ClassData(11,0,"1-1. 몇일까요?(1) - 1부터 5까지의 수",1,1));
        s_list.add(new ClassData(11,0,"1-2. 몇일까요?(2) - 6부터 9까지의 수",1,2));
        s_list.add(new ClassData(11,0,"1-3. 몇번째일까요 - 첫째~아홉째",1,3));
        s_list.add(new ClassData(11,0,"1-4. 수의 순서를 알아볼까요 - 수의 순서 쓰기, 수의 순서대로 연결하기",1,4));
        s_list.add(new ClassData(11,0,"1-5. 1 큰 수와 1 작은 수는 무엇일까요",1,5));
        s_list.add(new ClassData(11,0,"1-6. 어떤 수가 더 클까요",1,6));
        s_list.add(new ClassData(11,0,"1-7. 수 놀이를 해요",1,7));
        s_list.add(new ClassData(11,0,"1-8. 숫자 숨바꼭질을 해 볼까요",1,8));

        s_list.add(new ClassData(11,0,"2. 여러가지모양",2,0));
        s_list.add(new ClassData(11,0,"2-1. 여러 가지 모양을 찾아볼가요 - 입체모양",2,1));
        s_list.add(new ClassData(11,0,"2-2. 여러 가지 모양을 찾아볼가요",2,2));
        s_list.add(new ClassData(11,0,"2-3. 여러 가지 모양으로 만들어 볼까요",2,3));
        s_list.add(new ClassData(11,0,"2-4. 모양 찾기 놀이를 해요",2,4));
        s_list.add(new ClassData(11,0,"2-5. 마을을 만들어 볼까요",2,5));

        s_list.add(new ClassData(11,0,"3. 덧셈과 뺄셈",3,0));
        s_list.add(new ClassData(11,0,"3-1. 모으기와 가르기를 해볼까요(1) - 5까지의 수",3,1));
        s_list.add(new ClassData(11,0,"3-2. 모으기와 가르기를 해볼까요(1) - 9까지의 수",3,2));
        s_list.add(new ClassData(11,0,"3-3. 더하기를 만들어 볼까요",3,3));
        s_list.add(new ClassData(11,0,"3-4. 더하기는 어떻게 나타낼까요",3,4));
        s_list.add(new ClassData(11,0,"3-5. 더하면 얼마일까요(1)",3,5));
        s_list.add(new ClassData(11,0,"3-6. 더하면 얼마일까요(2)",3,6));
        s_list.add(new ClassData(11,0,"3-7. 덧셈 놀이를 해요",3,7));
        s_list.add(new ClassData(11,0,"3-8. 빼기는 어떻게 나타낼까요",3,8));
        s_list.add(new ClassData(11,0,"3-9. 빼면 얼마일까요(1)",3,9));
        s_list.add(new ClassData(11,0,"3-10. 빼면 얼마일까요(2)",3,10));
        s_list.add(new ClassData(11,0,"3-11. 0을 더하거나 빼면 어떻게 될까요",3,11));
        s_list.add(new ClassData(11,0,"3-12. 덧셈과 뺄셈을 해 볼까요",3,12));
        s_list.add(new ClassData(11,0,"3-13. 덧셈식과 뺄셈식을 만들어요",3,13));

        s_list.add(new ClassData(11,0,"4. 비교하기",4,0));
        s_list.add(new ClassData(11,0,"4-1. 어느 것이 더 길까요",4,1));
        s_list.add(new ClassData(11,0,"4-2. 비교하기 놀이를 해요 - 키 비교",4,2));
        s_list.add(new ClassData(11,0,"4-3. 어느 것이 더 무거울까요",4,3));
        s_list.add(new ClassData(11,0,"4-4. 어느 것이 더 넓을까요",4,4));
        s_list.add(new ClassData(11,0,"4-5. 어느 것이 더 많이 담을 수 있을까요",4,5));
        s_list.add(new ClassData(11,0,"4-6. 어떻게 비교할까요 - 높이 비교(쌓기)",4,6));

        s_list.add(new ClassData(11,0,"5. 50까지의 수",5,0));
        s_list.add(new ClassData(11,0,"5-1. 9 다음 수는 무엇일까요",5,1));
        s_list.add(new ClassData(11,0,"5-2. 십 몇을 알아볼까요",5,5));
        s_list.add(new ClassData(11,0,"5-3. 모으기와 가르기를 해 볼까요 - 19까지의 수",5,6));
        s_list.add(new ClassData(11,0,"5-4. 10개씩 묶어 세어 볼까요",5,4));
        s_list.add(new ClassData(11,0,"5-5. 50까지의 수를 세어 볼까요",5,5));
        s_list.add(new ClassData(11,0,"5-6. 수 놀이를 해요",5,6));
        s_list.add(new ClassData(11,0,"5-7. 수의 순서를 알아볼까요",5,7));
        s_list.add(new ClassData(11,0,"5-8. 어떤 수가 더 클까요",5,8));
        s_list.add(new ClassData(11,0,"5-9. 수를 세어 보아요",5,9));

        //초등학교 1학년 2학기
        s_list.add(new ClassData(11,1,"1. 100까지의 수",1,0));
        s_list.add(new ClassData(11,1,"1-1. 100까지의 수",1,1));

        s_list.add(new ClassData(11,1,"2. 덧셈과 뺄셈",2,0));
        s_list.add(new ClassData(11,1,"2-1. 덧셈과 뺄셈",2,1));

        s_list.add(new ClassData(11,1,"3. 여러가지모양",3,0));
        s_list.add(new ClassData(11,1,"3-1. 여러가지모양",3,1));

        s_list.add(new ClassData(11,1,"4. 덧셈과 뺄셈",4,0));
        s_list.add(new ClassData(11,1,"4-1. 덧셈과 뺄셈",4,1));

        s_list.add(new ClassData(11,1,"5. 시계보기와 규칙찾기",5,0));
        s_list.add(new ClassData(11,1,"5-1. 시계보기와 규칙찾기",5,1));

        s_list.add(new ClassData(11,1,"6. 덧셈과 뺄셈",6,0));
        s_list.add(new ClassData(11,1,"6-1. 덧셈과 뺄셈",6,1));

        //초등학교 2학년 1학기
        s_list.add(new ClassData(12,0,"1. 세 자리 수",1,0));
        s_list.add(new ClassData(12,0,"1-1. 세 자리 수",1,1));

        s_list.add(new ClassData(12,0,"2. 여러가지 도형",2,0));
        s_list.add(new ClassData(12,0,"2-1. 여러가지 도형",2,1));

        s_list.add(new ClassData(12,0,"3. 덧셈과 뺄셈",3,0));
        s_list.add(new ClassData(12,0,"3-1. 덧셈과 뺄셈",3,1));

        s_list.add(new ClassData(12,0,"4. 길이재기",4,0));
        s_list.add(new ClassData(12,0,"4-1. 길이재기",4,1));

        s_list.add(new ClassData(12,0,"5. 분류하기",5,0));
        s_list.add(new ClassData(12,0,"5-1. 분류하기",5,1));

        s_list.add(new ClassData(12,0,"6. 곱셈",6,0));
        s_list.add(new ClassData(12,0,"6-1. 곱셈",6,1));

        //초등학교 2학년 2학기
        s_list.add(new ClassData(12,1,"1. 네 자리 수",1,0));
        s_list.add(new ClassData(12,1,"1-1. 네 자리 수",1,1));

        s_list.add(new ClassData(12,1,"2. 곱셈구구",2,0));
        s_list.add(new ClassData(12,1,"2-1. 곱셈구구",2,1));

        s_list.add(new ClassData(12,1,"3. 길이재기",3,0));
        s_list.add(new ClassData(12,1,"3-1. 길이재기",3,1));

        s_list.add(new ClassData(12,1,"4. 시각과 시간",4,0));
        s_list.add(new ClassData(12,1,"4-1. 시각과 시간",4,1));

        s_list.add(new ClassData(12,1,"5. 표와 그래프",5,0));
        s_list.add(new ClassData(12,1,"5-1. 표와 그래프",5,1));

        s_list.add(new ClassData(12,1,"6. 규칙 찾기",6,0));
        s_list.add(new ClassData(12,1,"6-1. 규칙 찾기",6,1));

        //초등학교 3학년 1학기
        s_list.add(new ClassData(13,0,"1. 덧셈과 뺄셈",1,0));
        s_list.add(new ClassData(13,0,"1-1. 덧셈과 뺄셈",1,1));

        s_list.add(new ClassData(13,0,"2. 평면도형",2,0));
        s_list.add(new ClassData(13,0,"2-1. 평면도형",2,1));

        s_list.add(new ClassData(13,0,"3. 나눗셈",3,0));
        s_list.add(new ClassData(13,0,"3-1. 나눗셈",3,1));

        s_list.add(new ClassData(13,0,"4. 곱셈",4,0));
        s_list.add(new ClassData(13,0,"4-1. 곱셈",4,1));

        s_list.add(new ClassData(13,0,"5. 길이와 시간",5,0));
        s_list.add(new ClassData(13,0,"5-1. 길이와 시간",5,1));

        s_list.add(new ClassData(13,0,"6. 분수와 소수",6,0));
        s_list.add(new ClassData(13,0,"6-1. 분수와 소수",6,1));

        //초등학교 3학년 2학기
        s_list.add(new ClassData(13,1,"1. 곱셈",1,0));
        s_list.add(new ClassData(13,1,"1-1. 곱셈",1,1));

        s_list.add(new ClassData(13,1,"2. 나눗셈",2,0));
        s_list.add(new ClassData(13,1,"2-1. 나눗셈",2,1));

        s_list.add(new ClassData(13,1,"3. 원",3,0));
        s_list.add(new ClassData(13,1,"3-1. 원",3,1));

        s_list.add(new ClassData(13,1,"4. 분수",4,0));
        s_list.add(new ClassData(13,1,"4-1. 분수",4,1));

        s_list.add(new ClassData(13,1,"5. 들이와 물게",5,0));
        s_list.add(new ClassData(13,1,"5-1. 들이와 물게",5,1));

        s_list.add(new ClassData(13,1,"6. 자료의 정리",6,0));
        s_list.add(new ClassData(13,1,"6-1. 자료의 정리",6,1));

        //초등학교 4학년 1학기
        s_list.add(new ClassData(14,0,"1. 큰 수",1,0));
        s_list.add(new ClassData(14,0,"1-1. 큰 수",1,1));

        s_list.add(new ClassData(14,0,"2. 각도",2,0));
        s_list.add(new ClassData(14,0,"2-1. 각도",2,1));

        s_list.add(new ClassData(14,0,"3. 곱셈과 나눗셈",3,0));
        s_list.add(new ClassData(14,0,"3-1. 곱셈과 나눗셈",3,1));

        s_list.add(new ClassData(14,0,"4. 평면도형의 이동",4,0));
        s_list.add(new ClassData(14,0,"4-1. 평면도형의 이동",4,1));

        s_list.add(new ClassData(14,0,"5. 막대그래프",5,0));
        s_list.add(new ClassData(14,0,"5-1. 막대그래프",5,1));

        s_list.add(new ClassData(14,0,"6. 규칙찾기",6,0));
        s_list.add(new ClassData(14,0,"6-1. 규칙찾기",6,1));

        //초등학교 4학년 2학기
        s_list.add(new ClassData(14,1,"1. 분수의 덧셈과 뺄셈",1,0));
        s_list.add(new ClassData(14,1,"1-1. 분수의 덧셈과 뺄셈",1,1));

        s_list.add(new ClassData(14,1,"2. 삼각형",2,0));
        s_list.add(new ClassData(14,1,"2-1. 삼각형",2,1));

        s_list.add(new ClassData(14,1,"3. 소수의 덧셈과 뺄셈",3,0));
        s_list.add(new ClassData(14,1,"3-1. 소수의 덧셈과 뺄셈",3,1));

        s_list.add(new ClassData(14,1,"4. 사격형",4,0));
        s_list.add(new ClassData(14,1,"4-1. 사격형",4,1));

        s_list.add(new ClassData(14,1,"5. 꺽은선 그래프",5,0));
        s_list.add(new ClassData(14,1,"5-1. 꺽은선 그래프",5,1));

        s_list.add(new ClassData(14,1,"6. 다각형",6,0));
        s_list.add(new ClassData(14,1,"6-1. 다각형",6,1));

        //초등학교 5학년 1학기
        s_list.add(new ClassData(15,0,"1. 자연수의 혼합계산",1,0));
        s_list.add(new ClassData(15,0,"1-1. 덧셈과 뺄셈이 섞여 있는 식",1,1));
        s_list.add(new ClassData(15,0,"1-2. 곱셈과 나눗셈이 섞여 있는 식",1,2));
        s_list.add(new ClassData(15,0,"1-3. 덧셈, 뺄셈, 곱셈, 나눗셈이 섞여 있는 식",1,3));

        s_list.add(new ClassData(15,0,"2. 약수와 배수",2,0));
        s_list.add(new ClassData(15,0,"2-1. 약수와 배수",2,1));
        s_list.add(new ClassData(15,0,"2-2. 공약수와 최대공약수",2,2));
        s_list.add(new ClassData(15,0,"2-3. 공배수와 최소공배수",2,3));

        s_list.add(new ClassData(15,0,"3. 규칙과 대응",3,0));
        s_list.add(new ClassData(15,0,"3-1. 두 양 사이의 관계 알아보기",3,1));
        s_list.add(new ClassData(15,0,"3-2. 대응 관계를 식으로 나타내기",3,2));

        s_list.add(new ClassData(15,0,"4. 약분과 통분",4,0));
        s_list.add(new ClassData(15,0,"4-1. 크기가 같은 분수 알아보기",4,1));
        s_list.add(new ClassData(15,0,"4-2. 약분과 통분",4,2));
        s_list.add(new ClassData(15,0,"4-3. 분수와 소수의 크기 비교하기",4,3));

        s_list.add(new ClassData(15,0,"5. 분수의 덧셈과 뺄셈",5,0));
        s_list.add(new ClassData(15,0,"5-1. 분수의 덧셈",5,1));
        s_list.add(new ClassData(15,0,"5-2. 분수의 뺄셈",5,2));

        s_list.add(new ClassData(15,0,"6. 다각형의 둘레와 넓이",6,0));
        s_list.add(new ClassData(15,0,"6-1. 정다각형의 둘레, 사각형의 둘레",6,1));
        s_list.add(new ClassData(15,0,"6-2. 넓이의 단위",6,2));
        s_list.add(new ClassData(15,0,"6-3. 평행사변형, 삼각형, 마름모, 사다리꼴의 넓이",6,3));

        //초등학교 5학년 2학기
        s_list.add(new ClassData(15,1,"1. 수의 범위와 어림하기",1,0));
        s_list.add(new ClassData(15,1,"1-1. 이상과 이하",1,1));
        s_list.add(new ClassData(15,1,"1-2. 초과와 미만",1,2));
        s_list.add(new ClassData(15,1,"1-3. 수의 범위를 활용하여 문제 해결하기",1,3));
        s_list.add(new ClassData(15,1,"1-4. 올림",1,4));
        s_list.add(new ClassData(15,1,"1-5. 버림",1,5));
        s_list.add(new ClassData(15,1,"1-6. 반올림",1,6));
        s_list.add(new ClassData(15,1,"1-7. 올림, 버림, 반올림을 활용하여 문제해결하기",1,7));

        s_list.add(new ClassData(15,1,"2. 분수의 곱셈",2,0));
        s_list.add(new ClassData(15,1,"2-1. (분수)X(자연수)",2,1));
        s_list.add(new ClassData(15,1,"2-2. (자연수)X(분수)",2,2));
        s_list.add(new ClassData(15,1,"2-3. (진분수)X(진분수)",2,3));
        s_list.add(new ClassData(15,1,"2-4. 여러가지 분수의 곱셈",2,4));

        s_list.add(new ClassData(15,1,"3. 합동과 대칭",3,0));
        s_list.add(new ClassData(15,1,"3-1. 도형의 합동",3,1));
        s_list.add(new ClassData(15,1,"3-2. 합동인 도형의 성질",3,2));
        s_list.add(new ClassData(15,1,"3-3. 선대칭도형",3,3));
        s_list.add(new ClassData(15,1,"3-4. 선대칭도형의 성질과 그리기",3,4));
        s_list.add(new ClassData(15,1,"3-5. 점대칭도형",3,5));
        s_list.add(new ClassData(15,1,"3-6. 점대칭도형의 성질과 그리기",3,6));

        s_list.add(new ClassData(15,1,"4. 소수의 곱셈",4,0));
        s_list.add(new ClassData(15,1,"4-1. (1보다 작은 소수)X(자연수)",4,1));
        s_list.add(new ClassData(15,1,"4-2. (1보다 큰 소수)X(자연수)",4,2));
        s_list.add(new ClassData(15,1,"4-3. (자연수)X(1보다 작은 소수)",4,3));
        s_list.add(new ClassData(15,1,"4-4. (자연수)X(1보다 큰 소수)",4,4));
        s_list.add(new ClassData(15,1,"4-5. 1보다 작은 소수끼리의 곱셈",4,5));
        s_list.add(new ClassData(15,1,"4-6. 1보다 큰 소수끼리의 곱셈",4,6));
        s_list.add(new ClassData(15,1,"4-7. 곱의 소수점 위치",4,7));

        s_list.add(new ClassData(15,1,"5. 직육면체",5,0));
        s_list.add(new ClassData(15,1,"5-1. 직육면체",5,1));
        s_list.add(new ClassData(15,1,"5-2. 정육면체",5,2));
        s_list.add(new ClassData(15,1,"5-3. 직육면체의 성질",5,3));
        s_list.add(new ClassData(15,1,"5-4. 직육면체의 겨냥도",5,4));
        s_list.add(new ClassData(15,1,"5-5. 정육면체의 전개도",5,5));
        s_list.add(new ClassData(15,1,"5-6. 직육면체의 전개도",5,6));

        s_list.add(new ClassData(15,1,"6. 평균과 가능성",6,0));
        s_list.add(new ClassData(15,1,"6-1. 평균",6,1));
        s_list.add(new ClassData(15,1,"6-2. 평균 구하기",6,2));
        s_list.add(new ClassData(15,1,"6-3. 평균을 이용하여 문제 해결하기",6,3));
        s_list.add(new ClassData(15,1,"6-4. 일이 일어날 가능성을 말로 표현하기",6,4));
        s_list.add(new ClassData(15,1,"6-5. 일이 일어날 가능성을 비교하기",6,5));
        s_list.add(new ClassData(15,1,"6-6. 일이 일어날 가능성을 수로 표현하기",6,6));

        //초등학교 6학년 1학기
        s_list.add(new ClassData(16,0,"1. 분수의 나눗셈",1,0));
        s_list.add(new ClassData(16,0,"1-1. (자연수)/(자연수)",1,1));
        s_list.add(new ClassData(16,0,"1-2. (분수)/(자연수)",1,2));

        s_list.add(new ClassData(16,0,"2. 각기둥과 각뿔",2,0));
        s_list.add(new ClassData(16,0,"2-1. 각기둥, 각기두으이 전개도",2,1));
        s_list.add(new ClassData(16,0,"2-2. 각뿔",2,2));

        s_list.add(new ClassData(16,0,"3. 소수의 나눗셈",3,0));
        s_list.add(new ClassData(16,0,"3-1. (소수)/(자연수), (자연수)/(자연수)",3,1));
        s_list.add(new ClassData(16,0,"3-2. 몫의 소수점 위치 확인하기",3,2));

        s_list.add(new ClassData(16,0,"4. 비와 비율",4,0));
        s_list.add(new ClassData(16,0,"4-1. 두 수 비교하기, 비 알아보기",4,1));
        s_list.add(new ClassData(16,0,"4-2. 비율, 백분율",4,2));

        s_list.add(new ClassData(16,0,"5. 여러가지 그래프",5,0));
        s_list.add(new ClassData(16,0,"5-1. 그림그래프로 나타내기",5,1));
        s_list.add(new ClassData(16,0,"5-2. 띠그래프, 원그래프",5,2));

        s_list.add(new ClassData(16,0,"6. 직육면체의 부피와 겉넓이",6,0));
        s_list.add(new ClassData(16,0,"6-1. 직육면체의 부피, m^3 알아보기",6,1));
        s_list.add(new ClassData(16,0,"6-2. 직육면체의 겉넓이",6,2));

        //초등학교 6학년 2학기
        s_list.add(new ClassData(16,1,"1. 분수의 나눗셈",1,0));
        s_list.add(new ClassData(16,1,"1-1. 분모가 같은 (분수)/(분수)",1,1));
        s_list.add(new ClassData(16,1,"1-2. 분모가 다른 (분수)/(분수)",1,2));
        s_list.add(new ClassData(16,1,"1-3. (자연수)/(분수)",1,3));
        s_list.add(new ClassData(16,1,"1-4. (분수)/(분수)를 (분수)x(분수)로 계산하기",1,4));
        s_list.add(new ClassData(16,1,"1-5. (분수)/(분수)",1,5));

        s_list.add(new ClassData(16,1,"2. 소수의 나눗셈",2,0));
        s_list.add(new ClassData(16,1,"2-1. (소수)/(소수)",2,1));
        s_list.add(new ClassData(16,1,"2-2. 자릿수가 같은 (소수)/(소수)",2,2));
        s_list.add(new ClassData(16,1,"2-3. 자릿수가 다른 (소수)/(소수)",2,3));
        s_list.add(new ClassData(16,1,"2-4. (자연수)/(소수)",2,4));
        s_list.add(new ClassData(16,1,"2-5. 몫을 반올림하여 나타내기",2,5));
        s_list.add(new ClassData(16,1,"2-6. 나누어 주고 남는 양 알아보기",2,6));

        s_list.add(new ClassData(16,1,"3. 공간과 입체",3,0));
        s_list.add(new ClassData(16,1,"3-1. 어느 방향에서 본 것인지 알아보기",3,1));
        s_list.add(new ClassData(16,1,"3-2. 쌓은 모양과 위에서 본 모양으로 쌓기나무의 개수 구하기",3,2));
        s_list.add(new ClassData(16,1,"3-3. 위, 앞, 옆에서 본 모양으로 쌓은 모양과 쌓기 나무의 개수 구하기",3,3));
        s_list.add(new ClassData(16,1,"3-4. 위에서 본 모양에 수를 쓰는 방법으로 쌓은 모양과 쌓기나무의 개수 구하기",3,4));
        s_list.add(new ClassData(16,1,"3-5. 층별로 나타낸 모양으로 쌓은 모양과 쌓기 나무의 개수 구하기",3,5));
        s_list.add(new ClassData(16,1,"3-6. 여러가지 모양 만들기",3,6));

        s_list.add(new ClassData(16,1,"4. 비례식과 비례배분",4,0));
        s_list.add(new ClassData(16,1,"4-1. 비의 성질 알아보기",4,1));
        s_list.add(new ClassData(16,1,"4-2. 간단한 자연수의 비로 나타내기",4,2));
        s_list.add(new ClassData(16,1,"4-3. 비례식 알아보기",4,3));
        s_list.add(new ClassData(16,1,"4-4. 비례식의 성질 알아보기",4,4));
        s_list.add(new ClassData(16,1,"4-5. 비례식의 활용",4,5));
        s_list.add(new ClassData(16,1,"4-6. 비례배분 알아보기",4,6));
        s_list.add(new ClassData(16,1,"4-7. 비례배분의 활용",4,7));

        s_list.add(new ClassData(16,1,"5. 원의 넓이",5,0));
        s_list.add(new ClassData(16,1,"5-1. 원주의 지름의 관계",5,1));
        s_list.add(new ClassData(16,1,"5-2. 원주율",5,2));
        s_list.add(new ClassData(16,1,"5-3. 원주 구하기",5,3));
        s_list.add(new ClassData(16,1,"5-4. 지름 구하기",5,4));
        s_list.add(new ClassData(16,1,"5-5. 원의 넓이 어림하기",5,5));
        s_list.add(new ClassData(16,1,"5-6. 원의 넓이 구하는 방법 알아보기",5,6));
        s_list.add(new ClassData(16,1,"5-7. 여러 가지 원의 넓이 구하기",5,7));

        s_list.add(new ClassData(16,1,"6. 원기둥, 원뿔, 구",6,0));
        s_list.add(new ClassData(16,1,"6-1. 원기둥 알아보기",6,1));
        s_list.add(new ClassData(16,1,"6-2. 원기둥의 전개도 알아보기",6,2));
        s_list.add(new ClassData(16,1,"6-3. 원뿔 알아보기",6,3));
        s_list.add(new ClassData(16,1,"6-4. 구 알아보기",6,4));
        s_list.add(new ClassData(16,1,"6-5. 여러 가지 모양 만들기",6,5));

        //중학교 1학년 1학기
        s_list.add(new ClassData(21,0,"1. 소인수분해(수와 연산)",1,0));
        s_list.add(new ClassData(21,0,"1-1. 소인수분해",1,1));
        s_list.add(new ClassData(21,0,"1-2. 최대공약수와 최소공배수",1,2));

        s_list.add(new ClassData(21,0,"2. 정수와 유리수(수와 연산)",2,0));
        s_list.add(new ClassData(21,0,"2-1. 정수와 유리수",2,1));
        s_list.add(new ClassData(21,0,"2-2. 정수와 유리수의 계산",2,2));

        s_list.add(new ClassData(21,0,"3. 문자와 식",3,0));
        s_list.add(new ClassData(21,0,"3-1. 문자의 사용과 식의 계산",3,1));
        s_list.add(new ClassData(21,0,"3-2. 일차방정식의 풀이",3,2));
        s_list.add(new ClassData(21,0,"3-3. 일차방정식의 활용",3,3));

        s_list.add(new ClassData(21,0,"4. 좌표평면과 그래프",4,0));
        s_list.add(new ClassData(21,0,"4-1. 좌표와 그래프",4,1));
        s_list.add(new ClassData(21,0,"4-2. 정비례와 반비례",4,2));

        //중학교 1학년 2학기
        s_list.add(new ClassData(21,1,"1. 기본도형",1,0));
        s_list.add(new ClassData(21,1,"1-1. 기본도형",1,1));
        s_list.add(new ClassData(21,1,"1-2. 위치관계",1,2));
        s_list.add(new ClassData(21,1,"1-3. 작도와 합동",1,3));

        s_list.add(new ClassData(21,1,"2. 평면 도형",2,0));
        s_list.add(new ClassData(21,1,"2-1. 다각형",2,1));
        s_list.add(new ClassData(21,1,"2-2. 원과 부채꼴",2,2));

        s_list.add(new ClassData(21,1,"3. 입체 도형",3,0));
        s_list.add(new ClassData(21,1,"3-1. 다면체와 회전체",3,1));
        s_list.add(new ClassData(21,1,"3-2. 입체도형의 겉넓이와 부피",3,2));

        s_list.add(new ClassData(21,1,"4. 통계",4,0));
        s_list.add(new ClassData(21,1,"4-1. 자료의 정리와 해석",4,1));

        //중학교 2학년 1학기
        s_list.add(new ClassData(22,0,"1. 수와 식의 계산",1,0));
        s_list.add(new ClassData(22,0,"1-1. 유리수와 순환소수",1,1));
        s_list.add(new ClassData(22,0,"1-2. 식의 계산",1,2));

        s_list.add(new ClassData(22,0,"2. 일차부등식과 연립일차방정식",2,0));
        s_list.add(new ClassData(22,0,"2-1. 일차부등식",2,1));
        s_list.add(new ClassData(22,0,"2-2. 연립일차방정식",2,2));

        s_list.add(new ClassData(22,0,"3. 일차함수",3,0));
        s_list.add(new ClassData(22,0,"3-1. 일차함수와 그래프",3,1));
        s_list.add(new ClassData(22,0,"3-2. 일차함수와 일차방정식의 관계",3,2));

        //중학교 2학년 2학기
        s_list.add(new ClassData(22,1,"1. 삼각형의 성질",1,0));
        s_list.add(new ClassData(22,1,"1-1. 이등변삼각형",1,1));
        s_list.add(new ClassData(22,1,"1-2. 삼각형의 외심과 내심",1,2));

        s_list.add(new ClassData(22,1,"2. 사격형의 성질",2,0));
        s_list.add(new ClassData(22,1,"2-1. 평행사변형",2,1));
        s_list.add(new ClassData(22,1,"2-2. 여러 가지 사각형",2,2));

        s_list.add(new ClassData(22,1,"3. 도형의 닮음과 피타고라스 정리",3,0));
        s_list.add(new ClassData(22,1,"3-1. 도형의 닮음",3,1));
        s_list.add(new ClassData(22,1,"3-2. 평행선과 선분의 길이의 비",3,2));
        s_list.add(new ClassData(22,1,"3-3. 삼각형의 무게중심",3,3));
        s_list.add(new ClassData(22,1,"3-4. 피타고라스 정리",3,4));

        s_list.add(new ClassData(22,1,"4. 확률",4,0));
        s_list.add(new ClassData(22,1,"4-1. 경우의 수",4,1));
        s_list.add(new ClassData(22,1,"4-2. 확률",4,2));

        //중학교 3학년 1학기
        s_list.add(new ClassData(23,0,"1. 실수와 그 계산",1,0));
        s_list.add(new ClassData(23,0,"1-1. 제곱근과 실수",1,1));
        s_list.add(new ClassData(23,0,"1-2. 근호를 포함한 식의 계산",1,2));

        s_list.add(new ClassData(23,0,"2. 다항식의 인수분해",2,0));
        s_list.add(new ClassData(23,0,"2-1. 인수분해",2,1));

        s_list.add(new ClassData(23,0,"3. 이차방정식",3,0));
        s_list.add(new ClassData(23,0,"3-1. 이차방정식의 풀이",3,1));
        s_list.add(new ClassData(23,0,"3-2. 이차방정식의 활용",3,2));

        s_list.add(new ClassData(23,0,"4. 이차함수",4,0));
        s_list.add(new ClassData(23,0,"4-1. 이차함수와 그 그래프",4,1));
        s_list.add(new ClassData(23,0,"4-2. 이차함수의 그래프와 활용",4,2));

        //중학교 3학년 2학기
        s_list.add(new ClassData(23,1,"1. 통계",1,0));
        s_list.add(new ClassData(23,1,"1-1. 대푯값과 산포도",1,1));

        s_list.add(new ClassData(23,1,"2. 피타고라스 정리",2,0));
        s_list.add(new ClassData(23,1,"2-1. 피타고라스 정리",2,1));
        s_list.add(new ClassData(23,1,"2-2. 피타고라스 정리의 활용",2,2));

        s_list.add(new ClassData(23,1,"3. 삼각비",3,0));
        s_list.add(new ClassData(23,1,"3-1. 삼각비",3,1));
        s_list.add(new ClassData(23,1,"3-2. 삼각비의 활용",3,2));

        s_list.add(new ClassData(23,1,"4. 원의 성질",4,0));
        s_list.add(new ClassData(23,1,"4-1. 원과 직선",4,1));
        s_list.add(new ClassData(23,1,"4-2. 원주각",4,2));
        s_list.add(new ClassData(23,1,"4-3. 원주각의 활용",4,3));

        //수학 상
        s_list.add(new ClassData(31,0,"1. 다항식",1,0));
        s_list.add(new ClassData(31,0,"1-1. 다항식연산",1,1));
        s_list.add(new ClassData(31,0,"1-2. 항등식과 나머지 정리",1,2));
        s_list.add(new ClassData(31,0,"1-3. 인수분해",1,3));

        s_list.add(new ClassData(31,0,"2. 방정식과 부등식",2,0));
        s_list.add(new ClassData(31,0,"2-1. 복소수",2,1));
        s_list.add(new ClassData(31,0,"2-2. 이차방정식",2,2));
        s_list.add(new ClassData(31,0,"2-3. 이차방정식과 이차함수",2,3));
        s_list.add(new ClassData(31,0,"2-4. 여러가지 방정식",2,4));
        s_list.add(new ClassData(31,0,"2-5. 여러가지 부등식",2,5));

        s_list.add(new ClassData(31,0,"3. 도형의 방정식",3,0));
        s_list.add(new ClassData(31,0,"3-1. 평면좌표",3,1));
        s_list.add(new ClassData(31,0,"3-2. 직선의 방정식",3,2));
        s_list.add(new ClassData(31,0,"3-3. 원의 방정식",3,3));
        s_list.add(new ClassData(31,0,"3-4. 도형의 이동",3,4));

        //수학 하
        s_list.add(new ClassData(31,1,"1. 집합과 명제",1,0));
        s_list.add(new ClassData(31,1,"1-1. 집합",1,1));
        s_list.add(new ClassData(31,1,"1-2. 명제",1,2));

        s_list.add(new ClassData(31,1,"2. 함수",2,0));
        s_list.add(new ClassData(31,1,"2-1. 함수",2,1));
        s_list.add(new ClassData(31,1,"2-2. 유리함수와 무리함수",2,2));

        s_list.add(new ClassData(31,1,"3. 경우의 수",3,0));
        s_list.add(new ClassData(31,1,"3-1. 합의 법칙과 곱의 법칙",3,1));
        s_list.add(new ClassData(31,1,"3-2. 순열과 조합",3,2));

        //수학 I
        s_list.add(new ClassData(32,0,"1. 지수함수와 로그함수",1,0));
        s_list.add(new ClassData(32,0,"1-1. 지수",1,1));
        s_list.add(new ClassData(32,0,"1-2. 로그",1,2));
        s_list.add(new ClassData(32,0,"1-3. 지수함수",1,3));
        s_list.add(new ClassData(32,0,"1-4. 로그함수",1,4));

        s_list.add(new ClassData(32,0,"2. 삼각함수",2,0));
        s_list.add(new ClassData(32,0,"2-1. 삼각함수",2,1));
        s_list.add(new ClassData(32,0,"2-2. 삼각함수의 그래프",2,2));
        s_list.add(new ClassData(32,0,"2-3. 삼각함수의 활용",2,3));

        s_list.add(new ClassData(32,0,"3. 수열",3,0));
        s_list.add(new ClassData(32,0,"3-1. 등차수열과 등비수열",3,1));
        s_list.add(new ClassData(32,0,"3-2. 수열의 합",3,2));
        s_list.add(new ClassData(32,0,"3-3. 수학적 귀납법",3,3));

        //수학 II
        s_list.add(new ClassData(32,1,"1. 함수의 극한과 연속",1,0));
        s_list.add(new ClassData(32,1,"1-1. 함수의 극한",1,1));
        s_list.add(new ClassData(32,1,"1-2. 함수의 연속",1,2));

        s_list.add(new ClassData(32,1,"2. 미분",2,0));
        s_list.add(new ClassData(32,1,"2-1. 미분계수와 도함수",2,1));
        s_list.add(new ClassData(32,1,"2-2. 도함수의 활용",2,2));

        s_list.add(new ClassData(32,1,"3. 적분",3,0));
        s_list.add(new ClassData(32,1,"3-1. 부정적분",3,1));
        s_list.add(new ClassData(32,1,"3-2. 정적분",3,2));
        s_list.add(new ClassData(32,1,"3-3. 정적분의 활용",3,3));

        for(int i=0;i<s_list.size();i++){
            SQLiteStatement p = sqliteDb.compileStatement("INSERT INTO ClassData (ClassId, Term, Title, Tag, SubTag) VALUES (?,?,?,?,?);");
            p.bindLong(1, s_list.get(i).ClassId);
            p.bindLong(2, s_list.get(i).Term);
            p.bindString(3, s_list.get(i).Title);
            p.bindLong(4, s_list.get(i).Tag);
            p.bindLong(5, s_list.get(i).SubTag);
            System.out.println(p);
            p.execute();
        }
    }
}
