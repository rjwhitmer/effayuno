package io.github.formula1.view.composable

import android.graphics.Color.parseColor
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import io.github.formula1.factory.DriverViewModelFactory
import io.github.formula1.helper.resolveTeamImage
import io.github.formula1.model.Resource
import io.github.formula1.model.dto.DriverResponse
import io.github.formula1.repository.DriverRepository
import io.github.formula1.view.DriverViewModel

@Composable
fun DriverListScreen(navController: NavController) {
    val repository = DriverRepository()
    val factory = DriverViewModelFactory(repository)
    val viewModel: DriverViewModel = viewModel(factory = factory)
    val sessionState = viewModel.drivers.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchDrivers()
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
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                val teamDrivers: MutableMap<String, MutableList<DriverResponse>> = mutableMapOf()
                state!!.data?.forEach { driverResponse ->
                    teamDrivers.getOrPut(driverResponse.team_name) { mutableListOf() }.add(driverResponse)
                }
                teamDrivers.values.forEach { team ->
                    TeamDrivers(team.first().team_name, team)
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

@Composable
fun TeamDrivers(teamName: String, drivers: MutableList<DriverResponse>) {
    val teamColor: Number = parseColor("#${drivers.first()?.team_colour}")
    var isExpanded by rememberSaveable { mutableStateOf(false)}
    Row() {
            Card(
                colors = CardDefaults.cardColors(
                containerColor = Color(teamColor as Int)
                ),
                modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { isExpanded = !isExpanded },
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(resolveTeamImage(teamName)),
                        contentDescription = "Team Image",
                        modifier = Modifier.size(80.dp)
                    )
                }
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    drivers.forEach { driver ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = rememberAsyncImagePainter(driver?.headshot_url),
                                contentDescription = "Driver image",
                                modifier = Modifier.size(80.dp).padding(top = 8.dp)
                            )
                            Text(
                                text = "${driver.full_name} (${driver.driver_number})",
                                modifier = Modifier.padding(8.dp),
                                style = TextStyle(
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }
        }
    }
}