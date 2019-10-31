package com.cna.mineru.cna.DB

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity

import com.cna.mineru.cna.DTO.UserData

import java.io.File
import java.util.ArrayList

/*
    사용자 정보 관리 DB

*/

class UserSQLClass(internal var context: Context?) : AppCompatActivity() {

    internal var sqliteDb: SQLiteDatabase? = null

    val isClassChecked: Boolean
        get() {
            var result = 1
            if (sqliteDb != null) {
                var cursor: Cursor? = null
                val sqlQueryTb1 = "SELECT isClassChecked FROM User_Info;"
                cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
                cursor!!.moveToNext()
                result = cursor.getInt(0)
            }
            return result == 1
        }

    val isCoupon: Boolean
        get() {
            var result = 1
            if (sqliteDb != null) {
                var cursor: Cursor? = null
                val sqlQueryTb1 = "SELECT isCoupon FROM User_Info;"
                cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
                cursor!!.moveToNext()
                result = cursor.getInt(0)
            }
            return result == 1
        }

    var classId: Int
        get() {
            var ClassId = 1
            if (sqliteDb != null) {
                var cursor: Cursor? = null
                val sqlQueryTb1 = "SELECT ClassId FROM User_Info;"
                cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
                cursor!!.moveToNext()
                ClassId = cursor.getInt(0)
            }
            return ClassId
        }
        set(ClassId) {
            if (sqliteDb != null) {
                val sqlQueryTb1 = "UPDATE User_Info SET ClassId = $ClassId, isClassChecked = 1;"
                println(sqlQueryTb1)
                sqliteDb!!.execSQL(sqlQueryTb1)
            }
        }

    val userId: Int
        get() {
            var id = 0
            if (sqliteDb != null) {
                var cursor: Cursor? = null
                val sqlQueryTb1 = "SELECT User_Id FROM User_Info;"
                cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
                cursor!!.moveToNext()
                id = cursor.getInt(0)
            }
            return id
        }

    val first: Int
        get() {
            var isFirst = 0
            if (sqliteDb != null) {
                val sqlQueryTb1 = "SELECT isFirst FROM User_Info;"
                val cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
                cursor.moveToNext()
                isFirst = cursor.getInt(0)
                println(sqlQueryTb1)
            }
            return isFirst
        }

    val wifiSync: Boolean
        get() {
            var isWifiSync = 0
            if (sqliteDb != null) {
                val sqlQueryTb1 = "SELECT isWifiSync FROM User_Info;"
                val cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
                cursor.moveToNext()
                isWifiSync = cursor.getInt(0)
                println(sqlQueryTb1)
            }
            return isWifiSync != 1
        }

    val premium: Boolean
        get() {
            var isPremium = 0
            if (sqliteDb != null) {
                val sqlQueryTb1 = "SELECT isPremium FROM User_Info;"
                val cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
                cursor.moveToNext()
                isPremium = cursor.getInt(0)
                println(sqlQueryTb1)
            }
            return isPremium != 1
        }

    val _Name: String
        get() {
            var cursor: Cursor? = null
            var name = ""
            if (sqliteDb != null) {
                val sqlQueryTb1 = "SELECT Name FROM User_Info;"
                println(sqlQueryTb1)
                cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
                for (i in 0 until cursor!!.count) {
                    cursor.moveToNext()
                    name = cursor.getString(0)
                }
            }
            return name
        }

    val _Code: String
        get() {
            var cursor: Cursor? = null
            var Code = ""
            if (sqliteDb != null) {
                val sqlQueryTb1 = "SELECT CouponCode FROM User_Info;"
                println(sqlQueryTb1)
                cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
                cursor!!.moveToNext()
                Code = cursor.getString(0)
            }
            return Code
        }

    val _CouponDate: String
        get() {
            var cursor: Cursor? = null
            var CouponDate = ""
            if (sqliteDb != null) {
                val sqlQueryTb1 = "SELECT CouponDate FROM User_Info;"
                println(sqlQueryTb1)
                cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
                cursor!!.moveToNext()
                CouponDate = cursor.getString(0)
            }
            return CouponDate
        }

    init {
        sqliteDb = init_database(context)
        init_Tables()
    }

