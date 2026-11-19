package com.hupisan.di.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.hupisan.database.entity.GrupArisanModel
import io.github.xilinjia.krdb.Realm
import io.github.xilinjia.krdb.UpdatePolicy
import io.github.xilinjia.krdb.ext.query
import io.github.xilinjia.krdb.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map

class ArisanGroupDB(private val realm: Realm) {

    fun allGroupArisan(): LiveData<List<GrupArisanModel>> {
        return realm.query<GrupArisanModel>().sort("uid", Sort.ASCENDING).asFlow()
            .map { it.list.toList() }
            .asLiveData(Dispatchers.IO)
    }

    suspend fun insert(model: GrupArisanModel) {
        realm.write {
            val nextUid = if (model.uid <= 0) {
                val maxUid = query<GrupArisanModel>().sort("uid", Sort.DESCENDING).first().find()?.uid ?: 0
                maxUid + 1
            } else {
                model.uid
            }
            copyToRealm(
                GrupArisanModel(
                    uid = nextUid,
                    grup_name = model.grup_name,
                    dibuat = model.dibuat,
                    diupdate = model.diupdate,
                    anggaran_kesepakatan_peruser = model.anggaran_kesepakatan_peruser,
                    anggaran_terkumpul = model.anggaran_terkumpul,
                    total_prize_poll = model.total_prize_poll,
                    jumlah_member = model.jumlah_member,
                    status_dimulai = model.status_dimulai
                ),
                UpdatePolicy.ALL
            )
        }
    }

    suspend fun update(model: GrupArisanModel) {
        realm.write {
            val live = query<GrupArisanModel>("uid == $0", model.uid).first().find()
            if (live != null) {
                live.grup_name = model.grup_name
                live.dibuat = model.dibuat
                live.diupdate = model.diupdate
                live.anggaran_kesepakatan_peruser = model.anggaran_kesepakatan_peruser
                live.anggaran_terkumpul = model.anggaran_terkumpul
                live.total_prize_poll = model.total_prize_poll
                live.jumlah_member = model.jumlah_member
                live.status_dimulai = model.status_dimulai
            } else {
                copyToRealm(model, UpdatePolicy.ALL)
            }
        }
    }

    suspend fun delete(model: GrupArisanModel) {
        realm.write {
            val live = query<GrupArisanModel>("uid == $0", model.uid).first().find()
            if (live != null) {
                delete(live)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun getDataWhereName(group_name: String): LiveData<GrupArisanModel> {
        return realm.query<GrupArisanModel>("grup_name == $0", group_name).first().asFlow()
            .map { it.obj }
            .asLiveData(Dispatchers.IO) as LiveData<GrupArisanModel>
    }

    suspend fun updateGroupWhereNeeded(
        diupdate: String,
        anggaran_terkumpul: Int,
        jumlah_member: Int,
        groupId: Int
    ) {
        realm.write {
            val live = query<GrupArisanModel>("uid == $0", groupId).first().find()
            if (live != null) {
                live.diupdate = diupdate
                live.anggaran_terkumpul = anggaran_terkumpul
                live.jumlah_member = jumlah_member
            }
        }
    }

    suspend fun updateGroupWhereStatus(
        diupdate: String,
        status_dimulai: Boolean,
        groupId: Int
    ) {
        realm.write {
            val live = query<GrupArisanModel>("uid == $0", groupId).first().find()
            if (live != null) {
                live.diupdate = diupdate
                live.status_dimulai = status_dimulai
            }
        }
    }

    fun getAllGroupWhereStatus(status_dimulai: Boolean): LiveData<List<GrupArisanModel>> {
        return realm.query<GrupArisanModel>("status_dimulai == $0", status_dimulai).asFlow()
            .map { it.list.toList() }
            .asLiveData(Dispatchers.IO)
    }

    suspend fun deleteAllData(status_dimulai: Boolean) {
        realm.write {
            val items = query<GrupArisanModel>("status_dimulai == $0", status_dimulai).find()
            delete(items)
        }
    }

    suspend fun deleteDataWhereUid(uidGroup: Int) {
        realm.write {
            val live = query<GrupArisanModel>("uid == $0", uidGroup).first().find()
            if (live != null) {
                delete(live)
            }
        }
    }

    suspend fun deleteGroupWhereStatus(status_dimulai: Boolean) {
        realm.write {
            val items = query<GrupArisanModel>("status_dimulai == $0", status_dimulai).find()
            delete(items)
        }
    }
}
