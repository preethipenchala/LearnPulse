package com.learnpulse.data.mock

import com.learnpulse.domain.model.User
import com.learnpulse.domain.model.UserPreferences
import com.learnpulse.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class MockUserRepository : UserRepository {

    private val _currentUser = MutableStateFlow<User?>(MockDataSource.currentUser)

    override fun getCurrentUser(): Flow<User?> = _currentUser

    override suspend fun getUserById(userId: String): User? = _currentUser.value

    override suspend fun updateUserPreferences(userId: String, preferences: UserPreferences): Result<Unit> {
        val current = _currentUser.value ?: return Result.failure(Exception("No user"))
        _currentUser.value = current.copy(preferences = preferences)
        return Result.success(Unit)
    }

    override suspend fun logout(): Result<Unit> {
        _currentUser.value = null
        return Result.success(Unit)
    }

    override suspend fun login(email: String, password: String): Result<User> {
        // Accept any credentials for testing — resets to mock user
        _currentUser.value = MockDataSource.currentUser.copy(email = email)
        return Result.success(_currentUser.value!!)
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        val newUser = MockDataSource.currentUser.copy(
            id = "user-new",
            name = name,
            email = email,
            enrolledCourseIds = emptyList(),
            completedCourseIds = emptyList(),
            streakDays = 0,
            totalLearningTimeSeconds = 0L,
            certificates = emptyList()
        )
        _currentUser.value = newUser
        return Result.success(newUser)
    }
}
