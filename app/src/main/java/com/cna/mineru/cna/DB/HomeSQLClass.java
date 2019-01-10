package com.cna.mineru.cna.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.cna.mineru.cna.DTO.ExamData;
import com.cna.mineru.cna.DTO.HomeData;
import java.io.File;
import java.util.ArrayList;

//HomeSQLClass
public class HomeSQLClass extends AppCompatActivity {

    SQLiteDatabase sqliteDb;
    Context context;

    public HomeSQLClass(Context context) {
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
            String sqlCreateTb = "CREATE TABLE IF NOT EXISTS Note (" +
                    "Id "       + "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "Title "    + "TEXT," +
                    "Image "    + "BLOB DEFAULT ''," +
                    "Tag "      + "INTEGER" + ");";
            System.out.println(sqlCreateTb);
            sqliteDb.execSQL(sqlCreateTb);
        }
    }

    public ArrayList<HomeData> load_values(){
        ArrayList<HomeData> list = new ArrayList<HomeData>();

        if(sqliteDb != null){
            String sqlQueryTb1 = "SELECT * FROM Note ORDER BY Id DESC";
            Cursor cursor = null;

            System.out.println(sqlQueryTb1);

            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            for(int i=0;i<cursor.getCount();i++){
                cursor.moveToNext();
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                int tag = cursor.getInt(0);
                list.add(new HomeData(id, title,tag));
            }
        }
        return list;
    }

    public void update_item(int id, String title,int tag){
        if(sqliteDb != null){
            String sqlQueryTb1 = "UPDATE Note SET Title = '" +  title + "', " +
                    "Tag = "+ tag + " WHERE Id = " + id + ";";

            System.out.println(sqlQueryTb1);

            sqliteDb.execSQL(sqlQueryTb1);
        }
    }

    public void delete_item(int id){
        if(sqliteDb != null){
            String sqlQueryTb1 = "DELETE FROM Note WHERE id =" + id + ";";

            System.out.println(sqlQueryTb1);
            sqliteDb.execSQL(sqlQueryTb1);
        }
    }

    public ArrayList<ExamData> getList(){
        int count = getCount();
        int[] tmp_arr = new int[count];
        int index;
        ArrayList<ExamData> list = new ArrayList<ExamData>();
        int[] rnd = new int[4];
        /*
        Random generator = new Random();
        Log.d("TAG","Mineru : 0");
        for(int i=0;i<4;i++) {
            index=generator.nextInt(tmp_arr.length);
            Log.d("TAG","Mineru : index : " + index);
            rnd[i] = tmp_arr[index];
            Log.d("TAG","Mineru : rnd : " + rnd[i]);
            /*
            for(int j=0;j<i;j++){
                if(rnd[i] == rnd[j]){
                    i--;
                    break;
                }
            }
        }
        */
        rnd[0]= 1;
        rnd[1]= 3;
        rnd[2]= 4;
        rnd[3]= 2;
        if(sqliteDb != null) {
            for(int i=0;i<4;i++){
                String sqlQueryTb1 = "SELECT * FROM Note WHERE Id = "+rnd[i]+";";
                Cursor cursor = null;
                cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
                cursor.moveToNext();
                int examid = cursor.getInt(0);
                String title = cursor.getString(1);
                list.add(new ExamData(title,examid));
            }
        }
        Log.d("TAG","Mineru : 2");

        return list;
    }
    public int getCount(){
        int result = 0;
        if(sqliteDb != null) {
            String sqlQueryTb1 = "SELECT * FROM Note;";
            Cursor cursor = null;

            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            for(int i=0;i<cursor.getCount();i++){
                cursor.moveToNext();
                result++;
            }
        }
        return result;
    }
    public HomeData select_item(int id){
        HomeData data;
        if(sqliteDb != null){
            String sqlQueryTb1 = "SELECT * FROM Note WHERE id =" + id + ";";
            Cursor cursor = null;

            System.out.println(sqlQueryTb1);

            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            cursor.moveToNext();
            id = cursor.getInt(0);
            String title = cursor.getString(1);
            data = new HomeData(id,title,0);
        }
        else{
            data = new HomeData(id,"Error",0);
        }
        return data;
    }

    public void add_values(String title, int tag, byte[] image){
        if (sqliteDb != null) {
            Log.d("TAG","Mineru : " + image);
            if (image.length>1){
                SQLiteStatement p = sqliteDb.compileStatement("INSERT INTO NOTE (Title, Tag, Image) VALUES (?,?,?);");
                p.bindString(1,title);
                p.bindLong(2,tag);
                p.bindBlob(3,image);
                System.out.println(p) ;
                p.execute();
            }
            else{
                SQLiteStatement p = sqliteDb.compileStatement("INSERT INTO NOTE (Title, Tag) VALUES (?,?);");
                p.bindString(1,title);
                p.bindLong(2,tag);
                System.out.println(p) ;
                p.execute();
            }
        }
    }
}
