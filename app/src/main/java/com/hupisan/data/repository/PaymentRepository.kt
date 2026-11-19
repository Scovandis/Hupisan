package com.hupisan.data.repository

import androidx.lifecycle.LiveData
import com.hupisan.database.dao.PaymentDao
import com.hupisan.database.entity.PaymentModel

class PaymentRepository(private val paymentDao: PaymentDao) {

    val dataFromUid: LiveData<List<PaymentModel>> = paymentDao.getDataPembayaran()

    suspend fun insertData(data: PaymentModel) {
        paymentDao.insertData(data)
    }

    fun getDataPembayaran(uid: Int): LiveData<List<PaymentModel>> {
        return paymentDao.getDataWhereUid(uid)
    }

    fun deleteDataFromUid(uid: Int) {
        paymentDao.deleteDataFromUid(uid)
    }
}
