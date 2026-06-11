package io.github.formula1.view.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import io.github.formula1.R
import io.github.formula1.factory.MeetingViewModelFactory
import io.github.formula1.helper.countryCodeToEmojiFlag
import io.github.formula1.helper.formatTrackTime
import io.github.formula1.helper.getCountryCode
import io.github.formula1.model.Resource
import io.github.formula1.model.dto.MeetingResponse
import io.github.formula1.model.dto.SessionResponse
import io.github.formula1.repository.MeetingRepository
import io.github.formula1.view.MeetingViewModel
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.UtcOffset
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun MeetingComponent(session: SessionResponse, navController: NavController, weekendSessions: List<SessionResponse>? = null, meeting: MeetingResponse?) {
    MeetingItem(session, navController, weekendSessions, meeting)
}

@OptIn(ExperimentalTime::class)
@Composable
fun MeetingItem(session: SessionResponse, navController: NavController, weekendSessions: List<SessionResponse>?, meeting: MeetingResponse?) {
    val utcOffset = UtcOffset(8, 0)
    val sessionTime = kotlin.time.Instant.parse(session!!.date_start)
    val trackTime = OffsetDateTime.parse(formatTrackTime(sessionTime, utcOffset)).toString()
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val currentTime = Clock.System.now()

    val displayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")
    val futureSession: Boolean = sessionTime > currentTime

    Spacer(modifier = Modifier.height(10.dp))
    Card(
        modifier = Modifier.fillMaxWidth().clickable { isExpanded = !isExpanded }.padding(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                horizontalArrangement = Arrangement.Absolute.SpaceBetween
            ){
                Column(
                ) {
                    Text(text = "${meeting?.meeting_name}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = displayFormatter.format(OffsetDateTime.parse(trackTime).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()).toString())
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Column(
                ) {
                    Text(modifier = Modifier.align(Alignment.End), text = session.circuit_short_name)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.align(Alignment.End)) {
                        Text(text = countryCodeToEmojiFlag(getCountryCode(session.country_name)))
                        Text(text = session.country_name)
                    }
                }
            }
        }
        Row() {
            var sprintQualifying = SessionResponse()
            var qualifying = SessionResponse()
            var sprintRace = SessionResponse()
            weekendSessions?.forEach { session ->
                when (session.session_name) {
                    "Sprint Qualifying" -> {
                        sprintQualifying = session
                    }
                    "Qualifying" -> {
                        qualifying = session
                    }
                    "Sprint" -> {
                        sprintRace = session
                    }
                }
            }

            var formattedSprintQualifyingTrackTime = ""
            var formattedSprintRaceTrackTime = ""
            var formattedQualifyingTrackTime = ""
            if (sprintQualifying.session_name.isNotEmpty()) {
                val sprintQualifyingTrackTime = kotlin.time.Instant.parse(sprintQualifying.date_start)
                formattedSprintQualifyingTrackTime = OffsetDateTime.parse(formatTrackTime(sprintQualifyingTrackTime, utcOffset)).toString()
            }

            if (sprintRace.session_name.isNotEmpty()) {
                val sprintRaceTrackTime = kotlin.time.Instant.parse(sprintRace.date_start)
                formattedSprintRaceTrackTime = OffsetDateTime.parse(formatTrackTime(sprintRaceTrackTime, utcOffset)).toString()
            }

            if (qualifying.session_name.isNotEmpty()) {
                val qualifyingTrackTime = kotlin.time.Instant.parse(qualifying.date_start)
                formattedQualifyingTrackTime = OffsetDateTime.parse(formatTrackTime(qualifyingTrackTime, utcOffset)).toString()
            }

            if (meeting?.is_cancelled == true) {
                AnimatedVisibility(visible = isExpanded) {
                    Text(text = stringResource(R.string.cancel_race_weekend), modifier = Modifier.padding(8.dp))
                }
            } else {
                AnimatedVisibility(visible = isExpanded) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        MeetingInfo(meeting)
                        if (session.is_sprint_weekend && kotlin.time.Instant.parse(sprintQualifying.date_start) < currentTime) {
                            Box(modifier = Modifier.clickable { navController.navigate(route = sprintQualifying) }) {
                                Text(text = stringResource(R.string.sprint_qualifying_results), modifier = Modifier.fillMaxWidth())
                            }
                        } else if (session.is_sprint_weekend) {
                            Box() {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = stringResource(R.string.sprint_qualifying))
                                    Text(text = displayFormatter.format(OffsetDateTime.parse(formattedSprintQualifyingTrackTime).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()).toString())
                                }
                            }
                        }
                        Spacer(modifier = Modifier.padding(4.dp))
                        if (kotlin.time.Instant.parse(qualifying.date_start) < currentTime) {
                            Box(modifier = Modifier.clickable { navController.navigate(route = qualifying) }) {
                                Text(text = stringResource(R.string.qualifying_results), modifier = Modifier.fillMaxWidth())
                            }
                        } else {
                            Box() {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = stringResource(R.string.qualifying))
                                    Text(text = displayFormatter.format(OffsetDateTime.parse(formattedQualifyingTrackTime).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()).toString())
                                }
                            }
                        }
                        Spacer(modifier = Modifier.padding(4.dp))
                        if (session.is_sprint_weekend && kotlin.time.Instant.parse(sprintRace.date_start) < currentTime) {
                            Box(modifier = Modifier.clickable { navController.navigate(route = sprintRace) }) {
                                Text(text = stringResource(R.string.sprint_race_results), modifier = Modifier.fillMaxWidth())
                            }
                        } else if (session.is_sprint_weekend) {
                            Box() {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = stringResource(R.string.sprint_race))
                                    Text(text = displayFormatter.format(OffsetDateTime.parse(formattedSprintRaceTrackTime).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()).toString())
                                }
                            }
                        }
                        Spacer(modifier = Modifier.padding(4.dp))
                        if (!futureSession) {
                            Box(modifier = Modifier.clickable { navController.navigate(route = session) }) {
                                Text(text = stringResource(R.string.race_results), modifier = Modifier.fillMaxWidth())
                            }
                        } else {
                            Box() {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = stringResource(R.string.race))
                                    Text(text = displayFormatter.format(OffsetDateTime.parse(trackTime).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()).toString())
                                }
                            }
                        }
                        Spacer(modifier = Modifier.padding(4.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MeetingInfo(meeting: MeetingResponse?) {
    Row() {
        Column() {
            Text(text = "${meeting?.meeting_name}")
            Text(
                text = "${meeting?.circuit_type}",
                style = TextStyle(
                    fontStyle = FontStyle.Italic
                )
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Image(
                painter = rememberAsyncImagePainter(meeting?.circuit_image),
                contentDescription = meeting?.circuit_short_name,
                modifier = Modifier.size(80.dp)
            )
        }
    }
}