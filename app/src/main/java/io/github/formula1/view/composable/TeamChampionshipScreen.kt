package io.github.formula1.view.composable

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.github.formula1.factory.TeamChampionshipViewModelFactory
import io.github.formula1.helper.ordinalOf
import io.github.formula1.helper.resolveTeamImage
import io.github.formula1.model.Resource
import io.github.formula1.model.dto.TeamChampionshipResponse
import io.github.formula1.repository.TeamChampionshipRepository
import io.github.formula1.view.TeamChampionshipViewModel

@Composable
fun TeamChampionshipScreen(navController: NavController) {
    val repository = TeamChampionshipRepository()
    val factory = TeamChampionshipViewModelFactory(repository)
    val viewModel: TeamChampionshipViewModel = viewModel(factory = factory)
    val teamChampionshipState = viewModel.teams.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchTeamChampionships()
    }

    when (val state = teamChampionshipState.value) {
        is Resource.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is Resource.Success -> {
            Column(modifier = Modifier.verticalScroll(state = rememberScrollState())) {
            Text(
                text = stringResource(io.github.formula1.R.string.constructors_championships),
                modifier = Modifier.padding(4.dp).fillMaxWidth(),
                style = TextStyle(
                    fontSize = 18.sp
                ),
                textAlign = TextAlign.Center
            )
                state.data?.forEach { team ->
                    TeamChampionshipListItem(team)
                }
            }
        }

        is Resource.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (state.message!!.contains(401.toString())) {
                    Text(text = stringResource(io.github.formula1.R.string.live_session_message))
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
                Text(text = stringResource(io.github.formula1.R.string.no_data))
            }
        }
    }
}

@Composable
fun TeamChampionshipListItem(team: TeamChampionshipResponse) {
    Row() {
        Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 8.dp)
            ) {
                Box() {
                    Image(
                        painter = painterResource(resolveTeamImage(team.team_name)),
                        contentDescription = stringResource(io.github.formula1.R.string.team_image),
                        modifier = Modifier.size(60.dp)
                    )
                }
                Text(
                    text = ordinalOf(team.position_current),
                    style = TextStyle(
                        fontStyle = FontStyle.Italic
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text(text = team.team_name, modifier = Modifier)
                Text("${stringResource(io.github.formula1.R.string.points)}: ${team.points_current}")
            }
        }
    }
}