package com.example.hhhh.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.hhhh.database.db.ArisanModelDB
import com.example.hhhh.database.db.GroupArisanModelDB
import com.example.hhhh.database.entity.ArisanModel
import com.example.hhhh.database.entity.GrupArisanModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ArisanViewModel (val app: Application): AndroidViewModel(app){

    val dataGroup: LiveData<List<ArisanModel>>
    val repository: ArisanRepository

    init {
        val dao = ArisanModelDB.getDataBase(app).arisanDao()
        repository = ArisanRepository(dao)
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
}