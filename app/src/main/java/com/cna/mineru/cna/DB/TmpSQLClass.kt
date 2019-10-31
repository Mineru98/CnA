package com.cna.mineru.cna.DB

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase

import java.io.File

/*
    오프라인일때 서버 통신을 해야할 경우
    데이터를 쌓아뒀다 한번에 일괄 처리하는 DB

*/

class TmpSQLClass(internal var context: Context?) {
    internal var tmpSQLClass: TmpSQLClass? = null
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
            val sqlCreateTb = "CREATE TABLE IF NOT EXISTS Tmp (" +
                    "Id " + "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "ExecuteNumber " + "INTEGER DEFAULT 1," +
                    "ExecuteId " + "INTEGER DEFAULT 0);"
            println(sqlCreateTb)
            sqliteDb!!.execSQL(sqlCreateTb)
        }
    }
    //ExecuteNumber가 1이면 Note 추가에 대한 임시 테이블
    //ExecuteId에는 실행해야할 열의 id값을 저장해 둔다.

    fun reset_app() {
        if (sqliteDb != null) {
            val sqlInsert = "DELETE FROM Tmp;"
            println(sqlInsert)
            sqliteDb!!.execSQL(sqlInsert)
        }
    }
}
