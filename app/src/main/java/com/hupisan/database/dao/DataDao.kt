package com.hupisan.database.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.hupisan.database.entity.DataModel
import io.github.xilinjia.krdb.Realm
import io.github.xilinjia.krdb.UpdatePolicy
import io.github.xilinjia.krdb.ext.query
import io.github.xilinjia.krdb.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map

interface DataDao {
    suspend fun updateData(model: DataModel)
    suspend fun insertData(model: DataModel)
    suspend fun deleteData(model: DataModel)
    fun getAllData(): LiveData<List<DataModel>>
    fun getDataWhereUid(id: Int): LiveData<DataModel>
    suspend fun updateDataJumlah(jumlah: Int, id: Int, statusData: Boolean, tanggal_lunas: String, dibayar: Int)
    fun getAllDataWhereStatusLunas(statusData: Boolean): LiveData<List<DataModel>>
    fun getAllDataWhereStatusData(statusData: Boolean, jenis: Boolean): LiveData<List<DataModel>>
    fun getAllDataNewFromDate(tanggalDibuat: String): LiveData<List<DataModel>>
    fun loadDataWhereLimit(statusData: Boolean, limit: Int): LiveData<List<DataModel>>
    fun getAllDataWhereJenis(jenis: Boolean): LiveData<List<DataModel>>
    fun deleteAllData(jenis: Boolean, statusData: Boolean)
}

open class RealmDataDao(private val realm: Realm) : DataDao {

    override suspend fun updateData(model: DataModel) {
        realm.write {
            val live = query<DataModel>("id == $0", model.id).first().find()
            if (live != null) {
                live.username = model.username
                live.deskripsi = model.deskripsi
                live.jumlah = model.jumlah
                live.diBuat = model.diBuat
                live.jatuhTempo = model.jatuhTempo
                live.notif = model.notif
                live.genderUser = model.genderUser
                live.jenisPinjaman = model.jenisPinjaman
                live.statusDataSelesai = model.statusDataSelesai
                live.jam_jatuh_tempo = model.jam_jatuh_tempo
                live.tanggal_lunas = model.tanggal_lunas
                live.jumlah_dibayar = model.jumlah_dibayar
            } else {
                copyToRealm(model, UpdatePolicy.ALL)
            }
        }
    }

    override suspend fun insertData(model: DataModel) {
        realm.write {
            val nextId = if (model.id <= 0) {
                val maxId = query<DataModel>().sort("id", Sort.DESCENDING).first().find()?.id ?: 0
                maxId + 1
            } else {
                model.id
            }
            copyToRealm(
                DataModel(
                    id = nextId,
                    username = model.username,
                    deskripsi = model.deskripsi,
                    jumlah = model.jumlah,
                    diBuat = model.diBuat,
                    jatuhTempo = model.jatuhTempo,
                    notif = model.notif,
                    genderUser = model.genderUser,
                    jenisPinjaman = model.jenisPinjaman,
                    statusDataSelesai = model.statusDataSelesai,
                    jam_jatuh_tempo = model.jam_jatuh_tempo,
                    tanggal_lunas = model.tanggal_lunas,
                    jumlah_dibayar = model.jumlah_dibayar
                ),
                UpdatePolicy.ALL
            )
        }
    }

    override suspend fun deleteData(model: DataModel) {
        realm.write {
            val live = query<DataModel>("id == $0", model.id).first().find()
            if (live != null) {
                delete(live)
            }
        }
    }

    override fun getAllData(): LiveData<List<DataModel>> {
        return realm.query<DataModel>().sort("id", Sort.ASCENDING).asFlow()
            .map { it.list.toList() }
            .asLiveData(Dispatchers.IO)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getDataWhereUid(id: Int): LiveData<DataModel> {
        return realm.query<DataModel>("id == $0", id).first().asFlow()
            .map { it.obj }
            .asLiveData(Dispatchers.IO) as LiveData<DataModel>
    }

    override suspend fun updateDataJumlah(
        jumlah: Int,
        id: Int,
        statusData: Boolean,
        tanggal_lunas: String,
        dibayar: Int
    ) {
        realm.write {
            val live = query<DataModel>("id == $0", id).first().find()
            if (live != null) {
                live.jumlah = jumlah
                live.statusDataSelesai = statusData
                live.tanggal_lunas = tanggal_lunas
                live.jumlah_dibayar = dibayar
            }
        }
    }

    override fun getAllDataWhereStatusLunas(statusData: Boolean): LiveData<List<DataModel>> {
        return realm.query<DataModel>("statusDataSelesai == $0", statusData)
            .sort("id", Sort.DESCENDING)
            .asFlow()
            .map { it.list.toList() }
            .asLiveData(Dispatchers.IO)
    }

    override fun getAllDataWhereStatusData(statusData: Boolean, jenis: Boolean): LiveData<List<DataModel>> {
        return realm.query<DataModel>("statusDataSelesai == $0 AND jenisPinjaman == $1", statusData, jenis)
            .asFlow()
            .map { it.list.toList() }
            .asLiveData(Dispatchers.IO)
    }

    override fun getAllDataNewFromDate(tanggalDibuat: String): LiveData<List<DataModel>> {
        return realm.query<DataModel>("diBuat == $0", tanggalDibuat)
            .asFlow()
            .map { it.list.toList() }
            .asLiveData(Dispatchers.IO)
    }

    override fun loadDataWhereLimit(statusData: Boolean, limit: Int): LiveData<List<DataModel>> {
        return realm.query<DataModel>("statusDataSelesai == $0", statusData)
            .sort("id", Sort.DESCENDING)
            .asFlow()
            .map { it.list.take(limit) }
            .asLiveData(Dispatchers.IO)
    }

    override fun getAllDataWhereJenis(jenis: Boolean): LiveData<List<DataModel>> {
        return realm.query<DataModel>("jenisPinjaman == $0", jenis)
            .asFlow()
            .map { it.list.toList() }
            .asLiveData(Dispatchers.IO)
    }

    override fun deleteAllData(jenis: Boolean, statusData: Boolean) {
        realm.writeBlocking {
            val items = query<DataModel>("jenisPinjaman == $0 AND statusDataSelesai == $1", jenis, statusData).find()
            delete(items)
        }
    }
}
