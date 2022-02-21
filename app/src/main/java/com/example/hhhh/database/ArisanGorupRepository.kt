package com.example.hhhh.database

import androidx.lifecycle.LiveData
import com.example.hhhh.database.dao.ArisanGroupDataDao
import com.example.hhhh.database.entity.GrupArisanModel

class ArisanGorupRepository (private val dao: ArisanGroupDataDao){
    val allData: LiveData<List<GrupArisanModel>> = dao.allGroupArisan()

    suspend fun insertGroup(dataGroup : GrupArisanModel){
        dao.insertData(dataGroup)
    }
    suspend fun deleteGroup(dataGroup: GrupArisanModel){
        dao.deleteData(dataGroup)
    }

    fun getAllGroupArisan(): LiveData<List<GrupArisanModel>>{
        return dao.allGroupArisan()
    }
    fun getDataWhereName(group_name: String): LiveData<GrupArisanModel>{
        return dao.getDataWhereUid(group_name)
    }
    suspend fun updateGroupWhereNeeded(diupdat: String, anggaran_terkumpul: Int, jumlah_member: Int, fromGroupId: Int){
        dao.updateGroupWhere(diupdat, anggaran_terkumpul, jumlah_member, fromGroupId)
    }
    fun getAllGroupWhereStatus(status_dimulai: Boolean): LiveData<List<GrupArisanModel>>{
        return dao.getAllGroupWhereStatus(status_dimulai)
    }
    fun deleteAllData(status_dimulai: Boolean){
        dao.deleteAllData(status_dimulai)
    }
    fun deleteDataWhereUid(uid: Int){
        dao.deleteDataWhereUid(uid)
    }
}