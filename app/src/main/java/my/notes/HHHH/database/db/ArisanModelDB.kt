package my.notes.HHHH.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import my.notes.HHHH.database.dao.ArisanDataDao
import my.notes.HHHH.database.entity.ArisanModel

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
        private var INSTANCE: my.notes.HHHH.database.db.ArisanModelDB? = null
        fun getDataBase(context: Context): my.notes.HHHH.database.db.ArisanModelDB {
            return my.notes.HHHH.database.db.ArisanModelDB.Companion.INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    my.notes.HHHH.database.db.ArisanModelDB::class.java,
                    "arisan"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                my.notes.HHHH.database.db.ArisanModelDB.Companion.INSTANCE = instance
                instance
            }
        }
    }

}