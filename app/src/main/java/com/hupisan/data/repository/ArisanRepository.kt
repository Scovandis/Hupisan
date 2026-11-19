package com.hupisan.data.repository

import androidx.lifecycle.LiveData
import com.hupisan.database.entity.ArisanModel
import com.hupisan.di.database.ArisanDB

class ArisanRepository(private val arisanDB: ArisanDB) {

    val allData: LiveData<List<ArisanModel>> = arisanDB.allUserArisan()

    suspend fun insertUser(data: ArisanModel) {
        arisanDB.insert(data)
    }

    fun getUserArisan(): LiveData<List<ArisanModel>> {
        return arisanDB.allUserArisan()
    }

    fun getMemberWhereGroupId(groupId: Int): LiveData<List<ArisanModel>> {
        return arisanDB.getUserArisanWhere(groupId)
    }

    suspend fun deleteMemberWhereGroupId(groupId: Int) {
        arisanDB.deleteMemberWhereIdGroup(groupId)
    }

    suspend fun deleteMemberWhereUID(uid: Int) {
        arisanDB.deleteMemberWhereUid(uid)
    }
}
