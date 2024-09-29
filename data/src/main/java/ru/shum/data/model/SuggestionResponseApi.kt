package ru.shum.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import ru.shum.domain.model.Suggestion
import ru.shum.domain.model.SuggestionsResponse

data class SuggestionResponseApi(
    val suggests: List<SuggestionApi>
)

@Serializable
data class SuggestionApi (
    @SerializedName("point_key") val pointKey: String,
    @SerializedName("title") val title: String,
    @SerializedName("title_ru") val titleRu: String,
)

fun SuggestionResponseApi.toDomain() = SuggestionsResponse(
    suggests = suggests.map { it.toDomain() }
)

fun SuggestionApi.toDomain() = Suggestion (
    title = title,
    titleRu = titleRu,
    pointKey = pointKey,
)