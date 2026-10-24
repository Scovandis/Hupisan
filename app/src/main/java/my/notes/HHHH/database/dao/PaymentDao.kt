package my.notes.HHHH.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import my.notes.HHHH.database.entity.PaymentModel

@Dao
interface PaymentDao {
    @Insert
    suspend fun insertData(model: PaymentModel)

    @Query("SELECT * FROM data_pembayaran_model order by from_uid asc")
    fun getDataPembayaran(): LiveData<List<PaymentModel>>

    @Query("SELECT * FROM data_pembayaran_model WHERE from_uid = :id")
    fun getDataWhereUid(id: Int): LiveData<List<PaymentModel>>

    @Query("DELETE FROM data_pembayaran_model WHERE from_uid = :fromID")
    fun deleteDataFromUid(fromID: Int)
}
