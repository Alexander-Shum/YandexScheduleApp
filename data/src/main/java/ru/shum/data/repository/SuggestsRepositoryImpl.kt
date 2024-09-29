package ru.shum.data.repository

import ru.shum.data.api.SuggestsApiService
import ru.shum.data.model.toDomain
import ru.shum.domain.model.SuggestionsResponse
import ru.shum.domain.repository.SuggestsRepository

class SuggestsRepositoryImpl(
    private val service: SuggestsApiService
): SuggestsRepository {
    override suspend fun getSuggestions(part: String): Result<SuggestionsResponse> {
        val response = service.getSuggestions(part = part)

        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Result.success(body.toDomain())
            } else {
                Result.failure(Exception("Response body is null"))
            }
        } else {
            Result.failure(Exception("Error: ${response.message()}"))
        }
    }
}