package my.notes.HHHH.database

import androidx.lifecycle.LiveData
import my.notes.HHHH.database.dao.ArisanGroupDataDao
import my.notes.HHHH.database.entity.GrupArisanModel


class ArisanGorupRepository (private val dao: ArisanGroupDataDao){
    val allData: LiveData<List<GrupArisanModel>> by lazy { dao.allGroupArisan() }

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
    suspend fun deleteAllData(status_dimulai: Boolean){
        dao.deleteAllData(status_dimulai)
    }
    suspend fun deleteDataWhereUid(uid: Int){
        dao.deleteDataWhereUid(uid)
    }
    suspend fun deleteGroupWhereStatus(status_dimulai: Boolean){
        dao.deleteGroupWhereStatus(status_dimulai)
    }
    suspend fun updateGroupWhereStatus(diupdate: String, status_dimulai: Boolean, groupId: Int){
        dao.updateGroupWhereStatus(diupdate, status_dimulai, groupId)
    }

}