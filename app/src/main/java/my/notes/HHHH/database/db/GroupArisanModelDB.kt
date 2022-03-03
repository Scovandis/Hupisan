package my.notes.HHHH.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import my.notes.HHHH.database.dao.ArisanGroupDataDao
import my.notes.HHHH.database.entity.GrupArisanModel

@Database(entities = [GrupArisanModel::class], version = 2, exportSchema = false)
abstract class GroupArisanModelDB : RoomDatabase(){
    abstract fun GroupArisanDao(): ArisanGroupDataDao

    companion object{
        private val migration_1_2: Migration = object : Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE userinfo ADD COLUMN phone ''")
            }
        }

        @Volatile
        private var INSTANCE: GroupArisanModelDB? = null
        //        Environment.getExternalStorageDirectory().absolutePath
        fun getDataBase(context: Context): GroupArisanModelDB{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GroupArisanModelDB::class.java,
                    "grup_arisan"
                )
                    .addMigrations(migration_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}