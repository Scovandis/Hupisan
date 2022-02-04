package com.example.hhhh.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.hhhh.database.db.DataModelPembayaranDB
import com.example.hhhh.database.entity.DataPembayaranModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PembayaranViewModel (app: Application): AndroidViewModel(app){
    val dataPembayaran: LiveData<List<DataPembayaranModel>>
    val repository: DataPembayaranRepository


    init {
        val dao = DataModelPembayaranDB.getDataBase(app).dataPembayaranDao()
        repository = DataPembayaranRepository(dao)
        dataPembayaran = repository.dataFromUid
    }

    fun insertData(data: DataPembayaranModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertData(data)
    }

    fun getDataPembayaranFromUid(uid: Int): LiveData<List<DataPembayaranModel>>{
        return repository.getDataPembayaran(uid)
    }
}