package com.cna.mineru.cna.DB;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.cna.mineru.cna.DTO.ExamData;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
    시험 결과 관리하는 DB
    This DataBase is used to manage exam results

*/

public class ExamSQLClass extends AppCompatActivity {

    HomeSQLClass homeSQLClass;
    SQLiteDatabase sqliteDb;
    Context context;

    public ExamSQLClass(Context context) {
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
            String sqlCreateTb = "CREATE TABLE IF NOT EXISTS Exam (" +
                    "Id "          + "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "NoteId "      + "INTEGER DEFAULT 0," +
                    "ExamRoomId "  + "INTEGER DEFAULT 0," +
                    "Title "       + "TEXT," +
                    "Tag "         + "INTEGER DEFAULT 0," +
                    "TimeToSolve " + "INTEGER," +
                    "isSolved "    + "BOOLEAN DEFAULT 0," +
                    "ExamTitle "   + "TEXT DEFAULT ''," +
                    "CreateDate "  + "DATE);";
            System.out.println(sqlCreateTb);
            sqliteDb.execSQL(sqlCreateTb);
        }
    }

    // Exam Table에 등록된 시험 결과를 ListView에
    // 보여주기 위한 Data Loading 메소드
    // This is a data loading method for
    // displaying the Exam results registered in the Exam table in the ListView.
    @SuppressLint("Recycle")
    public ArrayList<ExamData> load_values(){
        ArrayList<ExamData> list = new ArrayList<>();

        if(sqliteDb != null){
            String sqlQueryTb1 = "SELECT Id, ExamTitle, ExamRoomId FROM Exam WHERE TAG = 3 ORDER BY Id DESC";
            Cursor cursor = null;

            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            for(int i=0;i<cursor.getCount();i++){
                cursor.moveToNext();
                int RoomId = cursor.getInt(0);
                String examtitle = cursor.getString(1);
                list.add(new ExamData(examtitle,RoomId));
            }
        }
        return list;
    }

    @SuppressLint("Recycle")
    public int get_Exam_RoomId(){
        int RoomId = 0;
        if(sqliteDb != null){
            String sqlQueryTb1 = "select ExamRoomId from Exam ORDER BY Id DESC limit 1;";
            Cursor cursor = null;
            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            cursor.moveToNext();
            RoomId = cursor.getInt(0);
        }
        return RoomId;
    }

    public int get_last_exam(){
        int RoomId = 0;
        if(sqliteDb != null) {
            String sqlQueryTb1 = "SELECT Id FROM Exam WHERE TAG = 3 ORDER BY Id DESC limit 1";
            Cursor cursor = null;
            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            cursor.moveToNext();
            RoomId = cursor.getInt(0);
        }
        return RoomId;
    }

    // 특정 시험에 대한 데이터 지표를 볼 때
    // NoteId를 통해 Data Loading을 하는 메소드
    // Methods for Data Loading via NoteId when viewing data indicators for a particular Exam
    @SuppressLint("Recycle")
    public ArrayList<ExamData> get_point_values(int RoomId){
        ArrayList<ExamData> list = new ArrayList<ExamData>();
        if(sqliteDb != null){
            String sqlQueryTb1 = "select Id, NoteId, Title, TimeToSolve, isSolved from Exam where ExamRoomId = " + RoomId + ";";
            Cursor cursor = null;
            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            for(int i=0;i<cursor.getCount();i++){
                cursor.moveToNext();
                int Id = cursor.getInt(0);
                int NoteId = cursor.getInt(1);
                String Title = cursor.getString(2);
                long TTS = cursor.getLong(3);
                int isSolved = cursor.getInt(4);
                list.add(new ExamData(Id,NoteId,Title,TTS,isSolved));
            }
        }
        return list;
    }

    // 랜덤으로 문제를 섞어 시험지를 만드는 메소드
    // Methods for creating exam papers by randomly mixing problems
    public ArrayList<ExamData> make_Exam(int exam_count){
        int count = 0;
        ArrayList<ExamData> list = new ArrayList<>();
        if (sqliteDb != null) {
            homeSQLClass = new HomeSQLClass(context);
            count = homeSQLClass.getCount();
            if (count < 4) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("알림");
                dialog.setMessage(
                        "현재 등록 된 노트의 수가 부족합니다.\n" +
                                "4개 이상의 노트가 등록되어야\n" +
                                "시험을 볼 수 있습니다.");
                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return list;
            } else if (count < exam_count) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("알림");
                dialog.setMessage(
                        "현재 등록 된 노트의 수보다 많습니다.\n");
                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return list;
            } else {
                list = homeSQLClass.getList(exam_count);
                for (int i = 0; i < exam_count; i++) {
//                    Log.d("TAG","Mineru NoteId " + i + " : " + list.get(i).NoteId);
//                    Log.d("TAG","Mineru Title " + i + " : "  + list.get(i).Title);
                    String sqlInsert = "";
                    if (i == 0) {
                        sqlInsert = "INSERT INTO Exam " +
                                "(NoteId, Tag, Title) VALUES (" +
                                list.get(i).NoteId + "," +
                                "1 ," +
                                "'" + list.get(i).Title + "'" + ")";
                    } else {
                        sqlInsert = "INSERT INTO Exam " +
                                "(NoteId, Tag, Title) VALUES (" +
                                list.get(i).NoteId + "," +
                                "0 ," +
                                "'" + list.get(i).Title + "'" + ")";
                    }
                    System.out.println(sqlInsert);
                    sqliteDb.execSQL(sqlInsert);
                }
                exam_first_update();
            }
        }
        return list;
    }

    // Random Exam Data를 생성한 다음,
    // Tag를 통해 테이블을 안정적으로 만들기 위한 메소드
    // After creating the Random Exam Data,
    // the method for making the table stable through Tag
    @SuppressLint("Recycle")
    private void exam_first_update(){
        if (sqliteDb != null) {
            String sql_select1 = "SELECT Id FROM Exam WHERE Tag = 1;";
            String sql_select2 = "SELECT Id FROM Exam WHERE Tag = 3;";
            Cursor cursor = null;
            cursor = sqliteDb.rawQuery(sql_select1, null);
            cursor.moveToNext();
            int id = cursor.getInt(0);
            int count=0;
            cursor = sqliteDb.rawQuery(sql_select2, null);
            for(int i=0;i<cursor.getCount();i++){
                cursor.moveToNext();
                count++;
            }
            count++;

            String sql_update1 = "UPDATE Exam SET ExamRoomId = " + id + ",Tag = 2  WHERE Tag = 0;";
            @SuppressLint("SimpleDateFormat")
            String sql_update2 = "UPDATE Exam SET ExamRoomId = " + id + ",Tag = 3, ExamTitle = '" +
                    new SimpleDateFormat("yyyy년 MM월 dd일").format(new Date(System.currentTimeMillis())) +
                    "_"+ count +"회차' WHERE Tag = 1;";
            sqliteDb.execSQL(sql_update1);
            sqliteDb.execSQL(sql_update2);
        }
    }

    public void update_result(int[] isSolved, long[] TTS, int RoomId, int[] NoteId){
        if(sqliteDb != null) {
            for(int i=0;i<isSolved.length; i++){
                String sqlQueryTb1 = "UPDATE Exam SET TimeToSolve = " + TTS[i] + ",isSolved = " + isSolved[i] +", CreateDate = (SELECT DATE('now','+9 hours')) WHERE ExamRoomId = "+ RoomId + " AND NoteId = " + NoteId[i] +";";
                System.out.println(sqlQueryTb1);
                sqliteDb.execSQL(sqlQueryTb1);
            }
        }
    }

    public void delete_exam(int id){
        if(sqliteDb != null) {
            String sqlQueryTb1 = "DELETE FROM Exam WHERE id =" + id + ";";
            System.out.println(sqlQueryTb1);
            sqliteDb.execSQL(sqlQueryTb1);
        }
    }

    public void reset_app(){
        if (sqliteDb != null) {
            String sqlInsert = "DELETE FROM Exam;";
            System.out.println(sqlInsert) ;
            sqliteDb.execSQL(sqlInsert) ;
        }
    }
}
