package com.cna.mineru.cna.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import com.cna.mineru.cna.DTO.GraphData;
import com.cna.mineru.cna.DTO.PlanData;

import java.io.File;
import java.util.ArrayList;

//GraphSQLClass
public class UserSQLClass extends AppCompatActivity {

    SQLiteDatabase sqliteDb;
    Context context;

    public UserSQLClass(Context context) {
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
            String sqlCreateTb = "CREATE TABLE IF NOT EXISTS User_Info (" +
                    "User_Id "       + "INTEGER NOT NULL PRIMARY KEY DEFAULT 1," +
                    "Name "     + "TEXT ," +
                    "isGoogle " + "BOOLEAN NOT NULL  DEFAULT 0)";
            System.out.println(sqlCreateTb);

            sqliteDb.execSQL(sqlCreateTb);
        }
    }

    public int getUserId(){
        int id =0;
        if(sqliteDb != null){
            Cursor cursor = null;
            String sqlQueryTb1 = "SELECT * FROM User_Info;";
            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            cursor.moveToNext();
            id = cursor.getInt(0);
        }
        return id;
    }
    public boolean delete_User() {
        int isGoogle = 0;

        if(sqliteDb != null){
            Cursor cursor = null;
            String sqlQueryTb1 = "SELECT * FROM User_Info;";
            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            cursor.moveToNext();
            isGoogle = cursor.getInt(2);
        }
        if(isGoogle==1){
            return true;
        }else{
            return false;
        }
    }

    public void update_isGoogle(boolean isGoogle, String email, String name, int User_Id){
        if(sqliteDb != null){
            String sqlQueryTb1 = "UPDATE User_Info SET isGoogle = '" +  isGoogle + "', Name = '"+name+"', User_Id = "+User_Id+";";
            System.out.println(sqlQueryTb1);
            sqliteDb.execSQL(sqlQueryTb1);
        }
    }

    public String get_Name(){
        Cursor cursor = null;
        String name="";
        if(sqliteDb != null){
            String sqlQueryTb1 = "SELECT Name FROM User_Info;";
            System.out.println(sqlQueryTb1);
            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            for(int i=0;i<cursor.getCount();i++){
                cursor.moveToNext();
                name = cursor.getString(0);
            }
        }
        return name;
    }
    public void add_values(int User_Id, String name, int isGoogle){
        if (sqliteDb != null) {

            String sqlInsert = "INSERT INTO User_Info " +
                    "(User_Id, Name, isGoogle) VALUES (" +
                    User_Id + ", '"+ name +"', " + isGoogle + ")" ;

            System.out.println(sqlInsert) ;

            sqliteDb.execSQL(sqlInsert) ;
        }
    }
}
