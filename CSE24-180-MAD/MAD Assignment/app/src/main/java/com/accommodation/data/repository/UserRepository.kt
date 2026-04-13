package com.accommodation.data.repository

import com.accommodation.data.database.dao.UserDao
import com.accommodation.data.database.entities.User
import com.accommodation.utils.ValidationUtils

class UserRepository(private val dao: UserDao) {
    suspend fun register(email: String, password: String, studentId: String?, phone: String, role: String): Result<User> {
        if (!ValidationUtils.isValidEmail(email)) return Result.failure(Exception("Invalid email format"))
        if (!ValidationUtils.isValidPassword(password)) return Result.failure(Exception("Password must be 8+ chars with letter, digit, and special character"))
        if (!ValidationUtils.isValidPhone(phone)) return Result.failure(Exception("Invalid phone number"))
        if (dao.findByEmail(email) != null) return Result.failure(Exception("Email already registered"))
        val user = User(email = email, passwordHash = ValidationUtils.hashPassword(password), studentId = studentId, phone = phone, role = role)
        val id = dao.insert(user)
        return Result.success(user.copy(id = id.toInt()))
    }

    suspend fun login(email: String, password: String): Result<User> {
        val user = dao.findByEmail(email) ?: return Result.failure(Exception("Invalid email or password"))
        if (!ValidationUtils.verifyPassword(password, user.passwordHash)) return Result.failure(Exception("Invalid email or password"))
        return Result.success(user)
    }

    suspend fun findById(id: Int): User? = dao.findById(id)
}
