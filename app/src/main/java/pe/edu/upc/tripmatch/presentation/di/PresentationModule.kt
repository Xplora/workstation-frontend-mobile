package pe.edu.upc.tripmatch.presentation.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import pe.edu.upc.tripmatch.data.local.AppDatabase
import pe.edu.upc.tripmatch.data.local.dao.ExperienceDao
import pe.edu.upc.tripmatch.data.remote.AuthService
import pe.edu.upc.tripmatch.data.remote.ExperienceService
import pe.edu.upc.tripmatch.data.repository.AuthRepository
import pe.edu.upc.tripmatch.data.repository.ExperienceRepository
import pe.edu.upc.tripmatch.presentation.viewmodel.AuthViewModel
import pe.edu.upc.tripmatch.presentation.viewmodel.TouristDashboardViewModel
import pe.edu.upc.tripmatch.data.remote.AgencyService
import pe.edu.upc.tripmatch.data.remote.CategoryService
import pe.edu.upc.tripmatch.data.remote.InquiryService
import pe.edu.upc.tripmatch.data.repository.AgencyRepository
import pe.edu.upc.tripmatch.data.repository.InquiryRepository
import pe.edu.upc.tripmatch.presentation.viewmodel.AgencyDashboardViewModel
import pe.edu.upc.tripmatch.presentation.viewmodel.AgencyProfileViewModel
import pe.edu.upc.tripmatch.presentation.viewmodel.BookingsViewModel
import pe.edu.upc.tripmatch.presentation.viewmodel.CreateExperienceViewModel
import pe.edu.upc.tripmatch.presentation.viewmodel.EditAgencyProfileViewModel
import pe.edu.upc.tripmatch.presentation.viewmodel.ManageExperiencesViewModel
import pe.edu.upc.tripmatch.presentation.viewmodel.QueriesViewModel

object PresentationModule {
    private var _bookingsViewModel: BookingsViewModel? = null
    private var _manageExperiencesViewModel: ManageExperiencesViewModel? = null
    private var _createExperienceViewModel: CreateExperienceViewModel? = null
    private lateinit var db: AppDatabase
    private lateinit var expDao: ExperienceDao
    private lateinit var expService: ExperienceService

    private lateinit var inquiryService: InquiryService

    private var _editAgencyProfileViewModel: EditAgencyProfileViewModel? = null
    private lateinit var categoryService: CategoryService
    private lateinit var authService: AuthService
    private lateinit var expRepository: ExperienceRepository
    private lateinit var authRepository: AuthRepository

    private lateinit var inquiryRepository: InquiryRepository

    private var _authViewModel: AuthViewModel? = null
    private var _touristDashboardViewModel: TouristDashboardViewModel? = null

    private lateinit var agencyService: AgencyService

    private lateinit var agencyRepository: AgencyRepository

    private var _agencyDashboardViewModel: AgencyDashboardViewModel? = null
    private var _agencyProfileViewModel: AgencyProfileViewModel? = null

    private var _queriesViewModel: QueriesViewModel? = null

    fun init(context: Context) {
        if (!::db.isInitialized) {
            db = Room.databaseBuilder(context, AppDatabase::class.java, "tripmatch_db").fallbackToDestructiveMigration().build()
            expDao = db.experienceDao()

            expService = ExperienceService.create(context)
            authService = AuthService.create()
            categoryService = CategoryService.create(context)
            inquiryService = InquiryService.create(context)

            expRepository = ExperienceRepository(expDao, expService, categoryService)
            authRepository = AuthRepository(authService, context)
            expRepository = ExperienceRepository(expDao, expService, categoryService)
            agencyService = AgencyService.create()
            agencyRepository = AgencyRepository(agencyService)
            inquiryRepository = InquiryRepository(inquiryService)
        }
    }
    fun onLogout() {

        _agencyDashboardViewModel = null
        _manageExperiencesViewModel = null
        _touristDashboardViewModel = null
        _agencyProfileViewModel = null
        _editAgencyProfileViewModel = null
        _bookingsViewModel = null
        _queriesViewModel = null
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
    fun getTouristDashboardViewModel(): TouristDashboardViewModel {
        val existing = _touristDashboardViewModel
        if (existing != null) return existing

        val created = TouristDashboardViewModel(expRepository, getAuthViewModel())

        _touristDashboardViewModel = created
        return created
    }
    fun getAgencyDashboardViewModel(): AgencyDashboardViewModel {
        val existing = _agencyDashboardViewModel
        if (existing != null) return existing
        val created = AgencyDashboardViewModel(agencyRepository, getAuthViewModel())
        _agencyDashboardViewModel = created
        return created
    }
    fun getEditAgencyProfileViewModel(): EditAgencyProfileViewModel {
        val existing = _editAgencyProfileViewModel
        if (existing != null) return existing
        val created = EditAgencyProfileViewModel(agencyRepository, getAuthViewModel())
        _editAgencyProfileViewModel = created
        return created
    }
    fun getCreateExperienceViewModel(): CreateExperienceViewModel {
        val existing = _createExperienceViewModel
        if (existing != null) return existing
        val created = CreateExperienceViewModel(expRepository, getAuthViewModel())
        _createExperienceViewModel = created
        return created
    }
    fun getAgencyProfileViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(AgencyProfileViewModel::class.java)) {
                    return AgencyProfileViewModel(agencyRepository, getAuthViewModel()) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
    fun getAgencyProfileViewModel(): AgencyProfileViewModel {
        val existing = _agencyProfileViewModel
        if (existing != null) return existing
        val created = AgencyProfileViewModel(agencyRepository, getAuthViewModel())
        _agencyProfileViewModel = created
        return created
    }
    fun getBookingsViewModel(): BookingsViewModel {
        val existing = _bookingsViewModel
        if (existing != null) return existing

        val created = BookingsViewModel(agencyRepository, getAuthViewModel())
        _bookingsViewModel = created
        return created
    }

    fun getQueriesViewModel(): QueriesViewModel {
        val existing = _queriesViewModel
        if (existing != null) return existing
        val created = QueriesViewModel(inquiryRepository, getAuthViewModel())
        _queriesViewModel = created
        return created
    }
}