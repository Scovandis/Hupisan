package com.hupisan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.hupisan.data.repository.PaymentRepository
import com.hupisan.database.entity.PaymentModel

class PaymentViewModel(val repository: PaymentRepository) : ViewModel() {
    val payments: LiveData<List<PaymentModel>> = repository.payments
    val dataPembayaran: LiveData<List<PaymentModel>> get() = payments

    fun insertPayment(data: PaymentModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertPayment(data)
    }

    fun insertData(data: PaymentModel) = insertPayment(data)

    fun getPaymentsByUid(uid: Int): LiveData<List<PaymentModel>> {
        return repository.getPaymentsByUid(uid)
    }

    fun getDataPembayaranFromUid(uid: Int): LiveData<List<PaymentModel>> = getPaymentsByUid(uid)

    fun deletePaymentsByUid(uid: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.deletePaymentsByUid(uid)
    }

    fun deleteDataFormUid(uid: Int) = deletePaymentsByUid(uid)
}
