package com.cna.mineru.cna.DB

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity

import com.cna.mineru.cna.DTO.GraphData

import java.io.File
import java.util.ArrayList

/*
    노트 및 시험 결과 그래프 관리 DB
    This DataBase is used to managing notes and test results graph
*/

class GraphSQLClass(internal var context: Context?) : AppCompatActivity() {

    internal var sqliteDb: SQLiteDatabase? = null

    init {
        sqliteDb = init_database(context)
        init_Tables()
        load_values()
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

    // Graph 테이블 생성 메소드
    // Methods to create the Graph table
    private fun init_Tables() {
        if (sqliteDb != null) {
            val sqlCreateTb = "CREATE TABLE IF NOT EXISTS Graph (" +
                    "Id " + "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "Note_Id " + "INTEFER," +
                    "Note_Type " + "INTEGER" + ")"

            println(sqlCreateTb)
            sqliteDb!!.execSQL(sqlCreateTb)
        }
    }

    // 등록된 Note에 대한 정보를 도식화하기 위한
    // Data를 Loading 하는 메소드
    //

    fun load_values(): ArrayList<GraphData> {
        val list = ArrayList<GraphData>()

        if (sqliteDb != null) {
            val sqlQueryTb1 = "SELECT Id, Note_Type FROM Graph ORDER BY Id ASC"
            var cursor: Cursor? = null

            cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
            for (i in 0 until cursor!!.count) {
                cursor.moveToNext()
                val id = cursor.getInt(0)
                val note_type = cursor.getInt(1)
                list.add(GraphData(id, note_type))
            }
        }
        return list
    }

    // Note와 같은 내용의 Graph 데이터를
    // Indexing을 하기 위한 메소드
    //
    fun add_values(Note_Id: Int, Note_Type: Int) {
        if (sqliteDb != null) {

            val sqlInsert = "INSERT INTO Graph " +
                    "(Note_Id, Note_Type) VALUES (" +
                    Note_Id + ", " +
                    Note_Type + ");"

            println(sqlInsert)

            sqliteDb!!.execSQL(sqlInsert)
        }
    }

    // Note의 내용이 변경 되었을 때,
    // 그래프의 성질을 변경하기 위한 메소드
    //
    fun update_value(Note_Id: Int, Note_Type: Int) {
        if (sqliteDb != null) {

            val sqlInsert = ("UPDATE Graph SET Note_Type = " + Note_Type
                    + " WHERE Note_Id = " + Note_Id + ";")
            println(sqlInsert)
            sqliteDb!!.execSQL(sqlInsert)
        }
    }

    // Indexing 된 Note의 정보가 삭제 되었을 때,
    // Graph 테이블의 내용을 동기화 하기 위한 메소드
    //

    fun delete_value(Note_Id: Int) {
        if (sqliteDb != null) {
            val sqlInsert = "DELETE FROM Graph WHERE Note_Id = $Note_Id;"
            println(sqlInsert)
            sqliteDb!!.execSQL(sqlInsert)
        }
    }

    fun reset_app() {
        if (sqliteDb != null) {
            val sqlInsert = "DELETE FROM Graph;"
            println(sqlInsert)
            sqliteDb!!.execSQL(sqlInsert)
        }
    }
}
