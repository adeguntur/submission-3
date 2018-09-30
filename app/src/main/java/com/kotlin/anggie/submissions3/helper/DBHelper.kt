package com.kotlin.anggie.submissions3.helper

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.google.gson.Gson
import com.kotlin.anggie.submissions3.model.Event
import org.jetbrains.anko.db.*
import java.util.logging.Logger

class DBHelper(ctx: Context): ManagedSQLiteOpenHelper(
        ctx, "Football.db", null, 1) {
    companion object {
        const val TABLE_FAVORITE: String = "TABLE_FAVORITE"
        const val EVENT_ID: String = "EVENT_ID"
        const val EVENT_CONTENT: String = "EVENT_CONTENT"

        private var instance: DBHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): DBHelper {
            if (instance == null) {
                instance = DBHelper(ctx.applicationContext)
            }
            return instance as DBHelper
        }
    }

    private val gson = Gson()

    override fun onCreate(db: SQLiteDatabase) {
        // Here you create tables
        db.createTable(
                TABLE_FAVORITE, true,
                EVENT_ID to TEXT + PRIMARY_KEY,
                EVENT_CONTENT to TEXT
        )
    }

    override fun onUpgrade(db: SQLiteDatabase,
                           oldVersion: Int,
                           newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable(TABLE_FAVORITE, true)
    }

    fun getAllFavMatches(): List<Event> {
        val listEvent = mutableListOf<Event>()
        instance?.use {
            val query = select(TABLE_FAVORITE, DBHelper.EVENT_CONTENT)
            val stringEvent = query.exec {
                parseList(StringParser)
            }
            stringEvent.forEach {
                Log.i("it", it)
                val event = gson.fromJson<Event>(it, Event::class.java)
                listEvent.add(event)
            }
        }
        return listEvent
    }

    fun insertFavMatch(event: Event,
                       listener: DatabaseListener?) {
        try {
            val content = gson.toJson(event)
            instance?.use {
                insert(TABLE_FAVORITE, EVENT_ID to event.idEvent, EVENT_CONTENT to content)
            }
            listener?.onSuccess()
        } catch (e: Throwable) {
            System.out.print(e.printStackTrace())
            listener?.onFailed(e.localizedMessage)
        }
    }

    fun removeFavMatch(eventId: String, listener: DatabaseListener?) {
        try {
            instance?.use {
                val items = delete(TABLE_FAVORITE,
                        "$EVENT_ID = {eventId}", "eventId" to eventId)
                if (items > 0) {
                    listener?.onSuccess()
                }
            }
        } catch (e: SQLiteConstraintException) {
            listener?.onFailed(e.localizedMessage)
        }
    }

    fun isMatchFavorite(eventId: String) : Boolean {
        var result = false
        instance?.use {
            val query = select(TABLE_FAVORITE, EVENT_CONTENT)
                    .whereArgs("$EVENT_ID = {eventId}",
                            "eventId" to eventId)
            val event = query.exec {
                count
            }
            result = event > 0
        }
        return result
    }

}

interface DatabaseListener {
    fun onSuccess()
    fun onFailed(message: String)
}