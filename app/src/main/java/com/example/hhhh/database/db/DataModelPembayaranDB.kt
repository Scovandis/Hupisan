package com.example.hhhh.database.db

import android.content.Context
import android.os.Environment
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.hhhh.database.dao.DataPembayaranDao
import com.example.hhhh.database.entity.DataPembayaranModel


@Database(entities = [DataPembayaranModel::class], version = 2, exportSchema = false)
abstract class DataModelPembayaranDB: RoomDatabase(){
    abstract fun dataPembayaranDao(): DataPembayaranDao

    companion object{
        private val migration_1_2: Migration = object : Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE userinfo ADD COLUMN phone ''")
            }
        }

        @Volatile
        private var INSTANCE: DataModelPembayaranDB? = null
//        Environment.getExternalStorageDirectory().absolutePath
        fun getDataBase(context: Context): DataModelPembayaranDB{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DataModelPembayaranDB::class.java,
                     "pembayaran"
                )
                    .addMigrations(migration_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}