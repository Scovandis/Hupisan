package com.example.hhhh.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hhhh.database.db.ModelDB
import com.example.hhhh.database.entity.DataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class DataViewModel(app : Application): AndroidViewModel(app) {
    val allData: LiveData<List<DataModel>>
    val repository: DataRepository

    init {
        val dao = ModelDB.getDataBase(app).dataDAO()
        repository = DataRepository(dao)
        allData = repository.allData
    }

    fun deleteData(data: DataModel) = viewModelScope.launch (Dispatchers.IO){
        repository.delete(data)
    }

    fun updateData(data: DataModel) = viewModelScope.launch (Dispatchers.IO){
        repository.update(data)
    }

    fun insertData(data: DataModel) = viewModelScope.launch (Dispatchers.IO){
        repository.insert(data)
    }

    fun updateDataJumlah(jumlah: Int, uidFrom: Int, statusData: String, tanggal_lunas: String, dibayar: Int) = viewModelScope.launch (Dispatchers.IO){
        repository.updateJumlah(jumlah, uidFrom, statusData, tanggal_lunas, dibayar)
    }

    fun getDataWhereId(uid: Int): LiveData<DataModel>{
        return repository.getDataWhereUid(uid)
    }

    fun getAllDataWhereStatusData(statusData: String, jenis: String): LiveData<List<DataModel>>{
        return repository.getAllDataWhereStatusData(statusData, jenis)
    }

    fun getDataFromDate(tanggalDibuat: String): LiveData<List<DataModel>>{
        return repository.getAllDataFromDate(tanggalDibuat)
    }

    fun loadDataLimit(statusData: String): LiveData<List<DataModel>>{
        return repository.loadDataLimit(statusData)
    }
    fun deleteAllDataWhere(jenis: String, statusData: String) = viewModelScope.launch (Dispatchers.IO){
        repository.deleteAllDataWhere(jenis, statusData)
    }

    fun getAllDataWhereStatusLunas(statusData: String): LiveData<List<DataModel>>{
        return repository.getAllDataWhereStatusLunas(statusData)
    }
    fun getAllDataWhereJenis(jenis: String): LiveData<List<DataModel>>{
        return repository.getAllDataWhereJenis(jenis)
    }
}