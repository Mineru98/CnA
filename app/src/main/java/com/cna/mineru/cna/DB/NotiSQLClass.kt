package com.cna.mineru.cna.DB

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import androidx.appcompat.app.AppCompatActivity

import com.cna.mineru.cna.DTO.NotiData

import java.io.File
import java.util.ArrayList

/*

 */

class NotiSQLClass(internal var context: Context?) : AppCompatActivity() {

    internal var sqliteDb: SQLiteDatabase? = null

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

    // Notificaiton 테이블 생성 메소드
    // Methods to create the Exam table
    private fun init_Tables() {
        if (sqliteDb != null) {
            val sqlCreateTb = "CREATE TABLE IF NOT EXISTS Notificaiton (" +
                    "Id " + "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "NotiTag " + "INTEGER DEFAULT 1," +
                    "Title " + "TEXT DEFAULT ''," +
                    "SubTitle " + "TEXT DEFAULT '');"
            println(sqlCreateTb)
            sqliteDb!!.execSQL(sqlCreateTb)
        }
    }

    fun load_value(): ArrayList<NotiData> {
        val list = ArrayList<NotiData>()
        if (sqliteDb != null) {
            val sqlQueryTb1 =
                "SELECT Id, NotiTag, Title, SubTitle FROM Notificaiton ORDER BY Id DESC"
            var cursor: Cursor? = null
            println(sqlQueryTb1)
            cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
            for (i in 0 until cursor!!.count) {
                cursor.moveToNext()
                val id = cursor.getInt(0)
                val tag = cursor.getInt(1)
                val title = cursor.getString(2)
                val subtitle = cursor.getString(3)
                list.add(NotiData(id, tag, title, subtitle))
            }
        }
        return list
    }

    fun add_notification(Id: Int, NotiTag: Int, Title: String, SubTitle: String) {
        if (sqliteDb != null) {
            val p =
                sqliteDb!!.compileStatement("INSERT INTO Note (Id, NotiTag, Title, SubTitle) VALUES (?,?,?,?);")
            p.bindLong(1, Id.toLong())
            p.bindLong(2, NotiTag.toLong())
            p.bindString(3, Title)
            p.bindString(4, SubTitle)
            println(p)
            p.execute()
        }
    }
}
