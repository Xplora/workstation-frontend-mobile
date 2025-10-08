package pe.edu.upc.tripmatch.presentation.di

import android.content.Context
import androidx.room.Room
import pe.edu.upc.tripmatch.data.local.AppDatabase
import pe.edu.upc.tripmatch.data.local.dao.ExperienceDao
import pe.edu.upc.tripmatch.data.remote.AuthService
import pe.edu.upc.tripmatch.data.remote.ExperienceService
import pe.edu.upc.tripmatch.data.repository.AuthRepository
import pe.edu.upc.tripmatch.data.repository.ExperienceRepository
import pe.edu.upc.tripmatch.presentation.viewmodel.AuthViewModel
import pe.edu.upc.tripmatch.presentation.viewmodel.ExperienceListViewModel

object PresentationModule {

    private lateinit var db: AppDatabase
    private lateinit var expDao: ExperienceDao
    private lateinit var expService: ExperienceService
    private lateinit var authService: AuthService
    private lateinit var expRepository: ExperienceRepository
    private lateinit var authRepository: AuthRepository

    fun init(context: Context) {
        db = Room.databaseBuilder(context, AppDatabase::class.java, "tripmatch_db").build()
        expDao = db.experienceDao()
        expService = ExperienceService.create()
        authService = AuthService.create()

        expRepository = ExperienceRepository(expDao, expService)
        authRepository = AuthRepository(authService, context)
    }

    fun getExperienceListViewModel(): ExperienceListViewModel {
        return ExperienceListViewModel(expRepository)
    }
    fun getAuthViewModel(): AuthViewModel {
        return AuthViewModel(authRepository)
    }
}
