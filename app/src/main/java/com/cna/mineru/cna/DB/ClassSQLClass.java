package com.cna.mineru.cna.DB;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

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
