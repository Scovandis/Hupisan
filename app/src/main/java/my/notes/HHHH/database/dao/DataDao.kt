package my.notes.HHHH.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Query
import my.notes.HHHH.database.entity.DataModel


@Dao
interface DataDao {
    @Update
    suspend fun updateData(model: DataModel)

    @Insert
    suspend fun insertData(model: DataModel)

    @Delete
    suspend fun deleteData(model: DataModel)

    @Query("SELECT * FROM data_model order by model_id ASC")
    fun getAllData(): LiveData<List<DataModel>>

    @Query("SELECT * FROM data_model WHERE model_id = :id LIMIT 1")
    fun getDataWhereUid(id: Int): LiveData<DataModel>

    @Query("UPDATE data_model SET jumlah = :jumlah, statusData = :statusData, tanggal_lunas = :tanggal_lunas, dibayar = :dibayar WHERE model_id = :id")
    suspend fun updateDataJumlah(jumlah: Int, id: Int, statusData: Boolean, tanggal_lunas: String, dibayar: Int)

    @Query("SELECT * FROM data_model WHERE statusData = :statusData ORDER BY model_id DESC")
    fun getAllDataWhereStatusLunas(statusData: Boolean): LiveData<List<DataModel>>

    @Query("SELECT * FROM data_model WHERE statusData = :statusData and jenis = :jenis")
    fun getAllDataWhereStatusData(statusData: Boolean, jenis: Boolean): LiveData<List<DataModel>>

    @Query("SELECT * FROM data_model WHERE diBuat = :tanggalDibuat")
    fun getAllDataNewFromDate(tanggalDibuat: String): LiveData<List<DataModel>>

    @Query("SELECT * FROM data_model WHERE statusData = :statusData ORDER BY model_id DESC LIMIT :limit")
    fun loadDataWhereLimit(statusData: Boolean, limit: Int): LiveData<List<DataModel>>

    @Query("SELECT * FROM data_model WHERE jenis = :jenis")
    fun getAllDataWhereJenis(jenis: Boolean): LiveData<List<DataModel>>

    @Query("DELETE FROM data_model WHERE jenis = :jenis and statusData = :statusData")
    fun deleteAllData(jenis: Boolean, statusData: Boolean)
}