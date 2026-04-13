package com.accommodation.data.database.dao

import androidx.room.*
import com.accommodation.data.database.entities.User

@Dao
interface UserDao {
    @Insert suspend fun insert(user: User): Long
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): User?
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): User?
    @Query("SELECT COUNT(*) FROM users") suspend fun count(): Int
}
