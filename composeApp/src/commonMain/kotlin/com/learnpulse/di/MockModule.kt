package com.learnpulse.di

import com.learnpulse.data.mock.MockCourseRepository
import com.learnpulse.data.mock.MockDownloadRepository
import com.learnpulse.data.mock.MockNotesRepository
import com.learnpulse.data.mock.MockProgressRepository
import com.learnpulse.data.mock.MockQuizRepository
import com.learnpulse.data.mock.MockUserRepository
import com.learnpulse.domain.repository.CourseRepository
import com.learnpulse.domain.repository.DownloadRepository
import com.learnpulse.domain.repository.NotesRepository
import com.learnpulse.domain.repository.ProgressRepository
import com.learnpulse.domain.repository.QuizRepository
import com.learnpulse.domain.repository.UserRepository
import org.koin.dsl.module

/**
 * Koin module that replaces all real repositories with in-memory mock implementations.
 * Swap [repositoryModule] ↔ [mockRepositoryModule] in [commonModules] to toggle.
 *
 * Mock data covers:
 *  - 10 courses across all 6 categories (free + paid, all difficulty levels)
 *  - 10 lessons per course with real public video URLs (Google sample videos)
 *  - Pre-built quizzes for course-1, course-2, course-3 lesson 10
 *  - AI quiz generator for Kotlin, Python/ML, or generic topics
 *  - A signed-in user (Alex Johnson) with 5 enrolled courses, 1 completed
 *  - Progress records showing 20–100% per enrolled course
 *  - 5 notes with video timestamps spread across courses
 *  - 3 bookmarks
 *  - 3 completed downloads + 1 in-progress download
 *  - Course reviews for 3 courses
 */
val mockRepositoryModule = module {
    single<CourseRepository> { MockCourseRepository() }
    single<UserRepository> { MockUserRepository() }
    single<ProgressRepository> { MockProgressRepository() }
    single<NotesRepository> { MockNotesRepository() }
    single<QuizRepository> { MockQuizRepository() }
    single<DownloadRepository> { MockDownloadRepository() }
}
