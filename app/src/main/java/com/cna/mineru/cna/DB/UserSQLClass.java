package com.cna.mineru.cna.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;


import com.cna.mineru.cna.DTO.UserData;

import java.io.File;
import java.util.ArrayList;

/*
    사용자 정보 관리 DB

*/

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
                    "User_Id "        + "INTEGER NOT NULL PRIMARY KEY DEFAULT 1," +
                    "Name "           + "TEXT DEFAULT '게스트'," +
                    "ClassId "        + "INTEGER DEFAULT 11," +
                    "isFirst "        + "BOOLEAN NOT NULL DEFAULT 0, " +
                    "isGoogle "       + "BOOLEAN NOT NULL DEFAULT 0, " +
                    "isWifiSync "     + "BOOLEAN NOT NULL DEFAULT 0, " +
                    "isPremium "      + "BOOLEAN NOT NULL DEFAULT 0, " +
                    "isClassChecked " + "BOOLEAN NOT NULL DEFAULT 0, " +
                    "isCoupon "       + "BOOLEAN NOT NULL DEFAULT 0, " +
                    "CouponCode "     + "TEXT DEFAULT '',"+
                    "CouponTag "      + "INTEGER DEFAULT 1,"+
                    "CouponDate "     + "DATE DEFAULT '');";
            System.out.println(sqlCreateTb);
            sqliteDb.execSQL(sqlCreateTb);
        }
    }

    public boolean isClassChecked(){
        int result = 1;
        if(sqliteDb != null){
            Cursor cursor = null;
            String sqlQueryTb1 = "SELECT isClassChecked FROM User_Info;";
            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            cursor.moveToNext();
            result = cursor.getInt(0);
        }
        return result == 1;
    }

    public boolean isCoupon(){
        int result = 1;
        if(sqliteDb != null){
            Cursor cursor = null;
            String sqlQueryTb1 = "SELECT isCoupon FROM User_Info;";
            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            cursor.moveToNext();
            result = cursor.getInt(0);
        }
        return result == 1;
    }

    public int getClassId(){
        int ClassId = 1;
        if(sqliteDb != null){
            Cursor cursor = null;
            String sqlQueryTb1 = "SELECT ClassId FROM User_Info;";
            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            cursor.moveToNext();
            ClassId = cursor.getInt(0);
        }
        return ClassId;
    }

    public int getUserId(){
        int id =0;
        if(sqliteDb != null){
            Cursor cursor = null;
            String sqlQueryTb1 = "SELECT Id FROM User_Info;";
            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            cursor.moveToNext();
            id = cursor.getInt(0);
        }
        return id;
    }

    public int getFirst(){
        int isFirst=0;
        if(sqliteDb != null){
            String sqlQueryTb1 = "SELECT isFirst FROM User_Info;";
            Cursor cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            cursor.moveToNext();
            isFirst = cursor.getInt(0);
            System.out.println(sqlQueryTb1);
        }
        return isFirst;
    }

    public boolean getWifiSync(){
        int isWifiSync=0;
        if(sqliteDb != null){
            String sqlQueryTb1 = "SELECT isWifiSync FROM User_Info;";
            Cursor cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            cursor.moveToNext();
            isWifiSync = cursor.getInt(0);
            System.out.println(sqlQueryTb1);
        }
        return isWifiSync != 1;
    }

    public boolean getPremium(){
        int isPremium = 0;
        if(sqliteDb != null){
            String sqlQueryTb1 = "SELECT isPremium FROM User_Info;";
            Cursor cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            cursor.moveToNext();
            isPremium = cursor.getInt(0);
            System.out.println(sqlQueryTb1);
        }
        return isPremium != 1;
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

    public String get_Code(){
        Cursor cursor = null;
        String Code="";
        if(sqliteDb != null){
            String sqlQueryTb1 = "SELECT CouponCode FROM User_Info;";
            System.out.println(sqlQueryTb1);
            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            cursor.moveToNext();
            Code = cursor.getString(0);
        }
        return Code;
    }

    public String get_CouponDate(){
        Cursor cursor = null;
        String CouponDate="";
        if(sqliteDb != null){
            String sqlQueryTb1 = "SELECT CouponDate FROM User_Info;";
            System.out.println(sqlQueryTb1);
            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            cursor.moveToNext();
            CouponDate = cursor.getString(0);
        }
        return CouponDate;
    }

    public void add_values(int User_Id, String name, int isFirst, int isGoogle){
        if (sqliteDb != null) {
            String sqlInsert = "INSERT INTO User_Info " +
                    "(User_Id, Name, isFirst, isGoogle) VALUES (" +
                    User_Id + ", '"+ name +"', " + isFirst + ", " + isGoogle + ")" ;
            sqliteDb.execSQL(sqlInsert) ;
            System.out.println(sqlInsert) ;

        }
    }

    public void setCouponCode(String couponCode){
        if(sqliteDb != null){
            String sqlQueryTb1 = "UPDATE User_Info SET CouponCode = " + couponCode + ", CouponDate = date('now','+9 hours'), isCoupon = 1;";
            System.out.println(sqlQueryTb1);
            sqliteDb.execSQL(sqlQueryTb1);
        }
    }

    public void setClassId(int ClassId){
        if(sqliteDb != null){
            String sqlQueryTb1 = "UPDATE User_Info SET ClassId = " + ClassId + ", isClassChecked = 1;";
            System.out.println(sqlQueryTb1);
            sqliteDb.execSQL(sqlQueryTb1);
        }
    }

    public void addCoupon(int tag){
        Cursor cursor = null;
        String CouponDate = "";
        tag *= 30;
        if(sqliteDb != null){
            String sqlQueryTb1 = "SELECT CouponDate FROM User_Info;";
            System.out.println(sqlQueryTb1);
            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            cursor.moveToNext();
            CouponDate = cursor.getString(0);
            String sqlQueryTb2 = "UPDATE User_Info SET CouponDate = date('"+ CouponDate +"','+" + tag +" days');";
            System.out.println(sqlQueryTb2);
            sqliteDb.execSQL(sqlQueryTb2);
        }
    }

    public void update_isWifiSync(int isWifiSync){
        if(sqliteDb != null){
            String sqlQueryTb1 = "UPDATE User_Info SET isWifiSync = " + isWifiSync + ";";
            System.out.println(sqlQueryTb1);
            sqliteDb.execSQL(sqlQueryTb1);
        }
    }

    public void update_isPremium(int isPremium){
        if(sqliteDb != null){
            String sqlQueryTb1 = "UPDATE User_Info SET isPremium = " + isPremium + ";";
            System.out.println(sqlQueryTb1);
            sqliteDb.execSQL(sqlQueryTb1);
        }
    }

    public void update_isGoogle(boolean isGoogle, String name, int User_Id){
        if(sqliteDb != null){
            String sqlQueryTb1 = "UPDATE User_Info SET isGoogle = '" +  isGoogle + "', Name = '"+ name + "', User_Id = " + User_Id + ";";
            System.out.println(sqlQueryTb1);
            sqliteDb.execSQL(sqlQueryTb1);
        }
    }

    public void reset_app(){
        if (sqliteDb != null) {
            String sqlInsert = "UPDATE User_Info SET User_Id = 1, Name = '게스트', isFirst = 0, isGoogle = 0, isWifiSync = 0, isPremium = 0, isClassChecked = 0, ClassId = 11;";
            System.out.println(sqlInsert) ;
            sqliteDb.execSQL(sqlInsert) ;
        }
    }

    public void syncDate(ArrayList<UserData> list){
        ArrayList<UserData> user_list = list;
        if (sqliteDb != null) {
            String sqlInsert = "UPDATE User_Info SET" +
                    " User_Id = "+ user_list.get(0).User_Id +
                    ", Name = '" + user_list.get(0).Name + "'" +
                    ", ClassId = "+user_list.get(0).ClassId+
                    ", isFirst = " + user_list.get(0).isFirst +
                    ", isGoogle = " + user_list.get(0).isGoogle +
                    ", isWifiSync = "+ user_list.get(0).isWifiSync +
                    ", isPremium = " + user_list.get(0).isPremium +
                    ", isClassChecked = " + user_list.get(0).isClassChecked +
                    ", isCounpon = " + user_list.get(0).isCounpon +
                    ", CounponCode = '" + user_list.get(0).CounponCode + "'" +
                    ", CouponTag = " + user_list.get(0).CouponTag +
                    ", CouponDate = '"+user_list.get(0).CouponDate+"';";
            System.out.println(sqlInsert);
            sqliteDb.execSQL(sqlInsert);
        }
    }

    public boolean delete_User() {
        int isGoogle = 0;
        if(sqliteDb != null){
            Cursor cursor = null;
            String sqlQueryTb1 = "SELECT isGoogle FROM User_Info;";
            cursor = sqliteDb.rawQuery(sqlQueryTb1, null);
            cursor.moveToNext();
            isGoogle = cursor.getInt(0);
        }
        if(isGoogle==1){
            return true;
        }else{
            return false;
        }
    }
}
