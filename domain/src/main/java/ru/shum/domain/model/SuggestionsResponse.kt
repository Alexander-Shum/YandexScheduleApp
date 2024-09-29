package ru.shum.domain.model

class SuggestionsResponse (
    val suggests: List<Suggestion>
)

data class Suggestion(
    val pointKey: String,
    val title: String,
    val titleRu: String,
)