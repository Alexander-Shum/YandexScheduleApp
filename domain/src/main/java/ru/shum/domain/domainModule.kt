package ru.shum.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.shum.domain.usecase.GetStationInfoByPartUseCase
import ru.shum.domain.usecase.GetScheduleUseCase


val domainModule = module {
    singleOf(::GetScheduleUseCase)
    singleOf(::GetStationInfoByPartUseCase)
}