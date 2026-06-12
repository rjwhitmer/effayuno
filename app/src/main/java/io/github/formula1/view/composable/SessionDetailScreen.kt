package io.github.formula1.view.composable

import android.graphics.Color.parseColor
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.github.formula1.factory.DriverViewModelFactory
import io.github.formula1.factory.SessionViewModelFactory
import io.github.formula1.factory.StartingGridViewModelFactory
import io.github.formula1.helper.convertLapTime
import io.github.formula1.helper.ordinalOf
import io.github.formula1.model.Resource
import io.github.formula1.model.dto.DriverChampionship
import io.github.formula1.model.dto.DriverResponse
import io.github.formula1.model.dto.QualifierSessionResult
import io.github.formula1.model.dto.RaceSessionResult
import io.github.formula1.model.dto.StartingGridResponse
import io.github.formula1.repository.DriverRepository
import io.github.formula1.repository.SessionRepository
import io.github.formula1.repository.StartingGridRepository
import io.github.formula1.view.DriverViewModel
import io.github.formula1.view.SessionViewModel
import io.github.formula1.view.StartingGridViewModel
import kotlinx.datetime.format.DateTimeFormat
import java.text.SimpleDateFormat
import kotlin.time.Duration
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

@Composable
fun SessionDetailScreen(navController: NavController, sessionKey: Int, meetingKey: Int, sessionType: String?, sessionName: String) {
    val driverRepository = DriverRepository()
    val sessionRepository = SessionRepository()
    val startingGridRepository = StartingGridRepository()

    val driverFactory = DriverViewModelFactory(driverRepository)
    val sessionFactory = SessionViewModelFactory(sessionRepository)
    val startingGridFactory = StartingGridViewModelFactory(startingGridRepository)

    val driverViewModel: DriverViewModel = viewModel(factory = driverFactory)
    val sessionViewModel: SessionViewModel = viewModel(factory = sessionFactory)
    val startingGridViewModel: StartingGridViewModel = viewModel(factory = startingGridFactory)

    val driverState = driverViewModel.drivers.observeAsState()
    val qualifyingSessionResult = sessionViewModel.qualifyingSessions.observeAsState()
    val raceSessionResult = sessionViewModel.raceSessions.observeAsState()
    val polePosition = startingGridViewModel.polePosition.observeAsState()

    LaunchedEffect(Unit) {
        driverViewModel.fetchDriversSessionSpecific(sessionKey, meetingKey)
        if (sessionType == "Race") {
            sessionViewModel.fetchRaceSessionResult(meetingKey = meetingKey, sessionKey = sessionKey)
        } else if (sessionType == "Qualifying") {
            sessionViewModel.fetchQualifyingSessionResult(meetingKey = meetingKey, sessionKey = sessionKey)
        }
    }

    when (val state = driverState.value) {
        is Resource.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is Resource.Success -> {
            val qualifyingState = qualifyingSessionResult.value
            val raceState = raceSessionResult.value
//            val polePositionState = polePosition.value
            if (sessionType == "Race") {
                RaceSessionResultScreen(raceState?.data, state.data, sessionName)
            } else {
                QualifyingSessionResultScreen(qualifyingState?.data, state.data, sessionName)
            }

        }

        is Resource.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (state.message!!.contains(401.toString())) {
                    Text(text = "Live session in progress. Try again later.")
                } else {
                    Text(text = state.message)
                }
            }
        }

        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No data")
            }
        }
    }
}

@Composable
fun QualifyingSessionResultScreen(results: List<QualifierSessionResult>?, drivers: List<DriverResponse>?, sessionTitle: String) {
    Column(modifier = Modifier.verticalScroll(state = rememberScrollState())) {
        Text(
            text = "$sessionTitle Results",
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            style = TextStyle(
                fontSize = 18.sp
            ),
            textAlign = TextAlign.Center
        )
        results?.forEach { result ->
            var driver = drivers?.first {
                it.driver_number.toInt() == result.driver_number
            }
            Row() {
                QualifyingSessionDriver(driver, result)
            }
        } ?: Text("No results found")
    }
}

