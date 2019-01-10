package com.cna.mineru.cna.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import com.cna.mineru.cna.DTO.GraphData;

import java.io.File;
import java.util.ArrayList;

//GraphSQLClass
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

    private void init_Tables(){
        if(sqliteDb != null){
            String sqlCreateTb = "CREATE TABLE IF NOT EXISTS Graph (" +
                    "Id "       + "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "Home_Tag " + "INTEGER" + ")";

            System.out.println(sqlCreateTb);
            sqliteDb.execSQL(sqlCreateTb);
        }
    }

    public ArrayList<GraphData> load_values(){
        ArrayList<GraphData> list = new ArrayList<GraphData>();

        if(sqliteDb != null){
            String sqlQueryTb1 = "SELECT * FROM Graph ORDER BY Id ASC";
            Cursor cursor = null;

            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            for(int i=0;i<cursor.getCount();i++){
                cursor.moveToNext();
                int id = cursor.getInt(0);
                int tag = cursor.getInt(1);
                list.add(new GraphData(id,tag));
            }
        }
        return list;
    }
    public void add_values(int Home_Tag){
        if (sqliteDb != null) {

            String sqlInsert = "INSERT INTO Graph " +
                    "(Home_Tag) VALUES (" +
                    "'" + Home_Tag + "'" + ")" ;

            System.out.println(sqlInsert) ;

            sqliteDb.execSQL(sqlInsert) ;
        }
    }
}
