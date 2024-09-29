package ru.shum.domain.model

import ru.shum.common.enums.TransportType

data class ScheduleRequest (
    var from: String,
    var to: String,
    val date: String? = null,
    val transport: TransportType? = null
)