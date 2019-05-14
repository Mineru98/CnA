package com.cna.mineru.cna.DB;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.cna.mineru.cna.DTO.ExamData;
import com.cna.mineru.cna.DTO.NotiData;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*

*/

public class NotiSQLClass extends AppCompatActivity {

    SQLiteDatabase sqliteDb;
    Context context;

    public NotiSQLClass(Context context) {
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

    // Notificaiton 테이블 생성 메소드
    // Methods to create the Exam table
    private void init_Tables(){
        if(sqliteDb != null){
            String sqlCreateTb = "CREATE TABLE IF NOT EXISTS Notificaiton (" +
                    "Id "          + "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "NotiTag "     + "INTEGER DEFAULT 1," +
                    "Title "       + "TEXT DEFAULT ''," +
                    "SubTitle "    + "TEXT DEFAULT '');";
            System.out.println(sqlCreateTb);
            sqliteDb.execSQL(sqlCreateTb);
        }
    }

    public ArrayList<NotiData> load_value(){
        ArrayList<NotiData> list = new ArrayList<>();
        if(sqliteDb != null){
            String sqlQueryTb1 = "SELECT Id, NotiTag, Title, SubTitle FROM Notificaiton ORDER BY Id DESC";
            Cursor cursor = null;
            System.out.println(sqlQueryTb1);
            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            for(int i=0;i<cursor.getCount();i++){
                cursor.moveToNext();
                int id = cursor.getInt(0);
                int tag = cursor.getInt(1);
                String title = cursor.getString(2);
                String subtitle = cursor.getString(3);
                list.add(new NotiData(id, tag, title, subtitle));
            }
        }
        return list;
    }

    public void add_notification(int Id, int NotiTag, String Title, String SubTitle){
        if (sqliteDb != null) {
            SQLiteStatement p = sqliteDb.compileStatement("INSERT INTO Note (Id, NotiTag, Title, SubTitle) VALUES (?,?,?,?);");
            p.bindLong(1, Id);
            p.bindLong(2, NotiTag);
            p.bindString(3, Title);
            p.bindString(4, SubTitle);
            System.out.println(p);
            p.execute();
        }
    }
}
