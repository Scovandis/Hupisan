package com.example.hhhh.database

import androidx.lifecycle.LiveData
import com.example.hhhh.database.dao.ArisanDataDao
import com.example.hhhh.database.entity.ArisanModel
import com.example.hhhh.database.entity.GrupArisanModel

class ArisanRepository (private val dao: ArisanDataDao){
    val allData: LiveData<List<ArisanModel>> = dao.allUserArisan()

    suspend fun insertUser(data : ArisanModel){
        dao.insertData(data)
    }
    fun getUserArisan(): LiveData<List<ArisanModel>> {
        return dao.allUserArisan()
    }

    fun getMemberWhereGroupId(groupId : Int): LiveData<List<ArisanModel>>{
        return dao.getUserArisanWhere(groupId)
    }
    fun deleteMemberWhereGroupId(groupId: Int){
        dao.deleteMemberWhereIdGroup(groupId)
    }
}