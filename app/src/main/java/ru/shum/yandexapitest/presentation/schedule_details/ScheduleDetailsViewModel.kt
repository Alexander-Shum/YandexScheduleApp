package ru.shum.yandexapitest.presentation.schedule_details

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.shum.domain.model.ScheduleRequest
import ru.shum.domain.model.ScheduleResponse
import ru.shum.domain.usecase.GetScheduleUseCase
import ru.shum.yandexapitest.R

class ScheduleDetailsViewModel(
    private val getScheduleUseCase: GetScheduleUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _schedules = MutableStateFlow<ScheduleResponse?>(null)
    val schedules = _schedules.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun getSchedule(context: Context, request: ScheduleRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            getScheduleUseCase(request).onSuccess {
                _schedules.value = it
                if (it.pagination.total == 0) {
                    _error.value = context.getString(R.string.not_found)
                } else {
                    _error.value = null
                }
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }
}