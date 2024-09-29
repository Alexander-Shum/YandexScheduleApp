package ru.shum.domain.repository

import ru.shum.domain.model.SuggestionsResponse

interface SuggestsRepository {

    suspend fun getSuggestions(part: String): Result<SuggestionsResponse>
}