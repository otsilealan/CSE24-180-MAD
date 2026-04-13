package com.accommodation.workers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.accommodation.MainActivity
import com.accommodation.data.database.AppDatabase
import com.accommodation.data.repository.ListingRepository
import com.accommodation.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class NotificationWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val db = AppDatabase.getInstance(applicationContext)
        val listingRepo = ListingRepository(db.listingDao())
        val prefsRepo = PreferencesRepository(db.preferencesDao())

        val allPrefs = prefsRepo.getAll()
        val allListings = listingRepo.getAll().first()

        allPrefs.forEach { pref ->
            val matches = allListings.filter { listing ->
                listing.status == "Available" &&
                (pref.minPrice == 0.0 || listing.price >= pref.minPrice) &&
                (pref.maxPrice == 0.0 || listing.price <= pref.maxPrice) &&
                (pref.location.isBlank() || listing.location == pref.location) &&
                (pref.availabilityDate == 0L || listing.availabilityDate >= pref.availabilityDate)
            }
            matches.firstOrNull()?.let { listing ->
                notify(pref.userId, listing.id, listing.title, listing.location, listing.price)
            }
        }
        return Result.success()
    }

    private fun notify(userId: Int, listingId: Int, title: String, location: String, price: Double) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("listingId", listingId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pi = PendingIntent.getActivity(applicationContext, listingId, intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("New match: $title")
            .setContentText("$location — BWP $price/month")
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()
        val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(userId * 1000 + listingId, notification)
    }

    companion object {
        const val CHANNEL_ID = "accommodation_alerts"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.NOT_REQUIRED).build())
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "preference_check", ExistingPeriodicWorkPolicy.KEEP, request
            )
        }
    }
}
