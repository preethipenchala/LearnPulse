package com.learnpulse.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.learnpulse.db.LearnPulseDatabase
import com.learnpulse.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LocalDataSource(private val database: LearnPulseDatabase) {

    // Courses
    fun getCachedCourses(): Flow<List<Course>> =
        database.courseTableQueries.selectAll().asFlow().mapToList(Dispatchers.Default).map { rows ->
            rows.map { it.toCourse() }
        }

    fun getCachedCoursesByCategory(category: String): Flow<List<Course>> =
        database.courseTableQueries.selectByCategory(category).asFlow().mapToList(Dispatchers.Default).map { rows ->
            rows.map { it.toCourse() }
        }

    suspend fun cacheCourse(course: Course) {
        database.courseTableQueries.insertOrReplace(
            id = course.id,
            title = course.title,
            description = course.description,
            instructorId = course.instructor.id,
            instructorName = course.instructor.name,
            instructorAvatarUrl = course.instructor.avatarUrl,
            instructorBio = course.instructor.bio,
            instructorCourseCount = course.instructor.courseCount.toLong(),
            instructorRating = course.instructor.rating,
            thumbnailUrl = course.thumbnailUrl,
            category = course.category.name,
            difficulty = course.difficulty.name,
            rating = course.rating,
            enrolledCount = course.enrolledCount.toLong(),
            totalDuration = course.totalDuration,
            price = course.price,
            isFree = if (course.isFree) 1L else 0L,
            tags = Json.encodeToString(course.tags),
            isCached = 1L,
            cachedAt = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        )
        course.lessons.forEach { lesson ->
            cacheLessons(listOf(lesson))
        }
    }

    suspend fun cacheLessons(lessons: List<Lesson>) {
        lessons.forEach { lesson ->
            database.lessonTableQueries.insertOrReplace(
                id = lesson.id,
                courseId = lesson.courseId,
                title = lesson.title,
                type = lesson.type.name,
                duration = lesson.duration,
                contentUrl = lesson.contentUrl,
                lessonOrder = lesson.order.toLong(),
                isPreview = if (lesson.isPreview) 1L else 0L,
                description = lesson.description
            )
        }
    }

    fun getLessonsForCourse(courseId: String): Flow<List<Lesson>> =
        database.lessonTableQueries.selectByCourseId(courseId).asFlow().mapToList(Dispatchers.Default).map { rows ->
            rows.map { it.toLesson() }
        }

    // Progress
    fun getProgress(userId: String, courseId: String): Flow<UserProgress?> =
        database.progressTableQueries.selectByCourseId(userId, courseId).asFlow().mapToOneOrNull(Dispatchers.Default).map { row ->
            row?.toUserProgress()
        }

    fun getAllProgress(userId: String): Flow<List<UserProgress>> =
        database.progressTableQueries.selectAllForUser(userId).asFlow().mapToList(Dispatchers.Default).map { rows ->
            rows.map { it.toUserProgress() }
        }

    suspend fun saveProgress(progress: UserProgress) {
        database.progressTableQueries.insertOrReplace(
            userId = progress.userId,
            courseId = progress.courseId,
            completedLessonsJson = Json.encodeToString(progress.completedLessons),
            quizScoresJson = Json.encodeToString(progress.quizScores),
            lastAccessedLessonId = progress.lastAccessedLessonId,
            overallProgress = progress.overallProgress.toDouble(),
            certificateEarned = if (progress.certificateEarned) 1L else 0L,
            streakDays = progress.streakDays.toLong(),
            totalTimeSpentSeconds = progress.totalTimeSpentSeconds,
            updatedAt = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        )
    }

    // Notes
    fun getNotes(userId: String, lessonId: String? = null): Flow<List<Note>> {
        return if (lessonId != null) {
            database.noteTableQueries.selectByLesson(userId, lessonId).asFlow().mapToList(Dispatchers.Default).map { rows ->
                rows.map { it.toNote() }
            }
        } else {
            database.noteTableQueries.selectByUser(userId).asFlow().mapToList(Dispatchers.Default).map { rows ->
                rows.map { it.toNote() }
            }
        }
    }

    fun searchNotes(userId: String, query: String): Flow<List<Note>> =
        database.noteTableQueries.searchNotes(userId, query).asFlow().mapToList(Dispatchers.Default).map { rows ->
            rows.map { it.toNote() }
        }

    suspend fun saveNote(note: Note) {
        database.noteTableQueries.insertOrReplace(
            id = note.id,
            userId = note.userId,
            courseId = note.courseId,
            lessonId = note.lessonId,
            content = note.content,
            timestampSeconds = note.timestampSeconds,
            createdAt = note.createdAt,
            updatedAt = note.updatedAt
        )
    }

    suspend fun deleteNote(noteId: String) {
        database.noteTableQueries.deleteById(noteId)
    }

    // Downloads
    fun getDownloadedLessons(): Flow<List<DownloadedLesson>> =
        database.downloadTableQueries.selectAll().asFlow().mapToList(Dispatchers.Default).map { rows ->
            rows.map { it.toDownloadedLesson() }
        }

    suspend fun saveDownload(download: DownloadedLesson) {
        database.downloadTableQueries.insertOrReplace(
            lessonId = download.lessonId,
            courseId = download.courseId,
            title = download.title,
            localFilePath = download.localFilePath,
            fileSizeBytes = download.fileSizeBytes,
            downloadedAt = download.downloadedAt,
            status = download.status.name
        )
    }

    suspend fun updateDownloadStatus(lessonId: String, status: DownloadStatus) {
        database.downloadTableQueries.updateStatus(status.name, lessonId)
    }

    suspend fun deleteDownload(lessonId: String) {
        database.downloadTableQueries.deleteById(lessonId)
    }

    suspend fun getTotalStorageUsedBytes(): Long =
        database.downloadTableQueries.totalStorageUsed().executeAsOneOrNull()?.SUM ?: 0L

    // Bookmarks
    fun getBookmarks(userId: String): Flow<List<Bookmark>> =
        database.bookmarkTableQueries.selectByUser(userId).asFlow()
            .mapToList(Dispatchers.Default).map { rows -> rows.map { it.toBookmark() } }

    suspend fun saveBookmark(bookmark: Bookmark) {
        database.bookmarkTableQueries.insertOrReplace(
            id = bookmark.id,
            userId = bookmark.userId,
            courseId = bookmark.courseId,
            lessonId = bookmark.lessonId,
            title = bookmark.title,
            createdAt = bookmark.createdAt
        )
    }

    suspend fun deleteBookmark(bookmarkId: String) {
        database.bookmarkTableQueries.deleteById(bookmarkId)
    }
}

