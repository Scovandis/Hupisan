package com.example.hhhh.database

import androidx.lifecycle.LiveData
import com.example.hhhh.database.dao.DataDao
import com.example.hhhh.database.entity.DataModel

class DataRepository(private val dataDao: DataDao) {

    val allData: LiveData<List<DataModel>> = dataDao.getAllData()


    suspend fun insert(data: DataModel){
        dataDao.insertData(data)
    }

    suspend fun delete(data: DataModel){
        dataDao.deleteData(data)
    }

    suspend fun update(data: DataModel){
        dataDao.updateData(data)
    }
    suspend fun updateJumlah(jumlah: Int, idFrom: Int, statusData: String, tanggal_lunas:String, dibayar: Int){
        dataDao.updateDataJumlah(jumlah, idFrom, statusData, tanggal_lunas, dibayar)
    }

    fun getDataWhereUid(uid: Int): LiveData<DataModel>{
        return dataDao.getDataWhereUid(uid)
    }

    fun getAllDataWhereStatusData(statusData: String, jenis: String): LiveData<List<DataModel>>{
        return dataDao.getAllDataWhereStatusData(statusData, jenis)
    }

    fun getAllDataFromDate(tanggalDibuat: String): LiveData<List<DataModel>>{
        return dataDao.getAllDataNewFromDate(tanggalDibuat)
    }

    fun loadDataLimit(statusData: String): LiveData<List<DataModel>>{
        return dataDao.loadDataWhereLimit(statusData, 12)
    }
    fun deleteAllDataWhere(jenis: String, statusData: String){
        dataDao.deleteAllData(jenis, statusData)
    }
    fun getAllDataWhereStatusLunas(status: String): LiveData<List<DataModel>>{
        return dataDao.getAllDataWhereStatusLunas(status)
    }
    fun getAllDataWhereJenis(jenis: String): LiveData<List<DataModel>>{
        return dataDao.getAllDataWhereJenis(jenis)
    }

}