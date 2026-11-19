package com.hupisan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.hupisan.data.repository.PaymentRepository
import com.hupisan.database.entity.PaymentModel

class PaymentViewModel(val repository: PaymentRepository) : ViewModel() {
    val dataPembayaran: LiveData<List<PaymentModel>> = repository.dataFromUid

    fun insertData(data: PaymentModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertData(data)
    }

    fun getDataPembayaranFromUid(uid: Int): LiveData<List<PaymentModel>> {
        return repository.getDataPembayaran(uid)
    }

    fun deleteDataFormUid(uid: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteDataFromUid(uid)
    }
}
