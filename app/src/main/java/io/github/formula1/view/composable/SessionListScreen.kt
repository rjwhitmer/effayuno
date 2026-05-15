package io.github.formula1.view.composable

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.github.formula1.R
import io.github.formula1.factory.MeetingViewModelFactory
import io.github.formula1.factory.SessionViewModelFactory
import io.github.formula1.model.Resource
import io.github.formula1.model.dto.MeetingResponse
import io.github.formula1.model.dto.SessionResponse
import io.github.formula1.repository.MeetingRepository
import io.github.formula1.repository.SessionRepository
import io.github.formula1.service.navigation.Route
import io.github.formula1.view.MeetingViewModel
import io.github.formula1.view.SessionViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Calendar
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionListScreen(navController: NavController) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (2023..currentYear).map { year -> year.toString() }.reversed()
    var selectedYear = remember { mutableStateOf(currentYear.toString()) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedYear.value,
                onValueChange = { value -> selectedYear.value = value },
                readOnly = true,
                label = { Text("Year") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                years.forEach { year ->
                    DropdownMenuItem(
                        text = { Text(year) },
                        onClick = {
                            selectedYear.value = year
                            expanded = false
                        }
                    )
                }
            }
        }
        SessionListScreenComponent(navController, selectedYear.value)
    }
}

@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
@Composable
fun SessionListScreenComponent(navController: NavController, currentYear: String) {
    val sessionRepository = SessionRepository()
    val sessionFactory = SessionViewModelFactory(sessionRepository)
    val sessionViewModel: SessionViewModel = viewModel(factory = sessionFactory)
    val sessionState = sessionViewModel.sessions.observeAsState()

    val meetingRepository = MeetingRepository()
    val meetingFactory = MeetingViewModelFactory(meetingRepository)
    val meetingViewModel: MeetingViewModel = viewModel(factory = meetingFactory)
    val meetingState = meetingViewModel.meetings.observeAsState()

    LaunchedEffect(currentYear) {
        sessionViewModel.fetchSessions(currentYear)
        meetingViewModel.fetchMeetings(currentYear)
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
            val sprintRaces: List<SessionResponse> = state.data!!.filter {
                it.session_name == "Sprint"
            }
            val pastRaces: List<SessionResponse> = state.data!!.filter {
                it.session_name == "Race"
            }

            var weekendSessions: List<SessionResponse> = emptyList()

            Column (modifier = Modifier.verticalScroll(state = rememberScrollState())) {
                pastRaces.forEach { session ->
                    val meeting: List<MeetingResponse>? = meetingState.value?.data?.filter { data ->
                        data.meeting_key == session.meeting_key
                    }
                    weekendSessions = state.data!!.filter {
                        session.meeting_key == it.meeting_key
                    }
                    if(sprintRaces.any { sprint ->
                        session.meeting_key == sprint.meeting_key
                    }) {
                        session.is_sprint_weekend = true
                    }
                    MeetingComponent(session, navController, weekendSessions, meeting?.first())
                }
            }
        }

        is Resource.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (state.message!!.contains(401.toString())) {
                    Text(text = stringResource(R.string.live_session_message))
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