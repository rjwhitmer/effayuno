package io.github.formula1.view.composable

import android.se.omapi.Session
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.github.formula1.R
import io.github.formula1.factory.MeetingViewModelFactory
import io.github.formula1.factory.SessionViewModelFactory
import io.github.formula1.helper.countryCodeToEmojiFlag
import io.github.formula1.helper.formatTrackTime
import io.github.formula1.helper.getCountryCode
import io.github.formula1.model.Resource
import io.github.formula1.model.dto.SessionResponse
import io.github.formula1.repository.MeetingRepository
import io.github.formula1.repository.SessionRepository
import io.github.formula1.service.navigation.Route
import io.github.formula1.view.MeetingViewModel
import io.github.formula1.view.SessionViewModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.format
import kotlinx.datetime.toLocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun SessionComponent(navController: NavController) {
    val sessionRepository = SessionRepository()
    val sessionFactory = SessionViewModelFactory(sessionRepository)
    val sessionViewModel: SessionViewModel = viewModel(factory = sessionFactory)
    val sessionState = sessionViewModel.sessions.observeAsState()
    val currentDateTime = Clock.System.now()

    LaunchedEffect(Unit) {
        sessionViewModel.fetchSessions(currentDateTime.toLocalDateTime(TimeZone.currentSystemDefault()).year.toString())
    }

    when (val state = sessionState.value) {
        is Resource.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is Resource.Success -> {
            var futureSessions: List<SessionResponse> = state.data!!.filter {
                kotlin.time.Instant.parse(it.date_start) > kotlin.time.Instant.parse(currentDateTime.toString()) && !it.is_cancelled
            }
            futureSessions = futureSessions.filter {
                it.session_type != "Practice"
            }
            var isExpanded by rememberSaveable { mutableStateOf(false) }
            val race = futureSessions.filter {
                it.session_name == "Race"
            }
            SessionItemHomepage(futureSessions[0], navController)
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                val sessionText: String = if (isExpanded) stringResource(R.string.hide_next_session) else stringResource(R.string.next_session_link)
                Text(
                    text = sessionText,
                    modifier = Modifier.padding(end = 8.dp).clickable { isExpanded = !isExpanded },
                    textAlign = TextAlign.End,
                    style = androidx.compose.ui.text.TextStyle(
                        fontStyle = FontStyle.Normal,
                        fontSize = 15.sp
                    )
                )
            }
            Row() {
                AnimatedVisibility(visible = isExpanded) {
                    Column()
                    {
                        if (futureSessions[1].session_name != "Race") {
                            SessionItemHomepage(futureSessions[1], navController)
                        }
                        SessionItemHomepage(race.first(), navController)
                    }
                }
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

@OptIn(ExperimentalTime::class)
@Composable
fun SessionItemHomepage(session: SessionResponse?, navController: NavController) {
    val utcOffset = UtcOffset(8, 0)
    val sessionTime = kotlin.time.Instant.parse(session!!.date_start)
    val trackTime = OffsetDateTime.parse(formatTrackTime(sessionTime, utcOffset)).toString()
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    val displayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a")
    val futureRace: Boolean = sessionTime > Clock.System.now()

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
            ) {
                Column(
                ) {
                    Text(text = session.session_name)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = displayFormatter.format(
                            OffsetDateTime.parse(trackTime)
                                .atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
                        ).toString()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Column(
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.End),
                        text = session.circuit_short_name
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(text = countryCodeToEmojiFlag(getCountryCode(session.country_name)))
                        Text(text = session.country_name)
                    }
                }
            }
        }
    }
}