package my.notes.HHHH.data.repository

import androidx.lifecycle.LiveData
import my.notes.HHHH.database.dao.DataPembayaranDao
import my.notes.HHHH.database.entity.DataPembayaranModel

class DataPembayaranRepository(private val dataPembayaranDao: DataPembayaranDao) {

    val dataFromUid: LiveData<List<DataPembayaranModel>> = dataPembayaranDao.getDataPembayaran()

    suspend fun insertData(data: DataPembayaranModel) {
        dataPembayaranDao.insertData(data)
    }

    fun getDataPembayaran(uid: Int): LiveData<List<DataPembayaranModel>> {
        return dataPembayaranDao.getDataWhereUid(uid)
    }

    fun deleteDataFromUid(uid: Int) {
        dataPembayaranDao.deleteDataFromUid(uid)
    }
}
