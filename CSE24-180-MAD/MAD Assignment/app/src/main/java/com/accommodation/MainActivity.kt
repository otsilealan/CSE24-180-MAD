package com.accommodation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.accommodation.ui.auth.*
import com.accommodation.ui.filter.*
import com.accommodation.ui.listings.*
import com.accommodation.ui.navigation.MapScreen
import com.accommodation.ui.navigation.NavigationViewModel
import com.accommodation.ui.reservation.*
import com.accommodation.ui.theme.AccommodationTheme
import com.accommodation.utils.SessionManager
import com.accommodation.workers.NotificationWorker

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        NotificationWorker.schedule(this)
        val deepLinkListingId = intent?.getIntExtra("listingId", -1) ?: -1

        setContent {
            AccommodationTheme {
                AppNavigation(deepLinkListingId)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NotificationWorker.CHANNEL_ID, "Accommodation Alerts", NotificationManager.IMPORTANCE_DEFAULT)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}

@Composable
fun AppNavigation(deepLinkListingId: Int = -1) {
    val app = androidx.compose.ui.platform.LocalContext.current.applicationContext as AccommodationApp
    val factory = remember { AppViewModelFactory(app.container) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val listingsViewModel: ListingsViewModel = viewModel(factory = factory)
    val filterViewModel: FilterViewModel = viewModel(factory = factory)
    val reservationViewModel: ReservationViewModel = viewModel(factory = factory)
    val navViewModel: NavigationViewModel = viewModel(factory = factory)

    val currentListing by navViewModel.currentListing.collectAsState()
    val currentReservation by navViewModel.currentReservation.collectAsState()

    val navController = rememberNavController()
    val startDest = if (SessionManager.isLoggedIn(context)) "listings" else "login"
    val userId = SessionManager.getUserId(context)
    val role = SessionManager.getRole(context)

    Scaffold(
        bottomBar = {
            val navBackStack by navController.currentBackStackEntryAsState()
            val current = navBackStack?.destination?.route
            if (current in listOf("listings", "profile")) {
                NavigationBar {
                    NavigationBarItem(selected = current == "listings", onClick = { navController.navigate("listings") { launchSingleTop = true } }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Listings") })
                    NavigationBarItem(selected = current == "profile", onClick = { navController.navigate("profile") { launchSingleTop = true } }, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") })
                }
            }
        }
    ) { padding ->
        NavHost(navController, startDestination = startDest, modifier = Modifier.padding(padding)) {

            composable("login") {
                LoginScreen(
                    viewModel = authViewModel,
                    onSuccess = { uid, r ->
                        SessionManager.save(context, uid, r)
                        navController.navigate("listings") { popUpTo("login") { inclusive = true } }
                    },
                    onNavigateToRegister = { navController.navigate("register") }
                )
            }

            composable("register") {
                RegisterScreen(
                    viewModel = authViewModel,
                    onSuccess = { uid, r ->
                        SessionManager.save(context, uid, r)
                        navController.navigate("listings") { popUpTo("register") { inclusive = true } }
                    },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }

            composable("listings") {
                ListingsScreen(
                    viewModel = listingsViewModel,
                    onListingClick = { id ->
                        navViewModel.loadListing(id)
                        navController.navigate("listing_detail")
                    },
                    onFilterClick = { navController.navigate("filter") }
                )
            }

            composable("listing_detail") {
                currentListing?.let { listing ->
                    ListingDetailScreen(
                        listing = listing,
                        isStudent = role == "Student",
                        onBack = { navController.popBackStack() },
                        onReserve = { navController.navigate("payment") },
                        onViewRoute = {
                            val coords = navViewModel.getListingLatLng(listing)
                            navController.navigate("map/${coords.latitude}/${coords.longitude}")
                        }
                    )
                }
            }

            composable("filter") {
                ModalBottomSheet(onDismissRequest = { navController.popBackStack() }) {
                    FilterScreen(
                        viewModel = filterViewModel,
                        userId = userId,
                        onApply = { params ->
                            listingsViewModel.applyFilter(params)
                            navController.popBackStack()
                        },
                        onDismiss = { navController.popBackStack() }
                    )
                }
            }

            composable("payment") {
                currentListing?.let { listing ->
                    PaymentScreen(
                        listing = listing,
                        studentId = userId,
                        viewModel = reservationViewModel,
                        onSuccess = { reservation ->
                            navViewModel.setReservation(reservation)
                            navController.navigate("receipt") { popUpTo("listings") }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            composable("receipt") {
                currentReservation?.let { reservation ->
                    ReceiptScreen(
                        reservation = reservation,
                        listingTitle = currentListing?.title ?: "",
                        listingLocation = currentListing?.location ?: "",
                        onDone = {
                            navViewModel.clearTransaction()
                            navController.navigate("listings") { popUpTo("listings") { inclusive = true } }
                        }
                    )
                }
            }

            composable(
                "map/{lat}/{lon}",
                arguments = listOf(navArgument("lat") { type = NavType.FloatType }, navArgument("lon") { type = NavType.FloatType })
            ) { back ->
                val lat = back.arguments?.getFloat("lat")?.toDouble() ?: 0.0
                val lon = back.arguments?.getFloat("lon")?.toDouble() ?: 0.0
                MapScreen(listingTitle = currentListing?.title ?: "", listingLat = lat, listingLon = lon, onBack = { navController.popBackStack() })
            }

            composable("profile") {
                ProfileScreen(userId = userId, role = role, onLogout = {
                    SessionManager.clear(context)
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                })
            }

            composable("create_listing") {
                CreateListingScreen(providerId = userId, viewModel = listingsViewModel, onCreated = { navController.popBackStack() })
            }
        }
    }

    // Handle deep link from notification
    LaunchedEffect(deepLinkListingId) {
        if (deepLinkListingId != -1 && SessionManager.isLoggedIn(context)) {
            navViewModel.loadListing(deepLinkListingId)
            navController.navigate("listing_detail")
        }
    }
}
