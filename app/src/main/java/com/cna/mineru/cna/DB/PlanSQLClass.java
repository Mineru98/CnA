package com.cna.mineru.cna.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import com.cna.mineru.cna.DTO.PlanData;

import java.io.File;
import java.util.ArrayList;

//PlanSQLClass
public class PlanSQLClass extends AppCompatActivity {

    SQLiteDatabase sqliteDb;
    Context context;

    public PlanSQLClass(Context context) {
        this.context = context;
        sqliteDb = init_database(context);

        init_Tables();

        //load_values();
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
            String sqlCreateTb = "CREATE TABLE IF NOT EXISTS _Plan (" +
                    "Id "        + "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "Title "     + "TEXT DEFAULT '수학'," +
                    "Form "      + "INTEGER DEFAULT 0," +
                    "Gold "      + "INTEGER," +
                    "GoldType "  + "INTEGER  DEFAULT 0," +
                    "Deadline "  + "NUMBERIC," +
                    "Progress "   + "INTEGER DEFAULT 0" + ");";
            System.out.println(sqlCreateTb);
            sqliteDb.execSQL(sqlCreateTb);
        }
    }

    public ArrayList<PlanData> load_values(String Deadline){
        ArrayList<PlanData> list = new ArrayList<PlanData>();

        if(sqliteDb != null){
            String sqlQueryTb1 = "SELECT * FROM _Plan WHERE Deadline = '"+ Deadline +"' ORDER BY Id DESC";
            Cursor cursor = null;

            System.out.println(sqlQueryTb1);

            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            for(int i=0;i<cursor.getCount();i++){
                cursor.moveToNext();
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                int form =  cursor.getInt(2);
                int gold =  cursor.getInt(3);
                String deadline = cursor.getString(5);
                int progress = cursor.getInt(6);
                list.add(new PlanData(id,title,form,gold,deadline,progress));
            }
        }
        return list;
    }
    public void add_values(String title, int form, int gold, String deadline){
        if (sqliteDb != null) {

            String sqlInsert = "INSERT INTO _Plan " +
                    "(Title, Form, Gold, Deadline) VALUES (" +
                    "'" + title + "', " +
                    form  + ", " +
                    gold  + ", " +
                    "'" + deadline + "'" +
                    ")" ;

            System.out.println(sqlInsert) ;

            sqliteDb.execSQL(sqlInsert) ;
        }
    }
}
