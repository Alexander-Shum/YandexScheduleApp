package ru.shum.domain.repository

import ru.shum.common.enums.TransportType
import ru.shum.domain.model.ScheduleResponse


interface ScheduleRepository {

    suspend fun getSchedule(
        from: String,
        to: String,
        date: String?,
        transport: TransportType?
    ): Result<ScheduleResponse>
}