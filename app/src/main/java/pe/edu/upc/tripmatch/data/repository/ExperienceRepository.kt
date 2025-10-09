package pe.edu.upc.tripmatch.data.repository

import pe.edu.upc.tripmatch.data.local.dao.ExperienceDao
import pe.edu.upc.tripmatch.data.model.ExperienceMapper.toDomain
import pe.edu.upc.tripmatch.data.model.ExperienceMapper.toEntity
import pe.edu.upc.tripmatch.data.remote.ExperienceService
import pe.edu.upc.tripmatch.domain.model.Experience

class ExperienceRepository(
    private val experienceDao: ExperienceDao,
    private val experienceService: ExperienceService
) {


    suspend fun getExperiences(): List<Experience> {
        val apiExperiences = experienceService.getExperiences()
        return apiExperiences.map { dto ->
            val domain = dto.toDomain()
            domain.isFavorite = isFavorite(domain.id)
            domain
        }
    }

    suspend fun addFavorite(experience: Experience) {
        experienceDao.insertFavorite(experience.toEntity())
    }

    suspend fun removeFavorite(experience: Experience) {
        experienceDao.deleteFavorite(experience.toEntity())
    }

    suspend fun getFavorites(): List<Experience> {
        return experienceDao.getAllFavorites().map { entity ->
            Experience(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                location = entity.location,
                duration = entity.duration,
                price = entity.price,
                frequencies = "",
                categoryId = 0,
                categoryName = entity.categoryName,
                agencyName = entity.agencyName,
                isFavorite = true
            )
        }
    }

    suspend fun isFavorite(id: Int): Boolean {
        return experienceDao.getFavoriteById(id) != null
    }
    suspend fun getExperiencesForAgency(agencyId: String): List<Experience> {
        val apiExperiences = experienceService.getExperiencesByAgencyId(agencyId)
        return apiExperiences.map { dto ->
            val domain = dto.toDomain()
            domain.isFavorite = isFavorite(domain.id)
            domain
        }
    }
}