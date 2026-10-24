package my.notes.HHHH.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.notes.HHHH.database.db.DataModelPembayaranDB
import my.notes.HHHH.database.entity.DataPembayaranModel

class PembayaranViewModel (app: Application): AndroidViewModel(app){
    val dataPembayaran: LiveData<List<DataPembayaranModel>>
    val repository: my.notes.HHHH.database.DataPembayaranRepository


    init {
        val dao = DataModelPembayaranDB.getDataBase(app).dataPembayaranDao()
        repository = my.notes.HHHH.database.DataPembayaranRepository(dao)
        dataPembayaran = repository.dataFromUid
    }

    fun insertData(data: DataPembayaranModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertData(data)
    }

    fun getDataPembayaranFromUid(uid: Int): LiveData<List<DataPembayaranModel>>{
        return repository.getDataPembayaran(uid)
    }

    fun deleteDataFormUid(uid: Int)=viewModelScope.launch (Dispatchers.IO) {
        repository.deleteDataFromUid(uid)
    }
}