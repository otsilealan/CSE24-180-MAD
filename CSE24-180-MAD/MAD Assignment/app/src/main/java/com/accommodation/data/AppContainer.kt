package com.accommodation.data

import android.content.Context
import com.accommodation.data.database.AppDatabase
import com.accommodation.data.repository.*

class AppContainer(context: Context) {
    private val db = AppDatabase.getInstance(context)
    val userRepository = UserRepository(db.userDao())
    val listingRepository = ListingRepository(db.listingDao())
    val reservationRepository = ReservationRepository(db.reservationDao(), db.listingDao())
    val preferencesRepository = PreferencesRepository(db.preferencesDao())
}
