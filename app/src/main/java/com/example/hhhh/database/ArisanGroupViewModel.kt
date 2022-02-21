package com.example.hhhh.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hhhh.database.db.ArisanModelDB
import com.example.hhhh.database.db.GroupArisanModelDB
import com.example.hhhh.database.entity.GrupArisanModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ArisanGroupViewModel(app:Application): AndroidViewModel(app) {

    val dataGroup: LiveData<List<GrupArisanModel>>
    val repository: ArisanGorupRepository

    init {
        val dao = GroupArisanModelDB.getDataBase(app).GroupArisanDao()
        repository = ArisanGorupRepository(dao)
        dataGroup = repository.allData
    }

    fun insertGroup(data: GrupArisanModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertGroup(data)
    }
    fun deleteGroup(data: GrupArisanModel) = viewModelScope.launch (Dispatchers.IO) {
        repository.deleteGroup(data)
    }
    fun getDataPembayaranFromUid(): LiveData<List<GrupArisanModel>>{
        return repository.getAllGroupArisan()
    }
    fun getDataWhereGroupName(group_name: String): LiveData<GrupArisanModel>{
        return repository.getDataWhereName(group_name)
    }
    fun updateDataWhereGroupNeeded(diupdate: String, anggaran_terkumpul: Int, jumlah_member: Int, fromGroupId: Int)= viewModelScope.launch{Dispatchers.IO
        repository.updateGroupWhereNeeded(diupdate, anggaran_terkumpul, jumlah_member, fromGroupId)
    }
    fun getAllGroupWhereStatus(status_dimulai: Boolean): LiveData<List<GrupArisanModel>>{
        return repository.getAllGroupWhereStatus(status_dimulai)
    }

    fun deleteAllGroup(status_dimulai: Boolean) = viewModelScope.launch(Dispatchers.IO){
        repository.deleteAllData(status_dimulai)
    }
    fun deleteGroupWhereUid(uid : Int) = viewModelScope.launch(Dispatchers.IO){
        repository.deleteDataWhereUid(uid)
    }
}