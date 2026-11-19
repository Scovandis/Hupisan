package com.hupisan.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.hupisan.data.repository.ArisanGroupRepository
import com.hupisan.data.repository.ArisanRepository
import com.hupisan.data.repository.DataRepository
import com.hupisan.data.repository.PaymentRepository
import com.hupisan.database.db.ArisanModelDB
import com.hupisan.database.db.GroupArisanModelDB
import com.hupisan.database.db.ModelDB
import com.hupisan.database.db.PaymentDatabase
import com.hupisan.viewmodel.ArisanGroupViewModel
import com.hupisan.viewmodel.ArisanViewModel
import com.hupisan.viewmodel.DataViewModel
import com.hupisan.viewmodel.PaymentViewModel

val databaseModule = module {
    single { ModelDB.getDataBase(androidContext()) }
    single { get<ModelDB>().dataDAO() }

    single { PaymentDatabase.getDataBase(androidContext()) }
    single { get<PaymentDatabase>().paymentDao() }

    single { ArisanModelDB.getDataBase(androidContext()) }
    single { get<ArisanModelDB>().arisanDao() }

    single { GroupArisanModelDB.getDataBase(androidContext()) }
    single { get<GroupArisanModelDB>().GroupArisanDao() }
}

val repositoryModule = module {
    single { DataRepository(get()) }
    single { PaymentRepository(get()) }
    single { ArisanRepository(get()) }
    single { ArisanGroupRepository(get()) }
}

val viewModelModule = module {
    viewModel { DataViewModel(get()) }
    viewModel { PaymentViewModel(get()) }
    viewModel { ArisanViewModel(get()) }
    viewModel { ArisanGroupViewModel(get()) }
}

val appModules = listOf(
    databaseModule,
    repositoryModule,
    viewModelModule
)
