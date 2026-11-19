package com.hupisan.database.db

import android.content.Context
import com.hupisan.database.dao.ArisanGroupDataDao
import com.hupisan.database.dao.RealmArisanGroupDataDao
import io.github.xilinjia.krdb.Realm

open class GroupArisanModelDB(private val realm: Realm = RealmDatabase.getRealm()) {
    fun GroupArisanDao(): ArisanGroupDataDao = RealmArisanGroupDataDao(realm)

    companion object {
        @Volatile
        private var INSTANCE: GroupArisanModelDB? = null

        fun getDataBase(context: Context? = null): GroupArisanModelDB {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: GroupArisanModelDB(RealmDatabase.getRealm()).also { INSTANCE = it }
            }
        }
    }
}
