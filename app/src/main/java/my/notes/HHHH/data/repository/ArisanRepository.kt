package my.notes.HHHH.data.repository

import androidx.lifecycle.LiveData
import my.notes.HHHH.database.dao.ArisanDataDao
import my.notes.HHHH.database.entity.ArisanModel

class ArisanRepository(private val arisanDataDao: ArisanDataDao) {

    val allData: LiveData<List<ArisanModel>> = arisanDataDao.allUserArisan()

    suspend fun insertUser(data: ArisanModel) {
        arisanDataDao.insertData(data)
    }

    fun getUserArisan(): LiveData<List<ArisanModel>> {
        return arisanDataDao.allUserArisan()
    }

    fun getMemberWhereGroupId(groupId: Int): LiveData<List<ArisanModel>> {
        return arisanDataDao.getUserArisanWhere(groupId)
    }

    fun deleteMemberWhereGroupId(groupId: Int) {
        arisanDataDao.deleteMemberWhereIdGroup(groupId)
    }

    fun deleteMemberWhereUID(uid: Int) {
        arisanDataDao.deleteMemberWhereUid(uid)
    }
}
