package my.notes.HHHH.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import my.notes.HHHH.data.repository.ArisanGroupRepository
import my.notes.HHHH.data.repository.ArisanRepository
import my.notes.HHHH.data.repository.DataRepository
import my.notes.HHHH.data.repository.PaymentRepository
import my.notes.HHHH.database.db.ArisanModelDB
import my.notes.HHHH.database.db.GroupArisanModelDB
import my.notes.HHHH.database.db.ModelDB
import my.notes.HHHH.database.db.PaymentDatabase
import my.notes.HHHH.viewmodel.ArisanGroupViewModel
import my.notes.HHHH.viewmodel.ArisanViewModel
import my.notes.HHHH.viewmodel.DataViewModel
import my.notes.HHHH.viewmodel.PaymentViewModel

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
