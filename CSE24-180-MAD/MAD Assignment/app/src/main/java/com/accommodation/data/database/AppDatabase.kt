package com.accommodation.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.accommodation.data.database.dao.*
import com.accommodation.data.database.entities.*
import com.accommodation.data.SeedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, Listing::class, Reservation::class, UserPreferences::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun listingDao(): ListingDao
    abstract fun reservationDao(): ReservationDao
    abstract fun preferencesDao(): PreferencesDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(context, AppDatabase::class.java, "accommodation.db")
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    SeedData.seed(database)
                                }
                            }
                        }
                    })
                    .build().also { INSTANCE = it }
            }
    }
}
