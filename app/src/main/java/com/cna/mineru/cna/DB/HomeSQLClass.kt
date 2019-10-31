package com.cna.mineru.cna.DB

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity

import com.cna.mineru.cna.DTO.ExamData
import com.cna.mineru.cna.DTO.HomeData

import java.io.File
import java.util.ArrayList

/*
    노트의 로딩, 삽입, 수정, 제거를 관리하기 위한 DB

*/

class HomeSQLClass(internal var context: Context?) : AppCompatActivity() {
    internal var sqliteDb: SQLiteDatabase? = null

    val count: Int
        get() {
            var result = 0
            if (sqliteDb != null) {
                val sqlQueryTb1 = "SELECT * FROM Note;"
                var cursor: Cursor? = null
                cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
                for (i in 0 until cursor!!.count) {
                    cursor.moveToNext()
                    result++
                }
            }
            return result
        }

    val id: Int
        get() {
            var id = 0
            if (sqliteDb != null) {
                val sqlQueryTb1 = "select id from Note order by id desc"
                val cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
                println(sqlQueryTb1)
                cursor.moveToNext()
                id = cursor.getInt(0)
            }
            return id
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
            val sqlCreateTb = "CREATE TABLE IF NOT EXISTS Note (" +
                    "Id " + "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "Title " + "TEXT," +
                    "Tag " + "INTEGER, " +
                    "SubTag " + "INTEGER, " +
                    "Note_Current " + "INTEGER DEFAULT 100," +
                    "Count " + "INTEGER DEFAULT 0," +
                    "ClassId " + "INTEGER DEFAULT 1);"

            println(sqlCreateTb)
            sqliteDb!!.execSQL(sqlCreateTb)
        }
    }

    fun load_values(): ArrayList<HomeData> {
        val list = ArrayList<HomeData>()

        if (sqliteDb != null) {
            val sqlQueryTb1 = "SELECT Id, Title FROM Note ORDER BY Id DESC"
            var cursor: Cursor? = null

            println(sqlQueryTb1)

            cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
            for (i in 0 until cursor!!.count) {
                cursor.moveToNext()
                val id = cursor.getInt(0)
                val title = cursor.getString(1)
                list.add(HomeData(id, title, 0, 0))
            }
        }
        return list
    }

    fun add_count(NoteId: Int) {
        if (sqliteDb != null) {
            val sqlQueryTb1 = "SELECT Count FROM Note Where Id = $NoteId;"
            var cursor: Cursor? = null
            println(sqlQueryTb1)
            cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
            cursor!!.moveToNext()
            var count = cursor.getInt(0)
            count++

            val sqlQueryTb2 = "UPDATE Note SET count = $count WHERE Id = $NoteId;"
            println(sqlQueryTb2)
            sqliteDb!!.execSQL(sqlQueryTb2)
        }
    }

    fun sub_count(NoteId: Int) {
        if (sqliteDb != null) {
            val sqlQueryTb1 = "SELECT Count FROM Note Where Id = $NoteId;"
            var cursor: Cursor? = null
            println(sqlQueryTb1)
            cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
            cursor!!.moveToNext()
            var count = cursor.getInt(0)
            count--

            val sqlQueryTb2 = "UPDATE Note SET count = $count WHERE Id = $NoteId;"
            println(sqlQueryTb2)
            sqliteDb!!.execSQL(sqlQueryTb2)
        }
    }

    fun update_item(id: Int, title: String, Tag: Int, Subtag: Int) {
        if (sqliteDb != null) {
            val sqlQueryTb1 = "UPDATE Note SET Title = '" + title + "', " +
                    "Tag = " + Tag + ", Subtag = " + Subtag + " WHERE Id = " + id + ";"

            println(sqlQueryTb1)

            sqliteDb!!.execSQL(sqlQueryTb1)
        }
    }

    fun delete_item(id: Int) {
        if (sqliteDb != null) {
            val sqlQueryTb1 = "DELETE FROM Note WHERE id =$id;"

            println(sqlQueryTb1)
            sqliteDb!!.execSQL(sqlQueryTb1)
        }
    }

    fun getList(exam_count: Int): ArrayList<ExamData> {
        val count = count //현재 Note Table에 있는 모든 Note ID의 갯수를 가져옴.
        val tmp_arr = IntArray(count)//Note ID 갯수만큼 tmp_arr 배열 할당.
        val list = ArrayList<ExamData>()//ExamData 객체 리스트 생성
        val rnd = IntArray(exam_count)//시험칠 문제의 수만큼 리스트 할당

        if (sqliteDb != null) {
            val sqlQueryTb1 = "SELECT id FROM Note;"
            var cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)

            for (i in 0 until cursor.count) {
                cursor.moveToNext()
                tmp_arr[i] = cursor.getInt(0)
            }

            val tmp = IntArray(exam_count)

            run {
                var k = 0
                while (k < tmp.size) {
                    tmp[k] = (Math.random() * tmp_arr.size).toInt()
                    for (j in 0 until k) {
                        if (tmp[k] == tmp[j]) {
                            k--
                            break
                        }
                    }
                    k++
                }
            }

            for (k in 0 until exam_count)
                rnd[k] = tmp_arr[tmp[k]]

            for (i in 0 until exam_count) {
                val sqlQueryTb2 = "SELECT Id, Title FROM Note WHERE Id = " + rnd[i] + ";"
                cursor = sqliteDb!!.rawQuery(sqlQueryTb2, null)
                cursor.moveToNext()
                list.add(ExamData(cursor.getInt(0), cursor.getString(1)))
            }
        }
        return list
    }

    fun select_item(id: Int): HomeData {
        var id = id
        val data: HomeData
        if (sqliteDb != null) {
            val sqlQueryTb1 = "SELECT Id, Title, Tag, Subtag FROM Note WHERE id = $id;"
            val cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
            println(sqlQueryTb1)
            cursor.moveToNext()
            id = cursor.getInt(0)
            val title = cursor.getString(1)
            val tag = cursor.getInt(2)
            val subtag = cursor.getInt(3)
            data = HomeData(id, title, tag, subtag)
        } else {
            data = HomeData(id, "Error", 0, 0)
        }
        return data
    }


    fun add_values(title: String, Tag: Int, ClassId: Int, Subtag: Int) {
        if (sqliteDb != null) {
            val p =
                sqliteDb!!.compileStatement("INSERT INTO Note (Title, Tag, ClassId, Subtag) VALUES (?,?,?,?);")
            p.bindString(1, title)
            p.bindLong(2, Tag.toLong())
            p.bindLong(3, ClassId.toLong())
            p.bindLong(4, Subtag.toLong())
            println(p)
            p.execute()
        }
    }

    fun reset_app() {
        if (sqliteDb != null) {
            val sqlInsert = "DELETE FROM Note;"
            println(sqlInsert)
            sqliteDb!!.execSQL(sqlInsert)
        }
    }
}
