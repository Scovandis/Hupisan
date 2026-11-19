package com.hupisan.data.repository

import androidx.lifecycle.LiveData
import com.hupisan.database.entity.PaymentModel
import com.hupisan.di.database.PaymentDB

class PaymentRepository(private val paymentDB: PaymentDB) {

    val payments: LiveData<List<PaymentModel>> = paymentDB.getDataPembayaran()
    val dataFromUid: LiveData<List<PaymentModel>> get() = payments

    suspend fun insertPayment(data: PaymentModel) {
        paymentDB.insert(data)
    }

    suspend fun insertData(data: PaymentModel) = insertPayment(data)

    fun getPaymentsByUid(uid: Int): LiveData<List<PaymentModel>> {
        return paymentDB.getDataWhereUid(uid)
    }

    fun getDataPembayaran(uid: Int): LiveData<List<PaymentModel>> = getPaymentsByUid(uid)

    suspend fun deletePaymentsByUid(uid: Int) {
        paymentDB.deleteDataFromUid(uid)
    }

    suspend fun deleteDataFromUid(uid: Int) = deletePaymentsByUid(uid)
}
