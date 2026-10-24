package my.notes.HHHH.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import my.notes.HHHH.database.dao.DataDao
import my.notes.HHHH.database.entity.DataModel

@Database(entities = [DataModel::class], version = 2, exportSchema = false)
abstract class ModelDB : RoomDatabase(){
    abstract fun dataDAO(): DataDao

    companion object{

        private val migration_1_2: Migration = object : Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE userinfo ADD COLUMN phone ''")
            }
        }

        @Volatile
        private var INSTANCE: ModelDB? = null

        fun getDataBase(context: Context): ModelDB{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ModelDB::class.java,
                    "HHHH"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }
}