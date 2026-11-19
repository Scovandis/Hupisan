package com.hupisan.data.repository

import androidx.lifecycle.LiveData
import com.hupisan.database.dao.ArisanGroupDataDao
import com.hupisan.database.entity.GrupArisanModel

class ArisanGroupRepository(private val arisanGroupDataDao: ArisanGroupDataDao) {

    val allData: LiveData<List<GrupArisanModel>> = arisanGroupDataDao.allGroupArisan()

    suspend fun insertGroup(data: GrupArisanModel) {
        arisanGroupDataDao.insertData(data)
    }

    suspend fun deleteGroup(data: GrupArisanModel) {
        arisanGroupDataDao.deleteData(data)
    }

    fun getAllGroupArisan(): LiveData<List<GrupArisanModel>> {
        return arisanGroupDataDao.allGroupArisan()
    }

    fun getDataWhereName(group_name: String): LiveData<GrupArisanModel> {
        return arisanGroupDataDao.getDataWhereUid(group_name)
    }

    suspend fun updateGroupWhereNeeded(
        diupdate: String,
        anggaran_terkumpul: Int,
        jumlah_member: Int,
        fromGroupId: Int
    ) {
        arisanGroupDataDao.updateGroupWhere(diupdate, anggaran_terkumpul, jumlah_member, fromGroupId)
    }

    suspend fun updateGroupWhereStatus(diupdate: String, status_dimulai: Boolean, groupId: Int) {
        arisanGroupDataDao.updateGroupWhereStatus(diupdate, status_dimulai, groupId)
    }

    fun getAllGroupWhereStatus(status_dimulai: Boolean): LiveData<List<GrupArisanModel>> {
        return arisanGroupDataDao.getAllGroupWhereStatus(status_dimulai)
    }

    suspend fun deleteAllData(status_dimulai: Boolean) {
        arisanGroupDataDao.deleteAllData(status_dimulai)
    }

    suspend fun deleteDataWhereUid(uid: Int) {
        arisanGroupDataDao.deleteDataWhereUid(uid)
    }

    suspend fun deleteGroupWhereStatus(status_dimulai: Boolean) {
        arisanGroupDataDao.deleteGroupWhereStatus(status_dimulai)
    }
}
