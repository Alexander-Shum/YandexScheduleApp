package ru.shum.data.model

import com.google.gson.annotations.SerializedName
import ru.shum.domain.model.IntervalSegment
import ru.shum.domain.model.Pagination
import ru.shum.domain.model.ScheduleResponse
import ru.shum.domain.model.Segment
import ru.shum.domain.model.Station
import  ru.shum.domain.model.Thread
import kotlinx.serialization.Serializable
import ru.shum.domain.model.Carrier

@Serializable
data class ScheduleResponseApi(
    val pagination: PaginationApi,
    @SerializedName("interval_segments") val intervalSegments: List<IntervalSegmentApi>,
    val segments: List<SegmentApi>
)

@Serializable
data class PaginationApi(
    val total: Int,
    val limit: Int,
    val offset: Int
)

@Serializable
data class IntervalSegmentApi(
    val from: StationApi,
    val to: StationApi,
    val thread: ThreadApi,
    val duration: Int
)

@Serializable
data class SegmentApi(
    val from: StationApi,
    val to: StationApi,
    val thread: ThreadApi,
    val arrival: String,
    val departure: String,
    val duration: Int
)

@Serializable
data class StationApi(
    val code: String,
    val title: String,
)

@Serializable
data class ThreadApi(
    val uid: String,
    val title: String,
    val number: String,
    val carrier: CarrierApi,
    @SerializedName("transport_type") val transportType: String
)

@Serializable
data class CarrierApi(
    val code: Int,
    val title: String,
    val url: String?,
    val contacts: String?,
    val phone: String?
)

fun ScheduleResponseApi.toDomain() = ScheduleResponse(
    pagination = pagination.toDomain(),
    segments = segments.map { it.toDomain() },
    intervalSegments = intervalSegments.map { it.toDomain() }
)

fun PaginationApi.toDomain() = Pagination(
    total = total,
    limit = limit,
    offset = offset
)

fun IntervalSegmentApi.toDomain() = IntervalSegment(
    from = from.toDomain(),
    to = to.toDomain(),
    thread = thread.toDomain(),
    duration = duration
)

fun ThreadApi.toDomain() = Thread(
    uid = uid,
    title = title,
    number = number,
    carrier = carrier.toDomain(),
    transportType = transportType,
)

fun CarrierApi.toDomain() = Carrier(
    code = code,
    title = title,
    url = url,
    contacts = contacts,
    phone = phone
)

fun SegmentApi.toDomain() = Segment(
    from = from.toDomain(),
    to = to.toDomain(),
    thread = thread.toDomain(),
    arrival = arrival,
    duration = duration,
    departure = departure
)

fun StationApi.toDomain() = Station(
    code = code,
    title = title
)

//fun TicketsInfoApi.toDomain() = TicketsInfo(
//    etMarker = etMarker,
//    places = places.map { it.toDomain() }
//)
//
//fun PlaceApi.toDomain() = Place(
//    currency = currency,
//    price = price.toDomain(),
//    name = name
//)
//
//fun PriceApi.toDomain() = Price(
//    whole = whole,
//    cents = cents
//)