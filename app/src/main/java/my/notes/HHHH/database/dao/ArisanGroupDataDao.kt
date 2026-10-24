package my.notes.HHHH.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import my.notes.HHHH.database.entity.GrupArisanModel


@Dao
interface ArisanGroupDataDao {
    @Update
    suspend fun updateData(model: GrupArisanModel)

    @Insert
    suspend fun insertData(model: GrupArisanModel)

    @Delete
    suspend fun deleteData(model: GrupArisanModel)

    @Query("SELECT * FROM  group_arisan_model ORDER BY uid ASC")
    fun allGroupArisan(): LiveData<List<GrupArisanModel>>

    @Query("DELETE FROM group_arisan_model WHERE status_dimulai = :status_dimulai")
    suspend fun deleteGroupWhereStatus(status_dimulai: Boolean)

    @Query("SELECT * FROM group_arisan_model WHERE grup_name = :group_name LIMIT 1")
    fun getDataWhereUid(group_name: String): LiveData<GrupArisanModel>

    @Query("UPDATE group_arisan_model SET diupdate = :diupdate, anggaran_terkumpul = :anggaran_terkumpul, jumlah_member = :jumlah_member WHERE uid = :groupId")
    suspend fun updateGroupWhere(diupdate: String, anggaran_terkumpul : Int, jumlah_member: Int, groupId: Int)

    @Query("UPDATE group_arisan_model SET diupdate = :diupdate, status_dimulai = :status_dimulai WHERE uid = :groupId")
    suspend fun updateGroupWhereStatus(diupdate: String, status_dimulai: Boolean, groupId: Int)

    @Query("SELECT * FROM group_arisan_model WHERE status_dimulai = :status_dimulai")
    fun getAllGroupWhereStatus(status_dimulai: Boolean): LiveData<List<GrupArisanModel>>

    @Query("DELETE FROM group_arisan_model WHERE status_dimulai = :status_dimulai")
    suspend fun deleteAllData(status_dimulai: Boolean)

    @Query("DELETE FROM group_arisan_model WHERE uid = :uidGroup")
    suspend fun deleteDataWhereUid(uidGroup: Int)
}