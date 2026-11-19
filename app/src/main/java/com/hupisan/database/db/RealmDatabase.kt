package com.hupisan.database.db

import com.hupisan.database.entity.ArisanModel
import com.hupisan.database.entity.DataModel
import com.hupisan.database.entity.GrupArisanModel
import com.hupisan.database.entity.PaymentModel
import io.github.xilinjia.krdb.Realm
import io.github.xilinjia.krdb.RealmConfiguration

object RealmDatabase {
    @Volatile
    private var realmInstance: Realm? = null

    fun getRealm(): Realm {
        return realmInstance ?: synchronized(this) {
            realmInstance ?: run {
                val config = RealmConfiguration.Builder(
                    schema = setOf(
                        DataModel::class,
                        PaymentModel::class,
                        ArisanModel::class,
                        GrupArisanModel::class
                    )
                )
                    .name("hupisan.realm")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build()
                Realm.open(config).also { realmInstance = it }
            }
        }
    }

    fun close() {
        realmInstance?.close()
        realmInstance = null
    }
}
