package io.github.formula1.view.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.github.formula1.factory.StartingGridViewModelFactory
import io.github.formula1.model.Resource
import io.github.formula1.model.dto.DriverResponse
import io.github.formula1.model.dto.StartingGridResponse
import io.github.formula1.repository.StartingGridRepository
import io.github.formula1.view.StartingGridViewModel
import java.sql.Driver

@Composable
fun StartingGridDetailScreen(navController: NavController, meetingKey: Int, drivers: List<DriverResponse>?)
{
    val startingGridRepository = StartingGridRepository()

    val startingGridFactory = StartingGridViewModelFactory(startingGridRepository)

    val startingGridViewModel: StartingGridViewModel = viewModel(factory = startingGridFactory)

    val polePosition = startingGridViewModel.polePosition.observeAsState()

    LaunchedEffect(Unit) {
        startingGridViewModel.fetchPolePosition(meetingKey, 1)
    }

    when (val state = polePosition.value) {
        is Resource.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is Resource.Success -> {
            val poleDriver = drivers!!.first() { driver ->
                driver.driver_number == state.data!!.first().driver_number
            }
            PolePositionScreen(poleDriver)
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
fun PolePositionScreen(poleDriver: DriverResponse)
{
    Row(modifier = Modifier.padding(8.dp)) {
        Text("Pole: ")
        Text(poleDriver.full_name)
    }
}