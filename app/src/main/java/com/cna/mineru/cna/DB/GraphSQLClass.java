package com.cna.mineru.cna.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import com.cna.mineru.cna.DTO.GraphData;

import java.io.File;
import java.util.ArrayList;

/*
    노트 및 시험 결과 그래프 관리 DB
    This DataBase is used to managing notes and test results graph
*/

public class GraphSQLClass extends AppCompatActivity {

    SQLiteDatabase sqliteDb;
    Context context;

    public GraphSQLClass(Context context) {
        this.context = context;
        sqliteDb = init_database(context);
        init_Tables();
        load_values();
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

    // Graph 테이블 생성 메소드
    // Methods to create the Graph table
    private void init_Tables(){
        if(sqliteDb != null){
            String sqlCreateTb = "CREATE TABLE IF NOT EXISTS Graph (" +
                    "Id "        + "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "Note_Id "   + "INTEFER,"+
                    "Note_Type " + "INTEGER" + ")";

            System.out.println(sqlCreateTb);
            sqliteDb.execSQL(sqlCreateTb);
        }
    }

    // 등록된 Note에 대한 정보를 도식화하기 위한
    // Data를 Loading 하는 메소드
    //

    public ArrayList<GraphData> load_values(){
        ArrayList<GraphData> list = new ArrayList<>();

        if(sqliteDb != null){
            String sqlQueryTb1 = "SELECT Id, Note_Type FROM Graph ORDER BY Id ASC";
            Cursor cursor = null;

            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            for(int i=0;i<cursor.getCount();i++){
                cursor.moveToNext();
                int id = cursor.getInt(0);
                int note_type = cursor.getInt(1);
                list.add(new GraphData(id,note_type));
            }
        }
        return list;
    }

    // Note와 같은 내용의 Graph 데이터를
    // Indexing을 하기 위한 메소드
    //
    public void add_values(int Note_Id, int Note_Type){
        if (sqliteDb != null) {

            String sqlInsert = "INSERT INTO Graph " +
                    "(Note_Id, Note_Type) VALUES (" +
                    Note_Id + ", " +
                    Note_Type + ");" ;

            System.out.println(sqlInsert) ;

            sqliteDb.execSQL(sqlInsert) ;
        }
    }

    // Note의 내용이 변경 되었을 때,
    // 그래프의 성질을 변경하기 위한 메소드
    //
    public void update_value(int Note_Id, int Note_Type){
        if (sqliteDb != null) {

            String sqlInsert = "UPDATE Graph SET Note_Type = " + Note_Type
                    +" WHERE Note_Id = " + Note_Id + ";";
            System.out.println(sqlInsert) ;
            sqliteDb.execSQL(sqlInsert) ;
        }
    }

    // Indexing 된 Note의 정보가 삭제 되었을 때,
    // Graph 테이블의 내용을 동기화 하기 위한 메소드
    //

    public void delete_value(int Note_Id){
        if (sqliteDb != null) {
            String sqlInsert = "DELETE FROM Graph WHERE Note_Id = " + Note_Id + ";";
            System.out.println(sqlInsert) ;
            sqliteDb.execSQL(sqlInsert) ;
        }
    }

    public void reset_app(){
        if (sqliteDb != null) {
            String sqlInsert = "DELETE FROM Graph;";
            System.out.println(sqlInsert) ;
            sqliteDb.execSQL(sqlInsert) ;
        }
    }
}
