package my.notes.HHHH.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.notes.HHHH.data.repository.ArisanRepository
import my.notes.HHHH.database.entity.ArisanModel

class ArisanViewModel(val repository: ArisanRepository) : ViewModel() {

    val dataGroup: LiveData<List<ArisanModel>> = repository.allData

    fun insertUser(data: ArisanModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertUser(data)
    }

    fun getAllMemberFromGroupId(groupId: Int): LiveData<List<ArisanModel>> {
        return repository.getMemberWhereGroupId(groupId)
    }

    fun getDataUser(): LiveData<List<ArisanModel>> {
        return repository.getUserArisan()
    }

    fun deleteMemberWhereGroupId(groupId: Int) {
        repository.deleteMemberWhereGroupId(groupId)
    }

    fun deleteMemberWhereUID(uid: Int) {
        repository.deleteMemberWhereUID(uid)
    }
}
