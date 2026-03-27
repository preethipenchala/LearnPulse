package com.learnpulse.domain.repository

import com.learnpulse.domain.model.User
import com.learnpulse.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): Flow<User?>

    suspend fun getUserById(userId: String): User?

    suspend fun updateUserPreferences(userId: String, preferences: UserPreferences): Result<Unit>

    suspend fun logout(): Result<Unit>

    suspend fun login(email: String, password: String): Result<User>

    suspend fun register(name: String, email: String, password: String): Result<User>
}
