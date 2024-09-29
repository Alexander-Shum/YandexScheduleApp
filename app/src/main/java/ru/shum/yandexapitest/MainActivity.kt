package ru.shum.yandexapitest

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import ru.shum.common.enums.TransportType
import ru.shum.yandexapitest.navigation.AppHavHost
import ru.shum.yandexapitest.navigation.NavigationViewModel
import ru.shum.yandexapitest.navigation.ScheduleDetails
import ru.shum.yandexapitest.util.DateConstants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppHavHost()
        }
    }
}

@Composable
fun MainScreen(
    navigationViewModel: NavigationViewModel,
    navHostController: NavHostController
) {
    val mainViewModel = koinViewModel<MainViewModel>()
    val error by mainViewModel.error.collectAsState()

    if(error == null){
        Content(
            viewModel = mainViewModel,
            navigationViewModel = navigationViewModel,
            navHostController = navHostController
        )
    } else {
        Box(Modifier.padding(horizontal = 10.dp).fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = error ?: stringResource(R.string.unknown_error),
                color = Color.Red
            )
        }
    }
}

@Composable
private fun Content(
    viewModel: MainViewModel,
    navigationViewModel: NavigationViewModel,
    navHostController: NavHostController
) {
    val fromInput by viewModel.fromInput.collectAsState()
    val toInput by viewModel.toInput.collectAsState()
    val fromSuggestions by viewModel.fromSuggestions.collectAsState()
    val toSuggestions by viewModel.toSuggestions.collectAsState()
    var fromExpendedDropMenu by remember { mutableStateOf(false) }
    var toExpendedDropMenu by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.schedule_title),
            modifier = Modifier.padding(vertical = 15.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
        ) {
            Column {
                TextField(
                    value = fromInput,
                    onValueChange = { newText ->
                        viewModel.updateInput(newText, true)
                        fromExpendedDropMenu = newText.isNotEmpty() && fromSuggestions.isNotEmpty()
                    },
                    modifier = Modifier
                        .onFocusChanged { focusState ->
                            if (!focusState.isFocused) {
                                viewModel.getStationCode(
                                    fromInput,
                                    true
                                )
                            }
                        }
                        .fillMaxWidth(),
                    placeholder = { Text(text = stringResource(R.string.from)) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        viewModel.getStationCode(
                            fromInput,
                            true
                        )
                        keyboardController?.hide()
                    })
                )

                if (fromExpendedDropMenu) {
                    DropdownMenu(
                        expanded = true,
                        properties = PopupProperties(focusable = false),
                        onDismissRequest = { fromExpendedDropMenu = false }
                    ) {
                        fromSuggestions.forEach { suggestion ->
                            DropdownMenuItem(
                                text = {
                                    Text(text = suggestion.title)
                                },
                                onClick = {
                                    viewModel.selectSuggestion(suggestion, true)
                                    fromExpendedDropMenu = false
                                    keyboardController?.hide()
                                }
                            )
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            Column {
                TextField(
                    value = toInput,
                    onValueChange = { newText ->
                        viewModel.updateInput(newText, false)
                        toExpendedDropMenu = newText.isNotEmpty() && toSuggestions.isNotEmpty()
                    },
                    modifier = Modifier
                        .onFocusChanged { focusState ->
                            if (!focusState.isFocused) {
                                viewModel.getStationCode(
                                    toInput,
                                    false
                                )
                            }
                        }
                        .fillMaxWidth(),
                    placeholder = { Text(text = stringResource(R.string.to)) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        viewModel.getStationCode(
                            toInput,
                            false
                        )
                        keyboardController?.hide()
                    })
                )

                if (toExpendedDropMenu) {
                    DropdownMenu(
                        expanded = true,
                        properties = PopupProperties(focusable = false),
                        onDismissRequest = { toExpendedDropMenu = false }
                    ) {
                        toSuggestions.forEach { suggestion ->
                            DropdownMenuItem(
                                text = {
                                    Text(text = suggestion.title)
                                },
                                onClick = {
                                    viewModel.selectSuggestion(suggestion, false)
                                    toExpendedDropMenu = false
                                    keyboardController?.hide()
                                }
                            )
                        }
                    }
                }
            }
        }

        DateSelector(viewModel = viewModel)
        TransportSelector(viewModel = viewModel)

        Button(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
            onClick = {
                if (viewModel.scheduleRequest.value.from.isNotBlank() && viewModel.scheduleRequest.value.to.isNotBlank()) {
                    navigationViewModel.setScheduleRequest(viewModel.scheduleRequest.value)
                    navHostController.navigate(ScheduleDetails)
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.fill_all_fields),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        ) {
            Text(text = stringResource(R.string.find))
        }
    }
}

@Composable
private fun DateSelector(viewModel: MainViewModel) {
    var selectedOption by remember { mutableIntStateOf(0) } // 0 - Сегодня, 1 - Завтра, 2 - Дата
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    val context = LocalContext.current

    val options = listOf(
        stringResource(id = R.string.today),
        stringResource(id = R.string.tomorrow),
        stringResource(id = R.string.date)
    )

    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = {
                selectedOption = 0
                selectedDate = Calendar.getInstance()
                viewModel.updateScheduleRequestDate(selectedDate)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedOption == 0) Color.Gray else Color.LightGray
            )
        ) {
            Text(options[0])
        }

        Button(
            onClick = {
                selectedOption = 1
                selectedDate = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
                viewModel.updateScheduleRequestDate(selectedDate)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedOption == 1) Color.Gray else Color.LightGray
            )
        ) {
            Text(options[1])
        }

        Button(
            onClick = {
                selectedOption = 2
                datePicker(context) { date ->
                    selectedDate = date
                    viewModel.updateScheduleRequestDate(selectedDate)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedOption == 2) Color.Gray else Color.LightGray
            )
        ) {
            Text(
                text = if (selectedOption == 2)
                    SimpleDateFormat(DateConstants.DATE_FORMAT, Locale.getDefault()).format(
                        selectedDate.time
                    )
                else
                    options[2]
            )
            Icon(
                Icons.Default.DateRange,
                contentDescription = options[2],
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

private fun datePicker(context: Context, onDateSelected: (Calendar) -> Unit) {
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            onDateSelected(selectedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    datePickerDialog.datePicker.minDate = System.currentTimeMillis()

    datePickerDialog.show()
}

@Composable
private fun TransportSelector(viewModel: MainViewModel) {
    var selectedOption by remember { mutableStateOf<TransportType?>(null) }

    Row(
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                selectedOption = null
                viewModel.updateTransportType(selectedOption)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedOption == null) Color.Gray else Color.LightGray
            )
        ) {
            Text(stringResource(R.string.any))
        }

        TransportType.values().forEach { transportType ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        selectedOption = transportType
                        viewModel.updateTransportType(selectedOption)
                    }
                    .background(
                        if (selectedOption == transportType) Color.Gray else Color.LightGray,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    modifier = Modifier.align(Alignment.Center),
                    painter = when (transportType) {
                        TransportType.PLANE -> painterResource(R.drawable.ic_plane)
                        TransportType.TRAIN -> painterResource(R.drawable.ic_train)
                        TransportType.SUBURBAN -> painterResource(R.drawable.ic_electric_train)
                        TransportType.BUS -> painterResource(R.drawable.ic_bus)
                        TransportType.WATER -> painterResource(R.drawable.ic_water)
                        TransportType.HELICOPTER -> painterResource(R.drawable.ic_helicopter)
                    },
                    contentDescription = transportType.value,
                    tint = Color.Black
                )
            }
        }
    }
}
