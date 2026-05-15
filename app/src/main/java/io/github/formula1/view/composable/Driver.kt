package io.github.formula1.view.composable

import android.R.attr.fraction
import android.graphics.Color.parseColor
import android.util.Log
import android.widget.ImageView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.FontScaling
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ComponentRegistry
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.load
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import coil3.target.ImageViewTarget
import io.github.formula1.R
import io.github.formula1.factory.DriverViewModelFactory
import io.github.formula1.helper.ordinalOf
import io.github.formula1.helper.resolveTeamImage
import io.github.formula1.model.Resource
import io.github.formula1.model.dto.DriverResponse
import io.github.formula1.repository.DriverRepository
import io.github.formula1.view.DriverViewModel
import kotlin.collections.forEach
import kotlin.math.absoluteValue

@Composable
fun DriversScreen() {
    val repository = DriverRepository()
    val factory = DriverViewModelFactory(repository)
    val viewModel: DriverViewModel = viewModel(factory = factory)
    val driverState = viewModel.drivers.observeAsState()
    val driverChampionshipState = viewModel.driverChampionships.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchDrivers()
        viewModel.fetchDriverChampionships()
    }

    when (val state = driverState.value) {
        is Resource.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
            }
        }

        is Resource.Success -> {
            val championshipState = driverChampionshipState.value
            if (championshipState?.data.isNullOrEmpty()) {
                return Text(text = stringResource(R.string.drivers_not_found), modifier = Modifier.padding(8.dp))
            }
            state.data?.forEach { driver ->
                championshipState?.data?.forEach { championshipDriver ->
                    if (championshipDriver.driver_number == driver.driver_number) {
                        driver.championship_info = championshipDriver
                        driver.championship_position = championshipDriver.position_current.toInt()
                    }
                }
            }
            val driverSortComparator = Comparator<DriverResponse> { driver1, driver2 ->
                driver1.championship_position.compareTo(driver2.championship_position)
            }
            val sortedDrivers = state.data?.sortedWith(driverSortComparator);
            sortedDrivers?.forEach { driver ->
                if (driver.championship_position == 1) {
                    driver.championship_info.interval = 0
                } else {
                    val intervalPoints: Int = sortedDrivers.first { intervalDriver ->
                        intervalDriver.championship_position == (driver.championship_position - 1)
                    }.championship_info.points_current
                    driver.championship_info.interval = driver.championship_info.points_current?.minus(
                        intervalPoints
                    )
                }
            }
            val column1: MutableList<DriverResponse> = mutableListOf<DriverResponse>()
            val column2: MutableList<DriverResponse> = mutableListOf<DriverResponse>()
            sortedDrivers?.forEach { driver ->
                if (driver.championship_position % 2 == 0) {
                    column2.add(driver)
                } else {
                    column1.add(driver)
                }
            }
            Column(modifier = Modifier.verticalScroll(state = rememberScrollState()).fillMaxWidth()) {
                Row() {
                    Column(modifier = Modifier.width(175.dp)) {
                        if(column1.isNotEmpty()) {
                            column1.forEach { driver ->
                                Box() {
                                    DriverItem(driver, sortedDrivers?.first()?.championship_info?.points_current)
                                }
                            }
                        }
                    }
                    Column(modifier = Modifier.padding(top = 30.dp).width(175.dp)) {
                        if(column2.isNotEmpty()) {
                            column2.forEach { driver ->
                                DriverItem(driver, sortedDrivers?.first()?.championship_info?.points_current)
                            }
                        }
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

@Composable
fun DriverItem(driver: DriverResponse?, leaderPoints: Number?) {
    val teamColor: Number = parseColor("#${driver?.team_colour}")
    var isExpanded by rememberSaveable { mutableStateOf(false)}
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
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Box(
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row() {
                        driver?.last_name?.let {
                            Text(
                                text = it,
                                style = TextStyle(
                                    fontStyle = FontStyle.Normal,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                )
                            )
                        }
                        Text(
                            text = " (${driver?.driver_number})",
                            style = TextStyle(
                                fontStyle = FontStyle.Normal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                            )
                        )
                    }
                }
                Text(
                    text = ordinalOf(driver!!.championship_position),
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontStyle = FontStyle.Italic
                    ),
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(top = 10.dp, end = 10.dp)
                )
            }
            Row() {
                Image(
                    painter = rememberAsyncImagePainter(driver?.headshot_url),
                    contentDescription = stringResource(R.string.driver_image),
                    modifier = Modifier.size(50.dp).padding(top = 8.dp)
                )
                Box(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Image(
                        painter = painterResource(resolveTeamImage(driver!!.team_name)),
                        contentDescription = stringResource(R.string.team_image),
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = isExpanded
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Box() {
                        if (driver?.championship_position == 1) {
                            Row() {
                                Text(text = "${stringResource(R.string.points)}: ", style = TextStyle(
                                    fontSize = 12.sp
                                ))
                                Text(text = driver?.championship_info?.points_current?.toString() ?: "0.0", style = TextStyle(
                                    fontSize = 12.sp
                                ))
                            }
                        } else {
                            val pointsBehind = leaderPoints!!.toInt() - (driver?.championship_info?.points_current ?: 0)
                            Column() {
                                if (driver?.championship_position != 1) {
                                    Text(text = "${stringResource(R.string.interval)}: ${driver?.championship_info?.interval}", style = TextStyle(fontSize = 12.sp))
                                }
                                Row() {
                                    Text(text = "Total: ", style = TextStyle(fontSize = 12.sp))
                                    Text(text = driver?.championship_info?.points_current?.toString() ?: "0.0", style = TextStyle(fontSize = 12.sp))
                                }
                                Text(text = "${stringResource(R.string.gap_to_lead)}: -${pointsBehind}", style = TextStyle(
                                    fontSize = 12.sp
                                ))
                            }
                        }
                    }
                }
            }
        }
    }
}