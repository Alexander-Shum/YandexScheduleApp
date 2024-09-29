package ru.shum.data.repository

import ru.shum.common.enums.TransportType
import ru.shum.data.api.YandexApiService
import ru.shum.data.model.toDomain
import ru.shum.domain.model.ScheduleResponse
import ru.shum.domain.repository.ScheduleRepository
import java.io.IOException

class ScheduleRepositoryImpl(
    private val api: YandexApiService
) : ScheduleRepository {
    override suspend fun getSchedule(
        from: String,
        to: String,
        date: String?,
        transport: TransportType?
    ): Result<ScheduleResponse> {
        return try {
            val response = api.getSchedule(from = from, to = to, date = date, transportTypes = transport)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body.toDomain())
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error: ${e.message}", e))
        } catch (e: Exception) {
            Result.failure(Exception("An unexpected error occurred: ${e.message}", e))
        }
    }
}