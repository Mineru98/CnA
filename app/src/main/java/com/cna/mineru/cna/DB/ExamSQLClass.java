package com.cna.mineru.cna.DB;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.cna.mineru.cna.DTO.ExamData;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//TestSQLClass
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

    private void init_Tables(){
        if(sqliteDb != null){
            String sqlCreateTb = "CREATE TABLE IF NOT EXISTS Exam (" +
                    "Id "        + "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "ExamId "    + "INTEGER DEFAULT 0," +
                    "ExamRoomId "+ "INTEGER DEFAULT 0," +
                    "Title "     + "TEXT," +
                    "Tag "       + "INTEGER DEFAULT 0," +
                    "ExamTitle " + "TEXT DEFAULT '');";
            System.out.println(sqlCreateTb);
            sqliteDb.execSQL(sqlCreateTb);
        }
    }

    public ArrayList<ExamData> load_values(){
        ArrayList<ExamData> list = new ArrayList<ExamData>();

        if(sqliteDb != null){
            String sqlQueryTb1 = "SELECT * FROM Exam WHERE TAG = 3 ORDER BY Id DESC";
            Cursor cursor = null;

            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            for(int i=0;i<cursor.getCount();i++){
                cursor.moveToNext();
                int id = cursor.getInt(2);
                String examtitle = cursor.getString(5);
                list.add(new ExamData(examtitle,id));
            }
        }
        return list;
    }
    public boolean make_Exam(){
        int count = 0;
        if (sqliteDb != null) {
            homeSQLClass = new HomeSQLClass(context);
            count = homeSQLClass.getCount();

            if(count>=4){
                ArrayList<ExamData> list;
                list = homeSQLClass.getList();
                for(int i=0;i<4;i++){
                    String sqlInsert = "";
                    if(i==0){
                        sqlInsert = "INSERT INTO Exam " +
                                "(ExamId, Tag, Title) VALUES (" +
                                list.get(i).examid + "," +
                                "1 ,"+
                                "'" + list.get(i).title + "'" + ")" ;
                    }
                    else{
                        sqlInsert = "INSERT INTO Exam " +
                                "(ExamId, Tag, Title) VALUES (" +
                                list.get(i).examid + "," +
                                "0 ,"+
                                "'" + list.get(i).title + "'" + ")" ;
                    }
                    System.out.println(sqlInsert) ;
                    sqliteDb.execSQL(sqlInsert) ;
                }
                exam_first_update();

            }else{
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
                return false;
            }
        }
        return true;
    }
    private void exam_first_update(){
        if (sqliteDb != null) {
            String sql_select = "SELECT Id FROM Exam WHERE Tag = 1;";
            String sql_select2 = "SELECT * FROM Exam WHERE Tag = 3;";
            Cursor cursor = null;
            cursor = sqliteDb.rawQuery(sql_select, null);
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
            String sql_update2 = "UPDATE Exam SET ExamRoomId = " + id + ",Tag = 3, ExamTitle = '" +
                    new SimpleDateFormat("yyyy년_MM월_dd일").format(new Date(System.currentTimeMillis())) +
                    "_"+ count +"' WHERE Tag = 1;";
            sqliteDb.execSQL(sql_update1);
            sqliteDb.execSQL(sql_update2);
        }
    }
}
