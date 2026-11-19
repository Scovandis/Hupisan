package com.hupisan.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hupisan.database.dao.PaymentDao
import com.hupisan.database.entity.PaymentModel

@Database(entities = [PaymentModel::class], version = 2, exportSchema = false)
abstract class PaymentDatabase : RoomDatabase() {
    abstract fun paymentDao(): PaymentDao

    companion object {
        private val migration_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE userinfo ADD COLUMN phone ''")
            }
        }

        @Volatile
        private var INSTANCE: PaymentDatabase? = null

        fun getDataBase(context: Context): PaymentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PaymentDatabase::class.java,
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
