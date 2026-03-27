package com.learnpulse.data.remote.api

import com.learnpulse.data.remote.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class LearnPulseApi(private val httpClient: HttpClient) {

    companion object {
        const val BASE_URL = "https://api.learnpulse.com/v1"
    }

    // Courses
    suspend fun getCourses(
        category: String? = null,
        difficulty: String? = null,
        sortBy: String = "popularity",
        page: Int = 0,
        pageSize: Int = 20
    ): CoursesResponse = httpClient.get("$BASE_URL/courses") {
        parameter("page", page)
        parameter("pageSize", pageSize)
        parameter("sortBy", sortBy)
        category?.let { parameter("category", it) }
        difficulty?.let { parameter("difficulty", it) }
    }.body()

    suspend fun searchCourses(query: String, page: Int = 0): CoursesResponse =
        httpClient.get("$BASE_URL/courses/search") {
            parameter("q", query)
            parameter("page", page)
        }.body()

    suspend fun getFeaturedCourses(): List<CourseDto> =
        httpClient.get("$BASE_URL/courses/featured").body()

    suspend fun getTrendingCourses(): List<CourseDto> =
        httpClient.get("$BASE_URL/courses/trending").body()

    suspend fun getRecommendedCourses(userId: String): List<CourseDto> =
        httpClient.get("$BASE_URL/users/$userId/recommendations").body()

    suspend fun getCourseById(courseId: String): CourseDto =
        httpClient.get("$BASE_URL/courses/$courseId").body()

    suspend fun getCourseReviews(courseId: String): List<CourseReviewDto> =
        httpClient.get("$BASE_URL/courses/$courseId/reviews").body()

    suspend fun enrollInCourse(request: EnrollRequest) {
        httpClient.post("$BASE_URL/enrollments") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun getEnrolledCourses(userId: String): List<CourseDto> =
        httpClient.get("$BASE_URL/users/$userId/courses").body()

    // Auth
    suspend fun login(request: LoginRequest): AuthResponse =
        httpClient.post("$BASE_URL/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun register(request: RegisterRequest): AuthResponse =
        httpClient.post("$BASE_URL/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun logout() {
        httpClient.post("$BASE_URL/auth/logout")
    }

    // Progress
    suspend fun getProgress(userId: String, courseId: String): UserProgressDto =
        httpClient.get("$BASE_URL/users/$userId/progress/$courseId").body()

    suspend fun getAllProgress(userId: String): List<UserProgressDto> =
        httpClient.get("$BASE_URL/users/$userId/progress").body()

    suspend fun markLessonComplete(userId: String, courseId: String, lessonId: String) {
        httpClient.post("$BASE_URL/users/$userId/progress/$courseId/lessons/$lessonId/complete")
    }

    suspend fun saveQuizScore(userId: String, courseId: String, lessonId: String, score: Int) {
        httpClient.post("$BASE_URL/users/$userId/progress/$courseId/quizzes/$lessonId") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("score" to score))
        }
    }

    // Quiz
    suspend fun getQuizForLesson(lessonId: String): QuizDto =
        httpClient.get("$BASE_URL/lessons/$lessonId/quiz").body()

    suspend fun generateAiQuiz(request: GenerateQuizRequest): QuizDto =
        httpClient.post("$BASE_URL/ai/generate-quiz") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}
