package com.cna.mineru.cna.DB;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.cna.mineru.cna.DTO.ExamData;
import com.cna.mineru.cna.DTO.HomeData;
import java.io.File;
import java.util.ArrayList;

/*
    노트의 로딩, 삽입, 수정, 제거를 관리하기 위한 DB

*/

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
                    "Id "           + "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "Title "        + "TEXT," +
                    "Image "        + "BLOB DEFAULT ''," +
                    "Note_Current " + "INTEGER DEFAULT 100," +
                    "Note_Type "          + "INTEGER" + ");";
            System.out.println(sqlCreateTb);
            sqliteDb.execSQL(sqlCreateTb);
        }
    }

    public ArrayList<HomeData> load_values(){
        ArrayList<HomeData> list = new ArrayList<>();

        if(sqliteDb != null){
            String sqlQueryTb1 = "SELECT Id, Title FROM Note ORDER BY Id DESC";
            Cursor cursor = null;

            System.out.println(sqlQueryTb1);

            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            for(int i=0;i<cursor.getCount();i++){
                cursor.moveToNext();
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                list.add(new HomeData(id, title, 0,null));
            }
        }
        return list;
    }

    public void update_item(int id, String title, int note_type){
        if(sqliteDb != null){
            String sqlQueryTb1 = "UPDATE Note SET Title = '" +  title + "', " +
                    "Note_Type = "+ note_type + " WHERE Id = " + id + ";";

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

    public int[] getItemIdEach(){
        int[] result = new int[getCount()];
        if(sqliteDb != null) {
            String sqlQueryTb1 = "SELECT id FROM Note;";
            @SuppressLint("Recycle")
            Cursor cursor = sqliteDb.rawQuery(sqlQueryTb1, null);

            for (int i = 0; i < getCount(); i++) {
                cursor.moveToNext();
                result[i] = cursor.getInt(0);
            }
        }
        return result;
    }
    public ArrayList<ExamData> getList(int exam_count){
        int count = getCount(); //현재 Note Table에 있는 모든 Note ID의 갯수를 가져옴.
        int[] tmp_arr = new int[count];//Note ID 갯수만큼 tmp_arr 배열 할당.
        ArrayList<ExamData> list = new ArrayList<ExamData>();
        int[] rnd = new int[exam_count];
        Log.d("TAG","Mineru : 1");

        if(sqliteDb != null) {
            String sqlQueryTb1 = "SELECT * FROM Note;";
            Cursor cursor = sqliteDb.rawQuery(sqlQueryTb1, null);

            for(int i=0;i<cursor.getCount();i++) {
                cursor.moveToNext();
                tmp_arr[i] = cursor.getInt(0);
            }
            for(int i=0;i<exam_count;i++){
                rnd[i]=tmp_arr[i];
                // tmp_arr에 있는 배열을 무작위로 섞어서 rnd에 exam_count만큼 넣어주는 코드 삽입 요청
            }
            for(int i=0;i<exam_count;i++) {
                String sqlQueryTb2 = "SELECT * FROM Note WHERE Id = " + rnd[i] + ";";
                cursor = sqliteDb.rawQuery(sqlQueryTb2, null);
                cursor.moveToNext();
                rnd[i] = cursor.getInt(0);
                list.add(new ExamData(cursor.getString(1), rnd[i]));
            }
        }
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
            String sqlQueryTb1 = "SELECT Id, Title, Note_Type FROM Note WHERE id = " + id + ";";
            String sqlQueryTb2 = "SELECT Image FROM Note WHERE id = " + id + ";";
            Cursor cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            Cursor image_curs = sqliteDb.rawQuery(sqlQueryTb2, null);

            System.out.println(sqlQueryTb1);

            cursor.moveToNext();
            image_curs.moveToNext();

            id = cursor.getInt(0);
            String title = cursor.getString(1);
            int tag = cursor.getInt(2);
            byte[] image = null;

            data = new HomeData(id,title,tag,image);
        }
        else{
            data = new HomeData(id,"Error",0,null);
        }
        return data;
    }

    public int getId(){
        int id=0;
        if(sqliteDb != null) {
            String sqlQueryTb1 = "select id from Note order by id desc";
            Cursor cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            System.out.println(sqlQueryTb1);
            cursor.moveToNext();
            id = cursor.getInt(0);
        }
        return id;
    }


    public void add_values(String note_id, String title, int note_type){
        int id = Integer.parseInt(note_id);
        if (sqliteDb != null) {
            SQLiteStatement p = sqliteDb.compileStatement("INSERT INTO NOTE (Id, Title, Note_Type) VALUES (?,?,?);");
            p.bindLong(1, id);
            p.bindString(2, title);
            p.bindLong(3, note_type);
            System.out.println(p);
            p.execute();
        }
    }
}
