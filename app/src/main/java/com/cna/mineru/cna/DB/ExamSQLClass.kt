package com.cna.mineru.cna.DB

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import com.cna.mineru.cna.DTO.ExamData
import com.cna.mineru.cna.Fragment.ExamFragmentChild.DoExamFragment

import java.io.File
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date

/*
    시험 결과 관리하는 DB
    This DataBase is used to manage exam results

*/

class ExamSQLClass(internal var context: Context?) : AppCompatActivity() {

    internal lateinit var homeSQLClass: HomeSQLClass
    internal var sqliteDb: SQLiteDatabase? = null

    val _Exam_RoomId: Int
        @SuppressLint("Recycle")
        get() {
            var RoomId = 0
            if (sqliteDb != null) {
                val sqlQueryTb1 = "select ExamRoomId from Exam ORDER BY Id DESC limit 1;"
                var cursor: Cursor? = null
                cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
                cursor!!.moveToNext()
                RoomId = cursor.getInt(0)
            }
            return RoomId
        }

    val _last_exam: Int
        get() {
            var RoomId = 0
            if (sqliteDb != null) {
                val sqlQueryTb1 = "SELECT Id FROM Exam WHERE TAG = 3 ORDER BY Id DESC limit 1"
                var cursor: Cursor? = null
                cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
                cursor!!.moveToNext()
                RoomId = cursor.getInt(0)
            }
            return RoomId
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

    // Exam 테이블 생성 메소드
    // Methods to create the Exam table
    private fun init_Tables() {
        if (sqliteDb != null) {
            val sqlCreateTb = "CREATE TABLE IF NOT EXISTS Exam (" +
                    "Id " + "INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "NoteId " + "INTEGER DEFAULT 0," +
                    "ExamRoomId " + "INTEGER DEFAULT 0," +
                    "Title " + "TEXT," +
                    "Tag " + "INTEGER DEFAULT 0," +
                    "EachTime " + "INTEGER DEFAULT 0," +
                    "AllTime " + "INTEGER DEFAULT 0," +
                    "TimeToSolve " + "INTEGER," +
                    "isSolved " + "BOOLEAN DEFAULT 0," +
                    "ExamTitle " + "TEXT DEFAULT ''," +
                    "CreateDate " + "DATE);"
            println(sqlCreateTb)
            sqliteDb!!.execSQL(sqlCreateTb)
        }
    }

    // Exam Table에 등록된 시험 결과를 ListView에
    // 보여주기 위한 Data Loading 메소드
    // This is a data loading method for
    // displaying the Exam results registered in the Exam table in the ListView.
    @SuppressLint("Recycle")
    fun load_values(): ArrayList<ExamData> {
        val list = ArrayList<ExamData>()

        if (sqliteDb != null) {
            val sqlQueryTb1 =
                "SELECT Id, ExamTitle, ExamRoomId FROM Exam WHERE TAG = 3 ORDER BY Id DESC"
            var cursor: Cursor? = null

            cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
            for (i in 0 until cursor!!.count) {
                cursor.moveToNext()
                val RoomId = cursor.getInt(0)
                val examtitle = cursor.getString(1)
                list.add(ExamData(examtitle, RoomId))
            }
        }
        return list
    }

    fun getNoteCount(NoteId: Int): Int {
        var result = 0
        if (sqliteDb != null) {
            val sqlQueryTb1 = "SELECT Id FROM Exam WHERE NoteId = $NoteId;"
            var cursor: Cursor? = null
            cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
            for (i in 0 until cursor!!.count) {
                cursor.moveToNext()
                result++
            }
        }
        return result
    }

    fun getEachExam(NoteId: Int): ArrayList<ExamData> {
        val list = ArrayList<ExamData>()
        if (sqliteDb != null) {
            val sqlQueryTb1 = "SELECT TimeToSolve, isSolved FROM Exam WHERE NoteId = $NoteId;"
            var cursor: Cursor? = null
            cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
            for (i in 0 until cursor!!.count) {
                cursor.moveToNext()
                val TimeToSolve = cursor.getLong(0)
                val isSolved = cursor.getInt(1)
                list.add(ExamData(TimeToSolve, isSolved))
            }
        }
        return list
    }

    // 특정 시험에 대한 데이터 지표를 볼 때
    // NoteId를 통해 Data Loading을 하는 메소드
    // Methods for Data Loading via NoteId when viewing data indicators for a particular Exam
    @SuppressLint("Recycle")
    fun get_point_values(RoomId: Int): ArrayList<ExamData> {
        val list = ArrayList<ExamData>()
        if (sqliteDb != null) {
            val sqlQueryTb1 =
                "select Id, NoteId, Title, TimeToSolve, isSolved from Exam where ExamRoomId = $RoomId;"
            var cursor: Cursor? = null
            cursor = sqliteDb!!.rawQuery(sqlQueryTb1, null)
            for (i in 0 until cursor!!.count) {
                cursor.moveToNext()
                val Id = cursor.getInt(0)
                val NoteId = cursor.getInt(1)
                val Title = cursor.getString(2)
                val TTS = cursor.getLong(3)
                val isSolved = cursor.getInt(4)
                list.add(ExamData(Id, NoteId, Title, TTS, isSolved))
            }
        }
        return list
    }

    // 랜덤으로 문제를 섞어 시험지를 만드는 메소드
    // Methods for creating exam papers by randomly mixing problems
    fun make_Exam(exam_count: Int): ArrayList<ExamData> {
        var count = 0
        var list = ArrayList<ExamData>()
        if (sqliteDb != null) {
            homeSQLClass = HomeSQLClass(context)
            count = homeSQLClass.count
            if (count < 4) {
                val dialog = context?.let { AlertDialog.Builder(it) }
                dialog?.setTitle("알림")
                dialog?.setMessage(
                    "현재 등록 된 노트의 수가 부족합니다.\n" +
                            "4개 이상의 노트가 등록되어야\n" +
                            "시험을 볼 수 있습니다."
                )
                dialog?.setPositiveButton(
                    "확인",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                dialog?.show()
                return list
            } else if (exam_count < 4) {
                val dialog = context?.let { AlertDialog.Builder(it) }
                dialog?.setTitle("알림")
                dialog?.setMessage(
                    "시험은 문제 4개부터 가능합니다.\n" +
                            "4개 이상의 노트가 등록되어야\n" +
                            "시험을 볼 수 있습니다."
                )
                dialog?.setPositiveButton(
                    "확인",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                dialog?.show()
                return list
            } else if (count < exam_count) {
                val dialog = context?.let { AlertDialog.Builder(it) }
                dialog?.setTitle("알림")
                dialog?.setMessage(
                    "현재 등록 된 노트의 수보다 많습니다.\n"
                )
                dialog?.setPositiveButton(
                    "확인",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                dialog?.show()
                return list
            } else {
                list = homeSQLClass.getList(exam_count)
                for (i in 0 until exam_count) {
                    var sqlInsert = ""
                    if (i == 0) {
                        sqlInsert = "INSERT INTO Exam " +
                                "(NoteId, Tag, Title) VALUES (" +
                                list[i].NoteId + "," +
                                "1 ," +
                                "'" + list[i].Title + "'" + ")"
                    } else {
                        sqlInsert = "INSERT INTO Exam " +
                                "(NoteId, Tag, Title) VALUES (" +
                                list[i].NoteId + "," +
                                "0 ," +
                                "'" + list[i].Title + "'" + ")"
                    }
                    println(sqlInsert)
                    sqliteDb!!.execSQL(sqlInsert)
                }
                exam_first_update()
            }
        }
        return list
    }

    // Random Exam Data를 생성한 다음,
    // Tag를 통해 테이블을 안정적으로 만들기 위한 메소드
    // After creating the Random Exam Data,
    // the method for making the table stable through Tag
    @SuppressLint("Recycle")
    private fun exam_first_update() {
        if (sqliteDb != null) {
            val sql_select1 = "SELECT Id FROM Exam WHERE Tag = 1;"
            val sql_select2 = "SELECT Id FROM Exam WHERE Tag = 3;"
            var cursor: Cursor? = null
            cursor = sqliteDb!!.rawQuery(sql_select1, null)
            cursor!!.moveToNext()
            val id = cursor.getInt(0)
            var count = 0
            cursor = sqliteDb!!.rawQuery(sql_select2, null)
            for (i in 0 until cursor!!.count) {
                cursor.moveToNext()
                count++
            }
            count++

            val sql_update1 = "UPDATE Exam SET ExamRoomId = $id,Tag = 2  WHERE Tag = 0;"
            @SuppressLint("SimpleDateFormat")
            val sql_update2 = "UPDATE Exam SET ExamRoomId = " + id + ",Tag = 3, ExamTitle = '" +
                    SimpleDateFormat("yyyy년 MM월 dd일").format(Date(System.currentTimeMillis())) +
                    "_" + count + "회차' WHERE Tag = 1;"
            sqliteDb!!.execSQL(sql_update1)
            sqliteDb!!.execSQL(sql_update2)
        }
    }

    fun update_result(isSolved: IntArray, TTS: LongArray, RoomId: Int, NoteId: IntArray) {
        if (sqliteDb != null) {
            for (i in isSolved.indices) {
                val sqlQueryTb1 =
                    "UPDATE Exam SET TimeToSolve = " + TTS[i] + ",isSolved = " + isSolved[i] + ", CreateDate = (SELECT DATE('now','+9 hours')) WHERE ExamRoomId = " + RoomId + " AND NoteId = " + NoteId[i] + ";"
                println(sqlQueryTb1)
                sqliteDb!!.execSQL(sqlQueryTb1)
            }
        }
    }

    fun delete_exam(id: Int) {
        if (sqliteDb != null) {
            val sqlQueryTb1 = "DELETE FROM Exam WHERE id =$id;"
            println(sqlQueryTb1)
            sqliteDb!!.execSQL(sqlQueryTb1)
        }
    }

    fun reset_app() {
        if (sqliteDb != null) {
            val sqlInsert = "DELETE FROM Exam;"
            println(sqlInsert)
            sqliteDb!!.execSQL(sqlInsert)
        }
    }
}
