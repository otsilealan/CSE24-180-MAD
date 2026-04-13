package com.accommodation.data.database.dao

import androidx.room.*
import com.accommodation.data.database.entities.UserPreferences

@Dao
interface PreferencesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun save(prefs: UserPreferences)
    @Query("SELECT * FROM user_preferences WHERE userId = :userId LIMIT 1") suspend fun get(userId: Int): UserPreferences?
    @Query("SELECT * FROM user_preferences") suspend fun getAll(): List<UserPreferences>
}
