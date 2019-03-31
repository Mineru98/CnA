package com.cna.mineru.cna.DB;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/*
    오프라인일때 서버 통신을 해야할 경우
    데이터를 쌓아뒀다 한번에 일괄 처리하는 DB

*/

public class TmpSQLClass  {
    TmpSQLClass tmpSQLClass;
    SQLiteDatabase sqliteDb;
    Context context;

    public TmpSQLClass(Context context) {
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
            String sqlCreateTb = "CREATE TABLE IF NOT EXISTS Tmp (" +
                    "Id "           + "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "ExecuteNumber "    + "INTEGER DEFAULT 1," +
                    "ExecuteId "    + "INTEGER DEFAULT 0);";
            System.out.println(sqlCreateTb);
            sqliteDb.execSQL(sqlCreateTb);
        }
    }
    //ExecuteNumber가 1이면 Note 추가에 대한 임시 테이블
    //ExecuteId에는 실행해야할 열의 id값을 저장해 둔다.
}
