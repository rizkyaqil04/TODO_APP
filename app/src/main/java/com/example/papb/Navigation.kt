package com.example.papb

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.papb.pages.HomePage
import com.example.papb.pages.ToDoListPage
import com.example.papb.pages.AccountPage
import com.example.papb.pages.LoginPage
import com.example.papb.pages.SignupPage
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.foundation.layout.*
import com.example.papb.ui.theme.ThemeViewModel
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.runtime.observeAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    themeViewModel: ThemeViewModel
) {
    val navController = rememberNavController()
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    val authState by authViewModel.authState.observeAsState(AuthState.Initial)

    NavHost(
        navController = navController,
        startDestination = if (authState == AuthState.Authenticated) "home" else "login"
    ) {
        composable("login") { LoginPage(modifier, navController, authViewModel) }
        composable("signup") { SignupPage(modifier, navController, authViewModel) }
        composable("home") {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                "Home",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        actions = {
                            IconButton(onClick = { themeViewModel.toggleTheme() }) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    )
                },
                bottomBar = {
                    BottomNavigation {
                        BottomNavigationItem(
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home") },
                            selected = false,
                            onClick = { navController.navigate("home") }
                        )
                        BottomNavigationItem(
                            icon = { Icon(Icons.Default.List, contentDescription = "To-Do List") },
                            label = { Text("To-Do List") },
                            selected = false,
                            onClick = { navController.navigate("todo") }
                        )
                        BottomNavigationItem(
                            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Account") },
                            label = { Text("Account") },
                            selected = false,
                            onClick = { navController.navigate("account") }
                        )
                    }
                }
            ) { innerPadding ->
                HomePage(modifier.padding(innerPadding))
            }
        }
        composable("todo") {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                "To-Do List",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        actions = {
                            IconButton(onClick = { themeViewModel.toggleTheme() }) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    )
                },
                bottomBar = {
                    BottomNavigation {
                        BottomNavigationItem(
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home") },
                            selected = false,
                            onClick = { navController.navigate("home") }
                        )
                        BottomNavigationItem(
                            icon = { Icon(Icons.Default.List, contentDescription = "To-Do List") },
                            label = { Text("To-Do List") },
                            selected = false,
                            onClick = { navController.navigate("todo") }
                        )
                        BottomNavigationItem(
                            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Account") },
                            label = { Text("Account") },
                            selected = false,
                            onClick = { navController.navigate("account") }
                        )
                    }
                }
            ) { innerPadding ->
                ToDoListPage(modifier.padding(innerPadding), navController, authViewModel)
            }
        }
        composable("account") {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                "Account",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        actions = {
                            IconButton(onClick = { themeViewModel.toggleTheme() }) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    )
                },
                bottomBar = {
                    BottomNavigation {
                        BottomNavigationItem(
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home") },
                            selected = false,
                            onClick = { navController.navigate("home") }
                        )
                        BottomNavigationItem(
                            icon = { Icon(Icons.Default.List, contentDescription = "To-Do List") },
                            label = { Text("To-Do List") },
                            selected = false,
                            onClick = { navController.navigate("todo") }
                        )
                        BottomNavigationItem(
                            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Account") },
                            label = { Text("Account") },
                            selected = false,
                            onClick = { navController.navigate("account") }
                        )
                    }
                }
            ) { innerPadding ->
                AccountPage(
                    modifier.padding(innerPadding),
                    navController,
                    authViewModel,
                    themeViewModel,

                )
            }
        }
        composable("camera") {
            CameraPreview(navController = navController)
        }
    }
}
