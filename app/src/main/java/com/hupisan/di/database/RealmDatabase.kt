package com.hupisan.di.database

import android.util.Log
import com.hupisan.database.entity.ArisanModel
import com.hupisan.database.entity.DataModel
import com.hupisan.database.entity.GrupArisanModel
import com.hupisan.database.entity.PaymentModel
import io.github.xilinjia.krdb.Realm
import io.github.xilinjia.krdb.RealmConfiguration
import io.github.xilinjia.krdb.migration.AutomaticSchemaMigration

object RealmDatabase {
    private const val TAG = "RealmDatabase"
    private const val CURRENT_SCHEMA_VERSION = 1L

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
                    .schemaVersion(CURRENT_SCHEMA_VERSION)
                    .migration(
                        AutomaticSchemaMigration { context ->
                            val oldVersion = context.oldRealm.schemaVersion()
                            Log.i(TAG, "Migrating Realm schema from v$oldVersion to v$CURRENT_SCHEMA_VERSION")
                            // Preserve all user financial records across schema upgrades.
                            // Add explicit version-gated field transformations when CURRENT_SCHEMA_VERSION increments.
                        }
                    )
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
