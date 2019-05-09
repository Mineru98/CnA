package com.cna.mineru.cna.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AppCompatActivity;


import java.io.File;
import java.util.ArrayList;

/*
    등록한 Note의 Image의 Index 관리하는 DB

*/

public class ImageSQLClass extends AppCompatActivity {

    ImageSQLClass imageSQLClass;
    SQLiteDatabase sqliteDb;
    Context context;

    public ImageSQLClass(Context context) {
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
            String sqlCreateTb = "CREATE TABLE IF NOT EXISTS Image (" +
                    "Id "        + "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "NoteId "    + "INTEGER DEFAULT 0," +
                    "ImageId "   + "INTEGER DEFAULT 0," +
                    "isSolve "   + "BOOLEAN DEFAULT 0," +
                    "Image "     + "BLOB DEFAULT '');";
            System.out.println(sqlCreateTb);
            sqliteDb.execSQL(sqlCreateTb);
        }
    }

    public ArrayList<byte[]> getImg(long NoteId, long isSolve) {
        ArrayList<byte[]> image = new ArrayList<>();
        if (sqliteDb != null) {
            String sqlQueryTb1 = "SELECT Image FROM Image WHERE NoteId = " + NoteId + " And isSolve = " + isSolve + ";";
            Cursor cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            while(cursor.moveToNext()){
                image.add(cursor.getBlob(0));
            }
        }
        return image;
    }

    public int getCount(long NoteId, long isSolve) {
        int count=0;
        if (sqliteDb != null) {
            String sqlQueryTb1 = "SELECT Image FROM Image WHERE NoteId = " + NoteId + " And isSolve = " + isSolve + ";";
            Cursor cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            while(cursor.moveToNext()){
                count++;
            }
        }
        return count;
    }

    public void add_value(long NoteId, long ImageId, long isSolve, byte[] image){
        if (sqliteDb != null) {
            if (image.length>1){
                SQLiteStatement p = sqliteDb.compileStatement("INSERT INTO Image (NoteId, ImageId, isSolve, Image) VALUES (?,?,?,?);");
                p.bindLong(1, NoteId);
                p.bindLong(2, ImageId);
                p.bindLong(3, isSolve);
                p.bindBlob(4, image);
                System.out.println(p) ;
                p.execute();
            }
        }
    }

    public void add_value2(){
        if (sqliteDb != null) {
            String sqlQueryTb1 = "SELECT Id FROM Image WHERE ImageId = 0;";
            Cursor cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            cursor.moveToNext();
            int id = cursor.getInt(0);
            System.out.println(sqlQueryTb1);

            String sqlQueryTb2 = "UPDATE Image SET ImageId = "+ id +" WHERE ImageId = 0;";
            sqliteDb.execSQL(sqlQueryTb2);
            System.out.println(sqlQueryTb2);
        }
    }

    public void delete_item(int Note_Id){
        if (sqliteDb != null) {
            String sqlInsert = "DELETE FROM Image WHERE NoteId = " + Note_Id + ";";
            System.out.println(sqlInsert) ;
            sqliteDb.execSQL(sqlInsert) ;
        }
    }
}

