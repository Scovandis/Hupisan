package com.example.hhhh.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.hhhh.database.dao.ArisanDataDao
import com.example.hhhh.database.dao.ArisanGroupDataDao
import com.example.hhhh.database.entity.ArisanModel
import com.example.hhhh.database.entity.GrupArisanModel

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
        private var INSTANCE: ArisanModelDB? = null
        fun getDataBase(context: Context): ArisanModelDB{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArisanModelDB::class.java,
                    "arisan"
                )
                    .addMigrations(migration_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}