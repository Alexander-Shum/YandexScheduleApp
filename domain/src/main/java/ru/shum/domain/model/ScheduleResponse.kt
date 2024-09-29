package ru.shum.domain.model

data class ScheduleResponse(
    val pagination: Pagination,
    val intervalSegments: List<IntervalSegment>,
    val segments: List<Segment>
)

data class Pagination(
    val total: Int,
    val limit: Int,
    val offset: Int
)

data class IntervalSegment(
    val from: Station,
    val to: Station,
    val thread: Thread,
    val duration: Int
)

data class Segment(
    val from: Station,
    val to: Station,
    val thread: Thread,
    val arrival: String,
    val departure: String,
    val duration: Int
)

data class Station(
    val code: String,
    val title: String,
)

data class Thread(
    val uid: String,
    val title: String,
    val number: String,
    val carrier: Carrier,
    val transportType: String,
)
data class Carrier(
    val code: Int,
    val title: String,
    val url: String?,
    val contacts: String?,
    val phone: String?
)