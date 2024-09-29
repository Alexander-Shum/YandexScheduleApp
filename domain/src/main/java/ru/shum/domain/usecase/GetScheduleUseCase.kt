package ru.shum.domain.usecase

import ru.shum.domain.model.ScheduleRequest
import ru.shum.domain.repository.ScheduleRepository

class GetScheduleUseCase(
    private val repository: ScheduleRepository
) {

    suspend operator fun invoke(
        scheduleRequest: ScheduleRequest
    ) = repository.getSchedule(
        from = scheduleRequest.from,
        to = scheduleRequest.to,
        date = scheduleRequest.date,
        transport = scheduleRequest.transport
    )
}