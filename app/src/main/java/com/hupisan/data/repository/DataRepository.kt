package com.hupisan.data.repository

import androidx.lifecycle.LiveData
import com.hupisan.database.entity.DataModel
import com.hupisan.di.database.DataDB

class DataRepository(private val dataDB: DataDB) {

    val allData: LiveData<List<DataModel>> = dataDB.getAllData()

    suspend fun insert(data: DataModel) {
        dataDB.insert(data)
    }

    suspend fun delete(data: DataModel) {
        dataDB.delete(data)
    }

    suspend fun update(data: DataModel) {
        dataDB.update(data)
    }

    suspend fun updateJumlah(
        jumlah: Int,
        idFrom: Int,
        statusData: Boolean,
        tanggal_lunas: String,
        dibayar: Int
    ) {
        dataDB.updateJumlah(jumlah, idFrom, statusData, tanggal_lunas, dibayar)
    }

    fun getDataWhereUid(uid: Int): LiveData<DataModel?> {
        return dataDB.getDataWhereUid(uid)
    }

    fun getAllDataWhereStatusData(statusData: Boolean, jenis: Boolean): LiveData<List<DataModel>> {
        return dataDB.getAllDataWhereStatusData(statusData, jenis)
    }

    fun getAllDataFromDate(tanggalDibuat: String): LiveData<List<DataModel>> {
        return dataDB.getAllDataNewFromDate(tanggalDibuat)
    }

    fun loadDataLimit(statusData: Boolean): LiveData<List<DataModel>> {
        return dataDB.loadDataWhereLimit(statusData, 12)
    }

    suspend fun deleteAllDataWhere(jenis: Boolean, statusData: Boolean) {
        dataDB.deleteAllData(jenis, statusData)
    }

    fun getAllDataWhereStatusLunas(status: Boolean): LiveData<List<DataModel>> {
        return dataDB.getAllDataWhereStatusLunas(status)
    }

    fun getAllDataWhereJenis(jenis: Boolean): LiveData<List<DataModel>> {
        return dataDB.getAllDataWhereJenis(jenis)
    }
}
