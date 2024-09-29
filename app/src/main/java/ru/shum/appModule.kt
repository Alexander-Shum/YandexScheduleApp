package ru.shum

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.shum.yandexapitest.MainViewModel
import ru.shum.yandexapitest.navigation.NavigationViewModel
import ru.shum.yandexapitest.presentation.schedule_details.ScheduleDetailsViewModel

val appModule = module {

    viewModel { MainViewModel(get()) }
    viewModel { ScheduleDetailsViewModel(get()) }
    viewModel { NavigationViewModel() }
}