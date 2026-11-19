package com.hupisan.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hupisan.database.entity.ArisanModel


@Dao
interface ArisanDataDao {
    @Update
    suspend fun updateData(model: ArisanModel)

    @Insert
    suspend fun insertData(model: ArisanModel)

    @Delete
    suspend fun deleteData(model: ArisanModel)

    @Query("SELECT * FROM  arisan_user_model ORDER BY uid ASC")
    fun allUserArisan(): LiveData<List<ArisanModel>>

    @Query("SELECT * FROM arisan_user_model WHERE from_uid_group= :groupId")
    fun getUserArisanWhere(groupId: Int): LiveData<List<ArisanModel>>


    @Query("DELETE FROM arisan_user_model WHERE from_uid_group = :groupId")
    fun deleteMemberWhereIdGroup(groupId: Int)

    @Query("DELETE FROM arisan_user_model WHERE uid = :uid")
    fun deleteMemberWhereUid(uid: Int)
}