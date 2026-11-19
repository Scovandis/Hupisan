package com.hupisan.database.db

import android.content.Context
import com.hupisan.database.dao.ArisanDataDao
import com.hupisan.database.dao.RealmArisanDataDao
import io.github.xilinjia.krdb.Realm

open class ArisanModelDB(private val realm: Realm = RealmDatabase.getRealm()) {
    fun arisanDao(): ArisanDataDao = RealmArisanDataDao(realm)

    companion object {
        @Volatile
        private var INSTANCE: ArisanModelDB? = null

        fun getDataBase(context: Context? = null): ArisanModelDB {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ArisanModelDB(RealmDatabase.getRealm()).also { INSTANCE = it }
            }
        }
    }
}
