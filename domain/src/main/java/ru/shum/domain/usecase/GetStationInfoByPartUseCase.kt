package ru.shum.domain.usecase

import ru.shum.domain.model.SuggestionsResponse
import ru.shum.domain.repository.SuggestsRepository

class GetStationInfoByPartUseCase(
    private val repository: SuggestsRepository
) {

    suspend operator fun invoke(part: String): Result<SuggestionsResponse> =
        repository.getSuggestions(part = part)
}