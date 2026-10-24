package my.notes.HHHH.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.notes.HHHH.database.db.ArisanModelDB
import my.notes.HHHH.database.entity.ArisanModel

class ArisanViewModel (val app: Application): AndroidViewModel(app){

    val dataGroup: LiveData<List<ArisanModel>>
    val repository: my.notes.HHHH.database.ArisanRepository

    init {
        val dao = ArisanModelDB.getDataBase(app).arisanDao()
        repository = my.notes.HHHH.database.ArisanRepository(dao)
        dataGroup = repository.allData
    }

    fun insertUser(data: ArisanModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertUser(data)
    }

    fun getAllMemberFromGroupId(groupId: Int): LiveData<List<ArisanModel>>{
        return repository.getMemberWhereGroupId(groupId)
    }

    fun getDataUser(): LiveData<List<ArisanModel>> {
        return repository.getUserArisan()
    }
    fun deleteMemberWhereGroupId(groupId: Int){
        repository.deleteMemberWhereGroupId(groupId)
    }

    fun deleteMemberWhereUID(uid: Int){
        repository.deleteMemberWhereUID(uid)
    }
}