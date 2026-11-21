package com.hupisan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.hupisan.data.repository.DataRepository
import com.hupisan.database.entity.DataModel

open class DataViewModel(val repository: DataRepository) : ViewModel() {
    val allData: LiveData<List<DataModel>> = repository.allData

    fun deleteData(data: DataModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(data)
    }

    fun updateData(data: DataModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(data)
    }

    fun insertData(data: DataModel, onInserted: ((Int) -> Unit)? = null) = viewModelScope.launch(Dispatchers.IO) {
        val insertedId = repository.insert(data)
        onInserted?.invoke(insertedId)
    }

    fun updateDataJumlah(jumlah: Int, uidFrom: Int, statusData: Boolean, tanggal_lunas: String, dibayar: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateJumlah(jumlah, uidFrom, statusData, tanggal_lunas, dibayar)
    }

    fun getDataWhereId(uid: Int): LiveData<DataModel?> {
        return repository.getDataWhereUid(uid)
    }

    fun getAllDataWhereStatusData(statusData: Boolean, jenis: Boolean): LiveData<List<DataModel>> {
        return repository.getAllDataWhereStatusData(statusData, jenis)
    }

    fun getDataFromDate(tanggalDibuat: String): LiveData<List<DataModel>> {
        return repository.getAllDataFromDate(tanggalDibuat)
    }

    fun loadDataLimit(statusData: Boolean): LiveData<List<DataModel>> {
        return repository.loadDataLimit(statusData)
    }

    fun deleteAllDataWhere(jenis: Boolean, statusData: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAllDataWhere(jenis, statusData)
    }

    fun getAllDataWhereStatusLunas(statusData: Boolean): LiveData<List<DataModel>> {
        return repository.getAllDataWhereStatusLunas(statusData)
    }

    fun getAllDataWhereJenis(jenis: Boolean): LiveData<List<DataModel>> {
        return repository.getAllDataWhereJenis(jenis)
    }
}
