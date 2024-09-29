package ru.shum.yandexapitest.navigation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.shum.domain.model.ScheduleRequest

class NavigationViewModel: ViewModel() {

    private val _scheduleRequest = MutableStateFlow<ScheduleRequest?>(null)
    val scheduleRequest: StateFlow<ScheduleRequest?> get() = _scheduleRequest.asStateFlow()

    fun setScheduleRequest(scheduleRequest: ScheduleRequest) {
        _scheduleRequest.value = scheduleRequest
    }
}