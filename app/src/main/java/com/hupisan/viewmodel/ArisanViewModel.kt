package com.hupisan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.hupisan.data.repository.ArisanRepository
import com.hupisan.database.entity.ArisanModel

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

    fun deleteMemberWhereGroupId(groupId: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteMemberWhereGroupId(groupId)
    }

    fun deleteMemberWhereUID(uid: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteMemberWhereUID(uid)
    }
}
