package my.notes.HHHH.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import my.notes.HHHH.database.dao.DataPembayaranDao
import my.notes.HHHH.database.entity.DataPembayaranModel


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
        fun getDataBase(context: Context): DataModelPembayaranDB{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DataModelPembayaranDB::class.java,
                     "pembayaran"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}