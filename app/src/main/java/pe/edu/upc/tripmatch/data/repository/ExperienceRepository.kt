package pe.edu.upc.tripmatch.data.repository

import pe.edu.upc.tripmatch.data.local.dao.ExperienceDao
import pe.edu.upc.tripmatch.data.model.CreateExperienceCommand
import pe.edu.upc.tripmatch.data.model.ExperienceMapper.toDomain
import pe.edu.upc.tripmatch.data.model.ExperienceMapper.toEntity
import pe.edu.upc.tripmatch.data.remote.CategoryService
import pe.edu.upc.tripmatch.data.remote.ExperienceService
import pe.edu.upc.tripmatch.domain.model.Experience

class ExperienceRepository(
    private val experienceDao: ExperienceDao,
    private val experienceService: ExperienceService,
    private val categoryService: CategoryService
) {

    suspend fun getCategories(): List<String> {
        return categoryService.getAllCategories().map { it.name }
    }
    suspend fun getExperiences(): List<Experience> {
        val apiExperiences = experienceService.getExperiences()
        return apiExperiences.map { dto ->
            val domain = dto.toDomain()
            domain.isFavorite = isFavorite(domain.id)
            domain
        }
    }

    suspend fun deleteExperience(experienceId: Int) {
        val response = experienceService.deleteExperience(experienceId)
        if (!response.isSuccessful) {
            throw Exception("Fallo al eliminar la experiencia. Código: ${response.code()}")
        }

    }

    suspend fun createExperience(command: CreateExperienceCommand) {
        val response = experienceService.createExperience(command)
        if (!response.isSuccessful) {
            throw Exception("Fallo al crear la experiencia. Código: ${response.code()}")
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
                frequencies = entity.frequencies,
                categoryId = entity.categoryId,
                categoryName = entity.categoryName,
                agencyName = entity.agencyName,

                experienceImages = entity.experienceImages,
                schedule = entity.schedule,
                includes = entity.includes,
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

    suspend fun updateExperience(experienceId: Int, command: CreateExperienceCommand) {
        val response = experienceService.updateExperience(experienceId, command)
        if (!response.isSuccessful) {
            throw Exception("Fallo al actualizar la experiencia. Código: ${response.code()}")
        }
    }

}