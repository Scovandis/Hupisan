package com.hupisan.database.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.hupisan.database.entity.ArisanModel
import io.github.xilinjia.krdb.Realm
import io.github.xilinjia.krdb.UpdatePolicy
import io.github.xilinjia.krdb.ext.query
import io.github.xilinjia.krdb.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map

interface ArisanDataDao {
    suspend fun updateData(model: ArisanModel)
    suspend fun insertData(model: ArisanModel)
    suspend fun deleteData(model: ArisanModel)
    fun allUserArisan(): LiveData<List<ArisanModel>>
    fun getUserArisanWhere(groupId: Int): LiveData<List<ArisanModel>>
    fun deleteMemberWhereIdGroup(groupId: Int)
    fun deleteMemberWhereUid(uid: Int)
}

open class RealmArisanDataDao(private val realm: Realm) : ArisanDataDao {

    override suspend fun updateData(model: ArisanModel) {
        realm.write {
            val live = query<ArisanModel>("uid == $0", model.uid).first().find()
            if (live != null) {
                live.grup_id = model.grup_id
                live.username = model.username
                live.jumlah_bayar = model.jumlah_bayar
                live.tanggal_menang = model.tanggal_menang
                live.status_bayar = model.status_bayar
                live.status_menang = model.status_menang
                live.gender = model.gender
            } else {
                copyToRealm(model, UpdatePolicy.ALL)
            }
        }
    }

    override suspend fun insertData(model: ArisanModel) {
        realm.write {
            val nextUid = if (model.uid <= 0) {
                val maxUid = query<ArisanModel>().sort("uid", Sort.DESCENDING).first().find()?.uid ?: 0
                maxUid + 1
            } else {
                model.uid
            }
            copyToRealm(
                ArisanModel(
                    uid = nextUid,
                    grup_id = model.grup_id,
                    username = model.username,
                    jumlah_bayar = model.jumlah_bayar,
                    tanggal_menang = model.tanggal_menang,
                    status_bayar = model.status_bayar,
                    status_menang = model.status_menang,
                    gender = model.gender
                ),
                UpdatePolicy.ALL
            )
        }
    }

    override suspend fun deleteData(model: ArisanModel) {
        realm.write {
            val live = query<ArisanModel>("uid == $0", model.uid).first().find()
            if (live != null) {
                delete(live)
            }
        }
    }

    override fun allUserArisan(): LiveData<List<ArisanModel>> {
        return realm.query<ArisanModel>().sort("uid", Sort.ASCENDING).asFlow()
            .map { it.list.toList() }
            .asLiveData(Dispatchers.IO)
    }

    override fun getUserArisanWhere(groupId: Int): LiveData<List<ArisanModel>> {
        return realm.query<ArisanModel>("grup_id == $0", groupId).asFlow()
            .map { it.list.toList() }
            .asLiveData(Dispatchers.IO)
    }

    override fun deleteMemberWhereIdGroup(groupId: Int) {
        realm.writeBlocking {
            val items = query<ArisanModel>("grup_id == $0", groupId).find()
            delete(items)
        }
    }

    override fun deleteMemberWhereUid(uid: Int) {
        realm.writeBlocking {
            val live = query<ArisanModel>("uid == $0", uid).first().find()
            if (live != null) {
                delete(live)
            }
        }
    }
}
