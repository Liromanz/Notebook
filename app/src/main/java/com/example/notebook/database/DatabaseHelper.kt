package com.example.notebook.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper (context: Context) : SQLiteOpenHelper(context, "note.db", null, 1) {
    val TABLE_NAME = "note_table"
    val COL2 = "Name"
    val COL3 = "Time"
    val COL4 = "Date"
    val COL5 = "Description"
    val COL6 = "NoteCheck"

    private val DROP_USER_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_NAME (ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL2 TEXT NOT NULL, $COL3 TEXT NOT NULL, $COL4 TEXT NOT NULL, $COL5 TEXT DEFAULT '', $COL6 INTEGER NOT NULL)"
        db?.execSQL(createTable);
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DROP_USER_TABLE)
        onCreate(db)
    }

    fun addData(name: String?, time: String?, date: String?, description: String?, noteCheck : Boolean?):Boolean
    {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL2,name)
        cv.put(COL3,time)
        cv.put(COL4,date)
        cv.put(COL5,description)
        cv.put(COL6,if(noteCheck == false) 0 else 1)
        val result = db.insert(TABLE_NAME,null, cv)
        return result != -1L;
    }

    fun showData(date: String?): Cursor?{
        val db = this.writableDatabase
        return  db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL4 = '$date' ORDER BY $COL3 ASC",null)
    }

    fun getDates(): Cursor?{
        val db = this.writableDatabase
        return  db.rawQuery("SELECT $COL4 FROM $TABLE_NAME ORDER BY $COL4 ASC",null)
    }

    fun delete(id: Int?):Boolean
    {
        val db = this.writableDatabase
        val count = db.delete(TABLE_NAME,"ID = $id" ,null)
        return count != 0
    }

    fun update(name: String?, time: String?, date: String?, description: String?, noteCheck : Boolean?,id:Int?)
    {
        var cv = ContentValues()
        cv.put(COL2,name)
        cv.put(COL3,time)
        cv.put(COL4,date)
        cv.put(COL5,description)
        cv.put(COL6,if(noteCheck == false) 0 else 1)
        var db = this.writableDatabase
        db.update(TABLE_NAME, cv,"ID = $id",null)
    }

}