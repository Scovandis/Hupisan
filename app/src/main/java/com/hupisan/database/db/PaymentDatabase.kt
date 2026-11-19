package com.hupisan.database.db

import android.content.Context
import com.hupisan.database.dao.PaymentDao
import com.hupisan.database.dao.RealmPaymentDao
import io.github.xilinjia.krdb.Realm

open class PaymentDatabase(private val realm: Realm = RealmDatabase.getRealm()) {
    fun paymentDao(): PaymentDao = RealmPaymentDao(realm)

    companion object {
        @Volatile
        private var INSTANCE: PaymentDatabase? = null

        fun getDataBase(context: Context? = null): PaymentDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PaymentDatabase(RealmDatabase.getRealm()).also { INSTANCE = it }
            }
        }
    }
}
