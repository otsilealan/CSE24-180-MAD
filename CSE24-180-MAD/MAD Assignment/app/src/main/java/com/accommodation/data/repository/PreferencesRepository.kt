package com.accommodation.data.repository

import com.accommodation.data.database.dao.PreferencesDao
import com.accommodation.data.database.entities.UserPreferences

class PreferencesRepository(private val dao: PreferencesDao) {
    suspend fun save(prefs: UserPreferences) = dao.save(prefs)
    suspend fun get(userId: Int): UserPreferences? = dao.get(userId)
    suspend fun getAll(): List<UserPreferences> = dao.getAll()
}
