package com.hupisan.data.repository

import androidx.lifecycle.LiveData
import com.hupisan.database.entity.GrupArisanModel
import com.hupisan.di.database.ArisanGroupDB

class ArisanGroupRepository(private val arisanGroupDB: ArisanGroupDB) {

    val allData: LiveData<List<GrupArisanModel>> = arisanGroupDB.allGroupArisan()

    suspend fun insertGroup(data: GrupArisanModel) {
        arisanGroupDB.insert(data)
    }

    suspend fun deleteGroup(data: GrupArisanModel) {
        arisanGroupDB.delete(data)
    }

    fun getAllGroupArisan(): LiveData<List<GrupArisanModel>> {
        return arisanGroupDB.allGroupArisan()
    }

    fun getDataWhereName(group_name: String): LiveData<GrupArisanModel> {
        return arisanGroupDB.getDataWhereName(group_name)
    }

    suspend fun updateGroupWhereNeeded(
        diupdate: String,
        anggaran_terkumpul: Int,
        jumlah_member: Int,
        fromGroupId: Int
    ) {
        arisanGroupDB.updateGroupWhereNeeded(diupdate, anggaran_terkumpul, jumlah_member, fromGroupId)
    }

    suspend fun updateGroupWhereStatus(diupdate: String, status_dimulai: Boolean, groupId: Int) {
        arisanGroupDB.updateGroupWhereStatus(diupdate, status_dimulai, groupId)
    }

    fun getAllGroupWhereStatus(status_dimulai: Boolean): LiveData<List<GrupArisanModel>> {
        return arisanGroupDB.getAllGroupWhereStatus(status_dimulai)
    }

    suspend fun deleteAllData(status_dimulai: Boolean) {
        arisanGroupDB.deleteAllData(status_dimulai)
    }

    suspend fun deleteDataWhereUid(uid: Int) {
        arisanGroupDB.deleteDataWhereUid(uid)
    }

    suspend fun deleteGroupWhereStatus(status_dimulai: Boolean) {
        arisanGroupDB.deleteGroupWhereStatus(status_dimulai)
    }
}
