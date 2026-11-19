package com.hupisan.database.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.hupisan.database.entity.PaymentModel
import io.github.xilinjia.krdb.Realm
import io.github.xilinjia.krdb.UpdatePolicy
import io.github.xilinjia.krdb.ext.query
import io.github.xilinjia.krdb.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map

interface PaymentDao {
    suspend fun insertData(model: PaymentModel)
    fun getDataPembayaran(): LiveData<List<PaymentModel>>
    fun getDataWhereUid(id: Int): LiveData<List<PaymentModel>>
    fun deleteDataFromUid(fromID: Int)
}

open class RealmPaymentDao(private val realm: Realm) : PaymentDao {

    override suspend fun insertData(model: PaymentModel) {
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

    override fun getDataPembayaran(): LiveData<List<PaymentModel>> {
        return realm.query<PaymentModel>().sort("from_uid", Sort.ASCENDING).asFlow()
            .map { it.list.toList() }
            .asLiveData(Dispatchers.IO)
    }

    override fun getDataWhereUid(id: Int): LiveData<List<PaymentModel>> {
        return realm.query<PaymentModel>("from_uid == $0", id).asFlow()
            .map { it.list.toList() }
            .asLiveData(Dispatchers.IO)
    }

    override fun deleteDataFromUid(fromID: Int) {
        realm.writeBlocking {
            val items = query<PaymentModel>("from_uid == $0", fromID).find()
            delete(items)
        }
    }
}
