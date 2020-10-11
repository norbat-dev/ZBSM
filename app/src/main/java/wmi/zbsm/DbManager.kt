package wmi.zbsm

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.security.AccessControlContext
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import java.nio.ByteOrder
import android.content.ContentValues

class DbManager {
    val dbName = "ZBSMNotes"
    val dbTable = "Notes"
    val colID = "ID"
    val colTitle = "Title"
    val colDes = "Description"
    val saltCol = "salt"
    val ivCol = "iv"

    val dbVersion = 2

    val sqlCreateTable = "CREATE TABLE IF NOT EXISTS "+dbTable+"("+colID+" INTEGER PRIMARY KEY, "+colTitle+" TEXT, "+colDes+" TEXT, "+saltCol+" TEXT, "+ivCol+" TEXT);"

    var sqlDB:SQLiteDatabase?=null

    constructor(context: Context){
        var db = DatabaseHelperNotes(context)
        sqlDB = db.writableDatabase
    }

    inner class DatabaseHelperNotes:SQLiteOpenHelper{
        var context: Context?=null
        constructor(context: Context):super(context, dbName, null, dbVersion){
            this.context = context
        }

        override fun onCreate(p0: SQLiteDatabase?) {

            p0!!.execSQL(sqlCreateTable)
            Toast.makeText(this.context, "Baza danych stworzona", Toast.LENGTH_SHORT).show()
        }
        override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
            p0!!.execSQL("Drop table if Exists "+dbTable)
        }


    }

    fun insert(values:ContentValues):Long{
        val ID = sqlDB!!.insert(dbTable,"",values)
        return ID
    }

    fun Query(projection:Array<String>, selection:String, selectionArgs:Array<String>, sorOrder: String):Cursor{
        val qb = SQLiteQueryBuilder();
        qb.tables = dbTable
        val cursor = qb.query(sqlDB, projection, selection, selectionArgs, null, null, sorOrder)
        return cursor
    }

    fun delete(selection:String, selectionArgs: Array<String>):Int{
        val count = sqlDB!!.delete(dbTable,selection, selectionArgs)
        return count
    }

    fun update(values: ContentValues, selection: String, selectionArgs: Array<String>):Int{
        val count = sqlDB!!.update(dbTable, values, selection, selectionArgs)
        return count
    }
}