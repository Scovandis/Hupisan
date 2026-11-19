package com.hupisan.di.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.hupisan.database.entity.PaymentModel
import io.github.xilinjia.krdb.Realm
import io.github.xilinjia.krdb.UpdatePolicy
import io.github.xilinjia.krdb.ext.query
import io.github.xilinjia.krdb.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map

class PaymentDB(private val realm: Realm) {

    fun getDataPembayaran(): LiveData<List<PaymentModel>> {
        return realm.query<PaymentModel>().sort("from_uid", Sort.ASCENDING).asFlow()
            .map { it.list.toList() }
            .asLiveData(Dispatchers.IO)
    }

    fun getDataWhereUid(id: Int): LiveData<List<PaymentModel>> {
        return realm.query<PaymentModel>("from_uid == $0", id).asFlow()
            .map { it.list.toList() }
            .asLiveData(Dispatchers.IO)
    }

    suspend fun insert(model: PaymentModel) {
        realm.write {
            val nextUid = if (model.uid <= 0) {
                val maxUid = query<PaymentModel>().sort("uid", Sort.DESCENDING).first().find()?.uid ?: 0
                maxUid + 1
            } else {
                model.uid
            }
            copyToRealm(
                PaymentModel(
                    uid = nextUid,
                    from_uid = model.from_uid,
                    diUpdate = model.diUpdate,
                    jumlahPembayaran = model.jumlahPembayaran,
                    keterangan = model.keterangan,
                    diTambahAtaudiKurangiAtauLunas = model.diTambahAtaudiKurangiAtauLunas,
                    typeJenis = model.typeJenis
                ),
                UpdatePolicy.ALL
            )
        }
    }

    suspend fun deleteDataFromUid(fromID: Int) {
        realm.write {
            val items = query<PaymentModel>("from_uid == $0", fromID).find()
            delete(items)
        }
    }
}
