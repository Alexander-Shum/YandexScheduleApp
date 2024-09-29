package ru.shum.yandexapitest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.shum.common.enums.TransportType
import ru.shum.domain.model.ScheduleRequest
import ru.shum.domain.model.Suggestion
import ru.shum.domain.usecase.GetStationInfoByPartUseCase
import ru.shum.yandexapitest.util.DateConstants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainViewModel(
    private val getStationInfoByPartUseCase: GetStationInfoByPartUseCase,
) : ViewModel() {

    private val _fromSuggestions = MutableStateFlow<List<Suggestion>>(emptyList())
    val fromSuggestions = _fromSuggestions.asStateFlow()

    private val _toSuggestions = MutableStateFlow<List<Suggestion>>(emptyList())
    val toSuggestions = _toSuggestions.asStateFlow()

    private val _fromInput = MutableStateFlow("")
    val fromInput = _fromInput.asStateFlow()

    private val _toInput = MutableStateFlow("")
    val toInput = _toInput.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var _scheduleRequest =
        MutableStateFlow(ScheduleRequest("", "", null, null))
    var scheduleRequest = _scheduleRequest.asStateFlow()

    init {
        updateScheduleRequestDate(Calendar.getInstance())
    }

    fun updateTransportType(type: TransportType?) {
        _scheduleRequest.value = _scheduleRequest.value.copy(transport = type)
    }

    fun updateScheduleRequestDate(calendar: Calendar) {
        val dateFormat = SimpleDateFormat(DateConstants.REQUEST_DATE_FORMAT, Locale.getDefault())
        _scheduleRequest.value = _scheduleRequest.value.copy(
            date = dateFormat.format(calendar.time)
        )
    }

    fun updateInput(input: String, isFrom: Boolean) {
        if (isFrom) {
            _fromInput.value = input
        } else {
            _toInput.value = input
        }
        getSuggestions(input, isFrom)
    }

    fun selectSuggestion(suggestion: Suggestion, isFrom: Boolean) {
        if (isFrom) {
            _scheduleRequest.value = _scheduleRequest.value.copy(
                from = suggestion.pointKey
            )
            _fromInput.value = suggestion.titleRu
            _fromSuggestions.value = emptyList()
        } else {
            _scheduleRequest.value = _scheduleRequest.value.copy(
                to = suggestion.pointKey
            )
            _toInput.value = suggestion.titleRu
            _toSuggestions.value = emptyList()
        }
    }

    fun getStationCode(input: String, isFrom: Boolean) {
        if (input.isEmpty()) return
        viewModelScope.launch {
            try {
                val result = getStationInfoByPartUseCase(input).getOrThrow()
                if (isFrom) {
                    if (result.suggests.isNotEmpty()) {
                        _scheduleRequest.value =
                            _scheduleRequest.value.copy(from = result.suggests[0].pointKey)
                        _fromInput.value = result.suggests[0].titleRu
                    }
                } else {
                    if (result.suggests.isNotEmpty()) {
                        _scheduleRequest.value =
                            _scheduleRequest.value.copy(to = result.suggests[0].pointKey)
                        _toInput.value = result.suggests[0].titleRu
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }


    private fun getSuggestions(part: String, isFrom: Boolean) {
        viewModelScope.launch {
            if (part.isEmpty()) {
                if (isFrom) _fromSuggestions.value = emptyList() else _toSuggestions.value =
                    emptyList()
                return@launch
            }

            try {
                getStationInfoByPartUseCase(part).onSuccess {
                    if (isFrom) {
                        _fromSuggestions.value = it.suggests
                    } else {
                        _toSuggestions.value = it.suggests
                    }
                }.onFailure {
                    _error.value = it.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
