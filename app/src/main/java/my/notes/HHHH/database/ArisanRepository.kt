package my.notes.HHHH.database

import androidx.lifecycle.LiveData

import my.notes.HHHH.database.dao.ArisanDataDao
import my.notes.HHHH.database.entity.ArisanModel

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
    fun deleteMemberWhereUID(uid: Int){
        dao.deleteMemberWhereUid(uid)
    }
}