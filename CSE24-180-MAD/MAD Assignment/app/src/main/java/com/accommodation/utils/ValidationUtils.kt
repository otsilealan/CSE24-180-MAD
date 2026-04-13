package com.accommodation.utils

import android.util.Patterns

object ValidationUtils {
    fun isValidEmail(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidPassword(password: String): Boolean =
        password.length >= 8 && password.any { it.isDigit() } && password.any { it.isLetter() } && password.any { !it.isLetterOrDigit() }

    fun isValidPhone(phone: String): Boolean =
        phone.matches(Regex("^(\\+267)?[78]\\d{7}$"))

    fun hashPassword(password: String): String =
        at.favre.lib.crypto.bcrypt.BCrypt.withDefaults().hashToString(10, password.toCharArray())

    fun verifyPassword(password: String, hash: String): Boolean =
        at.favre.lib.crypto.bcrypt.BCrypt.verifyer().verify(password.toCharArray(), hash).verified
}
