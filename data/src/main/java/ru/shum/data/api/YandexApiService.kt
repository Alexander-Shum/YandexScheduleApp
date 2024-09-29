package ru.shum.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import ru.shum.common.enums.TransportType
import ru.shum.data.BuildConfig
import ru.shum.data.model.ScheduleResponseApi

interface YandexApiService {
    @GET("search/")
    suspend fun getSchedule(
        @Query("apikey") apiKey: String = BuildConfig.YANDEX_API_KEY,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("date") date: String? = null,
        @Query("transport_types") transportTypes: TransportType? = null
    ): Response<ScheduleResponseApi>
}