@Composable
fun RaceSessionResultScreen(results: List<RaceSessionResult>?, drivers: List<DriverResponse>?, sessionTitle: String) {
//    val driverOnPole = drivers?.first { driver ->
//        if (!polePosition.isNullOrEmpty()) {
//            driver.driver_number.toInt() == polePosition?.first()?.driver_number
//        } else {
//            driver.driver_number == 12
//        }
//    }
    Column(modifier = Modifier.verticalScroll(state = rememberScrollState())) {
        Text(
            text = "$sessionTitle Results",
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            style = TextStyle(
                fontSize = 18.sp
            ),
            textAlign = TextAlign.Center
        )
//        Box()
//        {
//            Text(
//                text = "Pole Position",
//                modifier = Modifier.padding(8.dp).fillMaxWidth(),
//            )
//            Text(
//                text = driverOnPole?.full_name ?: ""
//            )
//        }
        results?.forEach { result ->
            val driver = drivers?.first { driver ->
                driver.driver_number.toInt() == result.driver_number
            }
            Row() {
                RaceSessionDriver(driver, result)
            }
        } ?: Text("No results found")
    }
}

@Composable
fun RaceSessionDriver(driver: DriverResponse?, sessionResult: RaceSessionResult) {
    var isExpanded by rememberSaveable { mutableStateOf(false)}
    val teamColor: Number = parseColor("#${driver?.team_colour}")
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(teamColor as Int)
        ),
        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { isExpanded = !isExpanded },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
    ) {
        Column() {
            Row() {
                Box(
                    modifier = Modifier.padding(10.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    driver?.full_name?.let {
                        Text(
                            text = it,
                            style = TextStyle(
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Row() {
                        Box(
                            modifier = Modifier.offset(x = -(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (sessionResult.gap_to_leader is Double && sessionResult.gap_to_leader > 0) {
                                Text(text = "-${convertLapTime(sessionResult.gap_to_leader)}")
                            } else if (sessionResult.gap_to_leader is String) {
                                Text(text = sessionResult.gap_to_leader)
                            }
                        }
                        var sessionResultPosition = if (sessionResult.position > 0) ordinalOf(sessionResult.position) else "DNF"
                        Text(text = sessionResultPosition)
                    }
                }
            }
        }
    }
}

@Composable
fun QualifyingSessionDriver(driver: DriverResponse?, sessionResult: QualifierSessionResult) {
    var isExpanded by rememberSaveable { mutableStateOf(false)}
    val teamColor: Number = parseColor("#${driver?.team_colour}")
    val q1Duration: String = convertLapTime(sessionResult.duration[0])
    val q2Duration: String = convertLapTime(sessionResult.duration[1])
    val q3Duration: String = convertLapTime(sessionResult.duration[2])
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(teamColor as Int)
        ),
        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { isExpanded = !isExpanded },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
    ) {
        Column() {
            Row() {
                Box(
                    modifier = Modifier.padding(10.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    driver?.full_name?.let {
                        Text(
                            text = it,
                            style = TextStyle(
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Row() {
                        Box(
                            modifier = Modifier.offset(x = -(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (q3Duration != "DNP") {
                                Text(text = q3Duration)
                            } else if (q2Duration != "DNP") {
                                Text(text = q2Duration)
                            } else {
                                Text(text = q1Duration)
                            }
                        }
                        Text(text = ordinalOf(sessionResult.position))
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = isExpanded
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row() {
                    Text(text = "Q1: ")
                    Text(text = q1Duration)
                }
                Row() {
                    Text(text = "Q2: ")
                    Text(text = q2Duration)
                }
                Row() {
                    Text(text = "Q3: ")
                    Text(text = q3Duration)
                }
            }
        }
    }
}