    private fun init_database(context: Context?): SQLiteDatabase? {
        var db: SQLiteDatabase? = null

        val file = File(context?.filesDir, "CnA.db")

        println("PATH : $file")
        try {
            db = SQLiteDatabase.openOrCreateDatabase(file, null)
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        if (db == null) {
            println("DB createion failed. " + file.absolutePath)
        }
        return db
    }

    private fun init_Tables() {
        if (sqliteDb != null) {
            val sqlCreateTb = "CREATE TABLE IF NOT EXISTS User_Info (" +
                    "User_Id " + "INTEGER NOT NULL PRIMARY KEY DEFAULT 1," +
                    "Name " + "TEXT DEFAULT '게스트'," +
                    "ClassId " + "INTEGER DEFAULT 10," +
                    "isFirst " + "BOOLEAN NOT NULL DEFAULT 0, " +
                    "isGoogle " + "BOOLEAN NOT NULL DEFAULT 0, " +
                    "isWifiSync " + "BOOLEAN NOT NULL DEFAULT 0, " +
                    "isPremium " + "BOOLEAN NOT NULL DEFAULT 0, " +
                    "isClassChecked " + "BOOLEAN NOT NULL DEFAULT 0, " +
                    "isCoupon " + "BOOLEAN NOT NULL DEFAULT 0, " +
                    "CouponCode " + "TEXT DEFAULT ''," +
                    "CouponTag " + "INTEGER DEFAULT 1," +
                    "CouponDate " + "DATE DEFAULT '');"
            println(sqlCreateTb)
            sqliteDb!!.execSQL(sqlCreateTb)
        }
    }

    fun add_values(User_Id: Int, name: String, isFirst: Int, isGoogle: Int) {
        if (sqliteDb != null) {
            val sqlInsert = "INSERT INTO User_Info " +
                    "(User_Id, Name, isFirst, isGoogle) VALUES (" +
                    User_Id + ", '" + name + "', " + isFirst + ", " + isGoogle + ")"
            sqliteDb!!.execSQL(sqlInsert)
            println(sqlInsert)

        }
    }

    fun setCouponCode(couponCode: String) {
        if (sqliteDb != null) {
            val sqlQueryTb1 =
                "UPDATE User_Info SET CouponCode = $couponCode, CouponDate = date('now','+9 hours'), isCoupon = 1;"
            println(sqlQueryTb1)
            sqliteDb!!.execSQL(sqlQueryTb1)
        }
    }

    fun addCoupon(tag: Int) {
        var tag = tag
        var cursor: Cursor? = null
        var CouponDate = ""
        tag *= 30
        if (sqliteDb != null) {
            val sqlQueryTb1 = "SELECT CouponDate FROM User_Info;"
            println(sqlQueryTb1)
            cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
            cursor!!.moveToNext()
            CouponDate = cursor.getString(0)
            val sqlQueryTb2 = "UPDATE User_Info SET CouponDate = date('$CouponDate','+$tag days');"
            println(sqlQueryTb2)
            sqliteDb!!.execSQL(sqlQueryTb2)
        }
    }

    fun update_name(Name: String) {
        if (sqliteDb != null) {
            val sqlQueryTb1 = "UPDATE User_Info SET Name = '$Name';"
            println(sqlQueryTb1)
            sqliteDb!!.execSQL(sqlQueryTb1)
        }
    }

    fun update_isWifiSync(isWifiSync: Int) {
        if (sqliteDb != null) {
            val sqlQueryTb1 = "UPDATE User_Info SET isWifiSync = $isWifiSync;"
            println(sqlQueryTb1)
            sqliteDb!!.execSQL(sqlQueryTb1)
        }
    }

    fun update_isPremium(isPremium: Int) {
        if (sqliteDb != null) {
            val sqlQueryTb1 = "UPDATE User_Info SET isPremium = $isPremium;"
            println(sqlQueryTb1)
            sqliteDb!!.execSQL(sqlQueryTb1)
        }
    }

    fun update_isGoogle(isGoogle: Boolean, name: String, User_Id: Int) {
        if (sqliteDb != null) {
            val sqlQueryTb1 =
                "UPDATE User_Info SET isGoogle = '$isGoogle', Name = '$name', User_Id = $User_Id;"
            println(sqlQueryTb1)
            sqliteDb!!.execSQL(sqlQueryTb1)
        }
    }

    fun reset_app() {
        if (sqliteDb != null) {
            val sqlInsert =
                "UPDATE User_Info SET User_Id = 1, Name = '게스트', isFirst = 0, isGoogle = 0, isWifiSync = 0, isPremium = 0, isClassChecked = 0, ClassId = 10;"
            println(sqlInsert)
            sqliteDb!!.execSQL(sqlInsert)
        }
    }

    fun syncDate(list: ArrayList<UserData>) {
        if (sqliteDb != null) {
            val sqlInsert = "UPDATE User_Info SET" +
                    " User_Id = " + list[0].User_Id +
                    ", Name = '" + list[0].Name + "'" +
                    ", ClassId = " + list[0].ClassId +
                    ", isFirst = " + list[0].isFirst +
                    ", isGoogle = " + list[0].isGoogle +
                    ", isWifiSync = " + list[0].isWifiSync +
                    ", isPremium = " + list[0].isPremium +
                    ", isClassChecked = " + list[0].isClassChecked +
                    ", isCounpon = " + list[0].isCounpon +
                    ", CounponCode = '" + list[0].CounponCode + "'" +
                    ", CouponTag = " + list[0].CouponTag +
                    ", CouponDate = '" + list[0].CouponDate + "';"
            println(sqlInsert)
            sqliteDb!!.execSQL(sqlInsert)
        }
    }

    fun delete_User(): Boolean {
        var isGoogle = 0
        if (sqliteDb != null) {
            var cursor: Cursor? = null
            val sqlQueryTb1 = "SELECT isGoogle FROM User_Info;"
            cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
            cursor!!.moveToNext()
            isGoogle = cursor.getInt(0)
        }
        return if (isGoogle == 1) {
            true
        } else {
            false
        }
    }
}
