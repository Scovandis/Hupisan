package my.notes.HHHH.data.repository

import androidx.lifecycle.LiveData
import my.notes.HHHH.database.dao.ArisanGroupDataDao
import my.notes.HHHH.database.entity.GrupArisanModel

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
