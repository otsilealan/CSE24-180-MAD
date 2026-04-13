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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.accommodation.data.database.AppDatabase
import com.accommodation.data.database.entities.Listing
import com.accommodation.data.database.entities.Reservation
import com.accommodation.data.repository.*
import com.accommodation.ui.auth.*
import com.accommodation.ui.filter.*
import com.accommodation.ui.listings.*
import com.accommodation.ui.navigation.MapScreen
import com.accommodation.ui.reservation.*
import com.accommodation.ui.theme.AccommodationTheme
import com.accommodation.utils.SessionManager
import com.accommodation.workers.NotificationWorker
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val userRepo = remember { UserRepository(db.userDao()) }
    val listingRepo = remember { ListingRepository(db.listingDao()) }
    val reservationRepo = remember { ReservationRepository(db.reservationDao(), db.listingDao()) }
    val prefsRepo = remember { PreferencesRepository(db.preferencesDao()) }

    val authViewModel = remember { AuthViewModel(userRepo) }
    val listingsViewModel = remember { ListingsViewModel(listingRepo) }
    val filterViewModel = remember { FilterViewModel(prefsRepo) }
    val reservationViewModel = remember { ReservationViewModel(reservationRepo) }

    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    // Transient state shared across screens
    var currentListing by remember { mutableStateOf<Listing?>(null) }
    var currentReservation by remember { mutableStateOf<Reservation?>(null) }

    val startDest = if (SessionManager.isLoggedIn(context)) "listings" else "login"

    val userId = SessionManager.getUserId(context)
    val role = SessionManager.getRole(context)

    Scaffold(
        bottomBar = {
            val navBackStack by navController.currentBackStackEntryAsState()
            val current = navBackStack?.destination?.route
            val showBar = current in listOf("listings", "profile")
            if (showBar) {
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
                        scope.launch {
                            currentListing = listingRepo.findById(id)
                            navController.navigate("listing_detail")
                        }
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
                            val coords = ListingsViewModel.campusCoords[listing.location]
                            if (coords != null) navController.navigate("map/${coords.first}/${coords.second}")
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
                            currentReservation = reservation
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
                            currentListing = null; currentReservation = null
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
            currentListing = listingRepo.findById(deepLinkListingId)
            navController.navigate("listing_detail")
        }
    }
}
