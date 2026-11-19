package com.hupisan.database.db

import android.content.Context
import com.hupisan.database.dao.DataDao
import com.hupisan.database.dao.RealmDataDao
import io.github.xilinjia.krdb.Realm

open class ModelDB(private val realm: Realm = RealmDatabase.getRealm()) {
    fun dataDAO(): DataDao = RealmDataDao(realm)

    companion object {
        @Volatile
        private var INSTANCE: ModelDB? = null

        fun getDataBase(context: Context? = null): ModelDB {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ModelDB(RealmDatabase.getRealm()).also { INSTANCE = it }
            }
        }
    }
}
