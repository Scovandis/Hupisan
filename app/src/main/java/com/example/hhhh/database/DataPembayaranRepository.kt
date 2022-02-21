package com.example.hhhh.database

import androidx.lifecycle.LiveData
import com.example.hhhh.database.dao.DataPembayaranDao
import com.example.hhhh.database.entity.DataModel
import com.example.hhhh.database.entity.DataPembayaranModel

class DataPembayaranRepository (private val fromDao: DataPembayaranDao){
    val dataFromUid: LiveData<List<DataPembayaranModel>> = fromDao.getDataPembayaran()

    suspend fun insertData(data: DataPembayaranModel){
        fromDao.insertData(data)
    }

    fun getDataPembayaran(uid: Int): LiveData<List<DataPembayaranModel>>{
        return fromDao.getDataWhereUid(uid)
    }
    fun deleteDataFromUid(uid: Int){
        fromDao.deleteDataFromUid(uid)
    }
}