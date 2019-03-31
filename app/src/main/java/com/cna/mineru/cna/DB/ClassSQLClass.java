package com.cna.mineru.cna.DB;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/*
    사용자의 학년 변화에 따라
    보이는 그래프 Type을 변경해주기 위한 DB
    This DataBase is used to change the graph type according to the user's grade change.
*/

public class ClassSQLClass {
    ClassSQLClass classSQLClass;
    SQLiteDatabase sqliteDb;
    Context context;

    public ClassSQLClass(Context context) {
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


    // Class 테이블 생성 메소드
    // Methods to create the Class table
    private void init_Tables(){
        if(sqliteDb != null){
            String sqlCreateTb = "CREATE TABLE IF NOT EXISTS Class (" +
                    "Id "           + "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "ClassId "      + "INTEGER DEFAULT 0," +
                    "ClassText "    + "TEXT DEFAULT '');";
            System.out.println(sqlCreateTb);
            sqliteDb.execSQL(sqlCreateTb);
        }
    }
}
