package com.learnpulse.di

import com.learnpulse.data.local.DatabaseDriverFactory
import com.learnpulse.data.local.LocalDataSource
import com.learnpulse.data.remote.api.LearnPulseApi
import com.learnpulse.data.remote.api.createHttpClient
import com.learnpulse.presentation.coursedetail.CourseDetailViewModel
import com.learnpulse.data.repository.CourseRepositoryImpl
import com.learnpulse.data.repository.DownloadRepositoryImpl
import com.learnpulse.data.repository.NotesRepositoryImpl
import com.learnpulse.data.repository.ProgressRepositoryImpl
import com.learnpulse.data.repository.QuizRepositoryImpl
import com.learnpulse.data.repository.UserRepositoryImpl
import com.learnpulse.db.LearnPulseDatabase
import com.learnpulse.domain.repository.CourseRepository
import com.learnpulse.domain.repository.DownloadRepository
import com.learnpulse.domain.repository.NotesRepository
import com.learnpulse.domain.repository.ProgressRepository
import com.learnpulse.domain.repository.QuizRepository
import com.learnpulse.domain.repository.UserRepository
import com.learnpulse.domain.usecase.DownloadLessonUseCase
import com.learnpulse.domain.usecase.GenerateQuizUseCase
import com.learnpulse.domain.usecase.GetCourseDetailUseCase
import com.learnpulse.domain.usecase.GetCoursesUseCase
import com.learnpulse.domain.usecase.GetNotesUseCase
import com.learnpulse.domain.usecase.GetProgressUseCase
import com.learnpulse.domain.usecase.SearchCoursesUseCase
import com.learnpulse.domain.usecase.TrackProgressUseCase
import com.learnpulse.presentation.aipractice.AiPracticeViewModel
import com.learnpulse.presentation.catalog.CatalogViewModel
import com.learnpulse.presentation.downloads.DownloadsViewModel
import com.learnpulse.presentation.home.HomeViewModel
import com.learnpulse.presentation.notes.NotesViewModel
import com.learnpulse.presentation.player.PlayerViewModel
import com.learnpulse.presentation.profile.ProfileViewModel
import com.learnpulse.presentation.progress.ProgressViewModel
import com.learnpulse.presentation.quiz.QuizViewModel
import org.koin.dsl.module

val networkModule = module {
    single { createHttpClient(get()) }
    single { LearnPulseApi(get()) }
}

val databaseModule = module {
    single { get<DatabaseDriverFactory>().createDriver() }
    single { LearnPulseDatabase(get()) }
    single { LocalDataSource(get()) }
}

val repositoryModule = module {
    single<CourseRepository> { CourseRepositoryImpl(get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<ProgressRepository> { ProgressRepositoryImpl(get(), get()) }
    single<NotesRepository> { NotesRepositoryImpl(get()) }
    single<QuizRepository> { QuizRepositoryImpl(get()) }
    single<DownloadRepository> { DownloadRepositoryImpl(get()) }
}

val useCaseModule = module {
    factory { GetCoursesUseCase(get()) }
    factory { GetCourseDetailUseCase(get()) }
    factory { SearchCoursesUseCase(get()) }
    factory { TrackProgressUseCase(get()) }
    factory { GenerateQuizUseCase(get()) }
    factory { GetProgressUseCase(get()) }
    factory { DownloadLessonUseCase(get()) }
    factory { GetNotesUseCase(get()) }
}

val viewModelModule = module {
    factory { HomeViewModel(get(), get(), get()) }
    factory { CatalogViewModel(get()) }
    factory { CourseDetailViewModel(get(), get(), get()) }
    factory { PlayerViewModel(get(), get(), get()) }
    factory { QuizViewModel(get(), get(), get()) }
    factory { AiPracticeViewModel(get()) }
    factory { ProgressViewModel(get(), get(), get()) }
    factory { NotesViewModel(get(), get()) }
    factory { DownloadsViewModel(get()) }
    factory { ProfileViewModel(get()) }
}

val commonModules = listOf(
    networkModule,
    databaseModule,
    mockRepositoryModule, // swap ↔ repositoryModule to use real API
    useCaseModule,
    viewModelModule
)
