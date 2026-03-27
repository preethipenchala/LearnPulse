package com.learnpulse.data.repository

import com.learnpulse.data.remote.api.LearnPulseApi
import com.learnpulse.data.remote.dto.LoginRequest
import com.learnpulse.data.remote.dto.RegisterRequest
import com.learnpulse.data.remote.dto.toDomain
import com.learnpulse.domain.model.User
import com.learnpulse.domain.model.UserPreferences
import com.learnpulse.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class UserRepositoryImpl(
    private val api: LearnPulseApi
) : UserRepository {

    private val _currentUser = MutableStateFlow<User?>(null)

    override fun getCurrentUser(): Flow<User?> = _currentUser

    override suspend fun getUserById(userId: String): User? = _currentUser.value

    override suspend fun updateUserPreferences(userId: String, preferences: UserPreferences): Result<Unit> {
        return try {
            val current = _currentUser.value ?: return Result.failure(Exception("No user"))
            _currentUser.value = current.copy(preferences = preferences)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            api.logout()
            _currentUser.value = null
            Result.success(Unit)
        } catch (e: Exception) {
            _currentUser.value = null
            Result.success(Unit)
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = api.login(LoginRequest(email, password))
            val user = response.user.toDomain()
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            val response = api.register(RegisterRequest(name, email, password))
            val user = response.user.toDomain()
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
