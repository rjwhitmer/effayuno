package io.github.formula1.service.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.github.formula1.R
import io.github.formula1.model.NavigationItem
import io.github.formula1.model.dto.DriverResponse
import io.github.formula1.model.dto.SessionResponse
import io.github.formula1.model.dto.TeamChampionshipResponse
import io.github.formula1.view.composable.DriverListScreen
import io.github.formula1.view.composable.MainScreen
import io.github.formula1.view.composable.SessionDetailScreen
import io.github.formula1.view.composable.SessionListScreen
import io.github.formula1.view.composable.TeamChampionshipScreen
import kotlinx.coroutines.launch

fun NavGraphBuilder.navigationGraph(navController: NavController) {
    composable(Route.Main.navRoute) {
        MainScreen(navController = navController)
    }
    composable(route = Route.SessionList.navRoute) {
        SessionListScreen(navController = navController)
    }

    composable<SessionResponse> { backStackEntry ->
        val sessionResponse: SessionResponse = backStackEntry.toRoute()
        SessionDetailScreen(navController, sessionResponse.session_key, sessionResponse.meeting_key, sessionResponse.session_type, sessionResponse.session_name)
    }

    composable(route = Route.Teams.navRoute) {
        TeamChampionshipScreen(navController = navController)
    }

    composable(route = Route.Drivers.navRoute) {
        DriverListScreen(navController = navController)
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationStack() {
    val navController = rememberNavController()
    val navigationItems = listOf(
        NavigationItem(
            title = stringResource(R.string.home_link),
            icon = Icons.Default.Home,
            route = Route.Main.navRoute
        ),
        NavigationItem(
            title = stringResource(R.string.meeting_link),
            icon = Icons.Filled.Flag,
            route = Route.SessionList.navRoute
        ),
        NavigationItem(
            title = stringResource(R.string.constructors_link),
            icon = Icons.Filled.Factory,
            route = Route.Teams.navRoute
        ),
        NavigationItem(
            title = stringResource(R.string.drivers_link),
            icon = Icons.Filled.People,
            route = Route.Drivers.navRoute
        )
    )
    val selectedNavigationIndex = rememberSaveable { mutableIntStateOf(0) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val modifier = Modifier.padding(start = 8.dp, end = 8.dp)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DismissibleDrawerSheet() {
                Box(modifier = Modifier.align(Alignment.End)) {
                    Button(onClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    }) {
                        Text(text = "X")
                    }
                }
                HorizontalDivider()
                NavigationDrawerItem(
                    modifier = modifier,
                    label = { Text(text = stringResource(R.string.home_link)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(route = Route.Main.navRoute)
                    }
                )
                NavigationDrawerItem(
                    modifier = modifier,
                    label = { Text(text = stringResource(R.string.meeting_link)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(route = Route.SessionList.navRoute)
                    }
                )
                NavigationDrawerItem(
                    modifier = modifier,
                    label = { Text(text = stringResource(R.string.constructors_link)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(route = Route.Teams.navRoute)
                    }
                )
                NavigationDrawerItem(
                    modifier = modifier,
                    label = { Text(text = stringResource(R.string.drivers_link)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(route = Route.Drivers.navRoute)
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(
                        text = "Effay Uno",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().offset(x = -(30.dp)),
                        style =
                            TextStyle(
                                fontStyle = FontStyle.Italic,
                                fontSize = 18.sp
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.apply {
                                        if (isClosed) open() else close()
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                    navigationItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedNavigationIndex.intValue == index,
                            onClick = {
                                selectedNavigationIndex.intValue = index
                                navController.navigate(item.route)
                            },
                            icon = {
                                Icon(imageVector = item.icon, contentDescription = item.title)
                            },
                            label = { Text(item.title) }
                        )
                    }
                }
            }
        ) { contentPadding ->
            Column(modifier = Modifier.padding(contentPadding)) {
                NavHost(navController = navController, startDestination = Route.Main.navRoute) {
                    navigationGraph(navController)
                }
            }
        }
    }
}