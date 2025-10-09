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
import pe.edu.upc.tripmatch.data.remote.AgencyService
import pe.edu.upc.tripmatch.data.repository.AgencyRepository
import pe.edu.upc.tripmatch.presentation.viewmodel.AgencyDashboardViewModel
import pe.edu.upc.tripmatch.presentation.viewmodel.ManageExperiencesViewModel

object PresentationModule {
    private var _manageExperiencesViewModel: ManageExperiencesViewModel? = null
    private lateinit var db: AppDatabase
    private lateinit var expDao: ExperienceDao
    private lateinit var expService: ExperienceService
    private lateinit var authService: AuthService
    private lateinit var expRepository: ExperienceRepository
    private lateinit var authRepository: AuthRepository

    private var _authViewModel: AuthViewModel? = null
    private var _experienceListViewModel: ExperienceListViewModel? = null

    private lateinit var agencyService: AgencyService

    private lateinit var agencyRepository: AgencyRepository

    private var _agencyDashboardViewModel: AgencyDashboardViewModel? = null
    fun init(context: Context) {
        if (!::db.isInitialized) {
            db = Room.databaseBuilder(context, AppDatabase::class.java, "tripmatch_db").build()
            expDao = db.experienceDao()
            expService = ExperienceService.create()
            authService = AuthService.create()

            expRepository = ExperienceRepository(expDao, expService)
            authRepository = AuthRepository(authService, context)
            agencyService = AgencyService.create()
            agencyRepository = AgencyRepository(agencyService)
        }
    }

    fun getAuthViewModel(): AuthViewModel {
        val existing = _authViewModel
        if (existing != null) return existing
        val created = AuthViewModel(authRepository)
        _authViewModel = created
        return created
    }
    fun getManageExperiencesViewModel(): ManageExperiencesViewModel {
        val existing = _manageExperiencesViewModel
        if (existing != null) return existing
        val created = ManageExperiencesViewModel(expRepository, getAuthViewModel())
        _manageExperiencesViewModel = created
        return created
    }
    fun getExperienceListViewModel(): ExperienceListViewModel {
        val existing = _experienceListViewModel
        if (existing != null) return existing
        val created = ExperienceListViewModel(expRepository)
        _experienceListViewModel = created
        return created
    }
    fun getAgencyDashboardViewModel(): AgencyDashboardViewModel {
        val existing = _agencyDashboardViewModel
        if (existing != null) return existing
        val created = AgencyDashboardViewModel(agencyRepository, getAuthViewModel())
        _agencyDashboardViewModel = created
        return created
    }
}
