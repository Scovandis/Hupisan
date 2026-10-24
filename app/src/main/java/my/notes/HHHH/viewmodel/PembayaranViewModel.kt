package my.notes.HHHH.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.notes.HHHH.data.repository.DataPembayaranRepository
import my.notes.HHHH.database.entity.DataPembayaranModel

class PembayaranViewModel(val repository: DataPembayaranRepository) : ViewModel() {
    val dataPembayaran: LiveData<List<DataPembayaranModel>> = repository.dataFromUid

    fun insertData(data: DataPembayaranModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertData(data)
    }

    fun getDataPembayaranFromUid(uid: Int): LiveData<List<DataPembayaranModel>> {
        return repository.getDataPembayaran(uid)
    }

    fun deleteDataFormUid(uid: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteDataFromUid(uid)
    }
}
