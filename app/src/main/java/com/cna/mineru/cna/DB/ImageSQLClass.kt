package com.cna.mineru.cna.DB

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity

import java.io.File
import java.util.ArrayList

/*
    등록한 Note의 Image의 Index 관리하는 DB

*/

class ImageSQLClass(internal var context: Context?) : AppCompatActivity() {

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

    private fun init_Tables() {
        if (sqliteDb != null) {
            val sqlCreateTb = "CREATE TABLE IF NOT EXISTS Image (" +
                    "Id " + "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "NoteId " + "INTEGER DEFAULT 0," +
                    "ImageId " + "INTEGER DEFAULT 0," +
                    "isSolve " + "BOOLEAN DEFAULT 0," +
                    "Image " + "BLOB DEFAULT '');"
            println(sqlCreateTb)
            sqliteDb!!.execSQL(sqlCreateTb)
        }
    }

    fun getImg(NoteId: Int, isSolve: Int): ArrayList<ByteArray> {
        val image = ArrayList<ByteArray>()
        if (sqliteDb != null) {
            val sqlQueryTb1 =
                "SELECT Image FROM Image WHERE NoteId = $NoteId And isSolve = $isSolve;"
            val cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
            while (cursor.moveToNext()) {
                image.add(cursor.getBlob(0))
            }
        }
        return image
    }

    fun getCount(NoteId: Int, isSolve: Int): Int {
        var count = 0
        if (sqliteDb != null) {
            val sqlQueryTb1 =
                "SELECT Image FROM Image WHERE NoteId = $NoteId And isSolve = $isSolve;"
            val cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
            while (cursor.moveToNext()) {
                count++
            }
        }
        return count
    }

    fun add_value(NoteId: Int, ImageId: Long, isSolve: Int, image: ByteArray?) {
        if (sqliteDb != null) {
            if (image != null) {
                if (image.size > 1) {
                    val p =
                        sqliteDb!!.compileStatement("INSERT INTO Image (NoteId, ImageId, isSolve, Image) VALUES (?,?,?,?);")
                    p.bindLong(1, NoteId.toLong())
                    p.bindLong(2, ImageId)
                    p.bindLong(3, isSolve.toLong())
                    p.bindBlob(4, image)
                    println(p)
                    p.execute()
                }
            }
        }
    }

    fun add_value2() {
        if (sqliteDb != null) {
            val sqlQueryTb1 = "SELECT Id FROM Image WHERE ImageId = 0;"
            val cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
            cursor.moveToNext()
            val id = cursor.getInt(0)
            println(sqlQueryTb1)

            val sqlQueryTb2 = "UPDATE Image SET ImageId = $id WHERE ImageId = 0;"
            sqliteDb!!.execSQL(sqlQueryTb2)
            println(sqlQueryTb2)
        }
    }

    fun delete_item(Note_Id: Int) {
        if (sqliteDb != null) {
            val sqlInsert = "DELETE FROM Image WHERE NoteId = $Note_Id;"
            println(sqlInsert)
            sqliteDb!!.execSQL(sqlInsert)
        }
    }

    fun reset_app() {
        if (sqliteDb != null) {
            val sqlInsert = "DELETE FROM Image;"
            println(sqlInsert)
            sqliteDb!!.execSQL(sqlInsert)
        }
    }
}

