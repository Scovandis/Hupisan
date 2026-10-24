package my.notes.HHHH.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.notes.HHHH.data.repository.DataRepository
import my.notes.HHHH.database.entity.DataModel

open class DataViewModel(val repository: DataRepository) : ViewModel() {
    val allData: LiveData<List<DataModel>> = repository.allData

    fun deleteData(data: DataModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(data)
    }

    fun updateData(data: DataModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(data)
    }

    fun insertData(data: DataModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(data)
    }

    fun updateDataJumlah(jumlah: Int, uidFrom: Int, statusData: Boolean, tanggal_lunas: String, dibayar: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateJumlah(jumlah, uidFrom, statusData, tanggal_lunas, dibayar)
    }

    fun getDataWhereId(uid: Int): LiveData<DataModel> {
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
