package com.hupisan.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hupisan.database.dao.ArisanDataDao
import com.hupisan.database.entity.ArisanModel

@Database(entities = [ArisanModel::class], version = 2, exportSchema = false)
abstract class ArisanModelDB :RoomDatabase(){
    abstract fun arisanDao(): ArisanDataDao

    companion object{
        private val migration_1_2: Migration = object : Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE userinfo ADD COLUMN phone ''")
            }
        }

        @Volatile
        private var INSTANCE: com.hupisan.database.db.ArisanModelDB? = null
        fun getDataBase(context: Context): com.hupisan.database.db.ArisanModelDB {
            return com.hupisan.database.db.ArisanModelDB.Companion.INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    com.hupisan.database.db.ArisanModelDB::class.java,
                    "arisan"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                com.hupisan.database.db.ArisanModelDB.Companion.INSTANCE = instance
                instance
            }
        }
    }

}