// Extension functions for DB row → Domain mapping
private fun com.learnpulse.db.CourseTable.toCourse(): Course = Course(
    id = id,
    title = title,
    description = description,
    instructor = Instructor(
        id = instructorId,
        name = instructorName,
        avatarUrl = instructorAvatarUrl,
        bio = instructorBio,
        courseCount = instructorCourseCount.toInt(),
        rating = instructorRating
    ),
    thumbnailUrl = thumbnailUrl,
    category = CourseCategory.entries.find { it.name == category } ?: CourseCategory.PROGRAMMING,
    difficulty = Difficulty.entries.find { it.name == difficulty } ?: Difficulty.BEGINNER,
    rating = rating,
    enrolledCount = enrolledCount.toInt(),
    totalDuration = totalDuration,
    lessons = emptyList(),
    price = price,
    isFree = isFree == 1L,
    tags = try { kotlinx.serialization.json.Json.decodeFromString(tags) } catch (e: Exception) { emptyList() }
)

private fun com.learnpulse.db.LessonTable.toLesson(): Lesson = Lesson(
    id = id,
    courseId = courseId,
    title = title,
    type = LessonType.entries.find { it.name == type } ?: LessonType.VIDEO,
    duration = duration,
    contentUrl = contentUrl,
    order = lessonOrder.toInt(),
    isPreview = isPreview == 1L,
    description = description
)

private fun com.learnpulse.db.ProgressTable.toUserProgress(): UserProgress = UserProgress(
    userId = userId,
    courseId = courseId,
    completedLessons = try { kotlinx.serialization.json.Json.decodeFromString(completedLessonsJson) } catch (e: Exception) { emptyList() },
    quizScores = try { kotlinx.serialization.json.Json.decodeFromString(quizScoresJson) } catch (e: Exception) { emptyMap() },
    lastAccessedLessonId = lastAccessedLessonId,
    overallProgress = overallProgress.toFloat(),
    certificateEarned = certificateEarned == 1L,
    streakDays = streakDays.toInt(),
    totalTimeSpentSeconds = totalTimeSpentSeconds
)

private fun com.learnpulse.db.NoteTable.toNote(): Note = Note(
    id = id,
    userId = userId,
    courseId = courseId,
    lessonId = lessonId,
    content = content,
    timestampSeconds = timestampSeconds,
    createdAt = createdAt,
    updatedAt = updatedAt
)

private fun com.learnpulse.db.DownloadTable.toDownloadedLesson(): DownloadedLesson = DownloadedLesson(
    lessonId = lessonId,
    courseId = courseId,
    title = title,
    localFilePath = localFilePath,
    fileSizeBytes = fileSizeBytes,
    downloadedAt = downloadedAt,
    status = DownloadStatus.entries.find { it.name == status } ?: DownloadStatus.QUEUED
)

private fun com.learnpulse.db.BookmarkTable.toBookmark(): Bookmark = Bookmark(
    id = id,
    userId = userId,
    courseId = courseId,
    lessonId = lessonId,
    title = title,
    createdAt = createdAt
)
