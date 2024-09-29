package ru.shum.yandexapitest.presentation.schedule_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import ru.shum.yandexapitest.R
import ru.shum.yandexapitest.navigation.NavigationViewModel
import ru.shum.yandexapitest.util.DateConstants
import ru.shum.yandexapitest.util.getTransportImage
import ru.shum.yandexapitest.util.getTransportNameInRussian
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ScheduleDetailsScreen(
    navigationViewModel: NavigationViewModel,
    navHostController: NavHostController
) {
    val viewModel = koinViewModel<ScheduleDetailsViewModel>()
    val isLoading by viewModel.isLoading.collectAsState()
    val schedules by viewModel.schedules.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        navigationViewModel.scheduleRequest.value?.let { viewModel.getSchedule(context, it) }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .padding(
                        top = WindowInsets.systemBars
                            .asPaddingValues()
                            .calculateTopPadding()
                    )
                    .fillMaxWidth()
            ) {
                IconButton(
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.TopStart),
                    onClick = {
                        navHostController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(),
                    text = stringResource(R.string.schedule),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            }
        },
        content = { paddingValues ->
            if (isLoading) {
                Loading()
            } else {
                error?.let { message ->
                    Box(Modifier.fillMaxSize()) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = message,
                            color = Color.Red
                        )
                    }
                }

                schedules?.let {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(top = 15.dp)
                            .padding(horizontal = 10.dp)
                    ) {
                        itemsIndexed(it.segments) { _, item ->
                            ScheduleItem(
                                icon = getTransportImage(item.thread.transportType),
                                titleRoute = item.thread.title,
                                titleCompany = item.thread.carrier.title,
                                titleTransportType = getTransportNameInRussian(
                                    context,
                                    item.thread.transportType
                                ),
                                duration = item.duration,
                                fromStationName = item.from.title,
                                toStationName = item.to.title,
                                fromDate = extractFormattedDate(item.arrival),
                                toDate = extractFormattedDate(item.departure),
                                fromTime = extractFormattedTime(item.arrival),
                                toTime = extractFormattedTime(item.departure)
                            )
                        }
                    }

                }
            }
        }
    )
}

@Composable
private fun ScheduleItem(
    icon: Int,
    titleRoute: String,
    titleCompany: String,
    titleTransportType: String,
    fromDate: String,
    toDate: String,
    fromStationName: String,
    toStationName: String,
    fromTime: String,
    toTime: String,
    duration: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 5.dp),
                    text = titleRoute,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = titleCompany,
                    fontSize = 14.sp
                )

                Text(
                    text = titleTransportType,
                    fontSize = 14.sp
                )
            }

            Icon(
                modifier = Modifier
                    .size(34.dp)
                    .padding(end = 5.dp),
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Row(
            modifier = Modifier.padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            DurationItem(
                date = fromDate,
                time = fromTime,
                stationName = fromStationName
            )

            Text(
                modifier = Modifier
                    .weight(1f),
                text = convertSecondsToHoursAndMinutes(duration),
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            DurationItem(
                date = toDate,
                time = toTime,
                stationName = toStationName
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = 20.dp)
                .fillMaxWidth(),
            color = Color.Gray
        )
    }
}

@Composable
private fun DurationItem(
    date: String,
    time: String,
    stationName: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = date,
            fontSize = 14.sp,
            color = Color.Gray
        )

        Text(
            text = time,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Text(
            modifier = Modifier.width(120.dp),
            text = stationName,
            fontSize = 12.sp,
            color = Color.Gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Loading(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.Center)
        )
    }
}

private fun convertSecondsToHoursAndMinutes(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60

    return if (minutes > 0) {
        "${hours} ч ${minutes} м"
    } else {
        "${hours} ч"
    }
}

private fun extractFormattedDate(datetimeString: String): String {
    val inputFormat = SimpleDateFormat(DateConstants.RESPONSE_DATE_FORMAT, Locale.getDefault())
    val outputFormat = SimpleDateFormat(DateConstants.UI_DATE_FORMAT, Locale("ru"))
    val date = inputFormat.parse(datetimeString)
    return outputFormat.format(date!!)
}

private fun extractFormattedTime(datetimeString: String): String {
    val inputFormat = SimpleDateFormat(DateConstants.RESPONSE_DATE_FORMAT, Locale.getDefault())
    val outputFormat = SimpleDateFormat(DateConstants.UI_TIME_FORMAT, Locale.getDefault())
    val date = inputFormat.parse(datetimeString)
    return outputFormat.format(date!!)
}
