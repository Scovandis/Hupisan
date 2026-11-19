package com.hupisan.data.repository

import androidx.lifecycle.LiveData
import com.hupisan.database.entity.PaymentModel
import com.hupisan.di.database.PaymentDB

class PaymentRepository(private val paymentDB: PaymentDB) {

    val dataFromUid: LiveData<List<PaymentModel>> = paymentDB.getDataPembayaran()

    suspend fun insertData(data: PaymentModel) {
        paymentDB.insert(data)
    }

    fun getDataPembayaran(uid: Int): LiveData<List<PaymentModel>> {
        return paymentDB.getDataWhereUid(uid)
    }

    suspend fun deleteDataFromUid(uid: Int) {
        paymentDB.deleteDataFromUid(uid)
    }
}
