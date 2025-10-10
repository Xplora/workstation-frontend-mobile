package pe.edu.upc.tripmatch.data.model

import pe.edu.upc.tripmatch.data.local.entity.FavoriteExperienceEntity
import pe.edu.upc.tripmatch.domain.model.Experience

object ExperienceMapper {
    fun ExperienceDto.toDomain() = Experience(
        id = id,
        title = title,
        description = description,
        location = location,
        duration = duration,
        price = price,
        frequencies = frequencies,
        categoryId = categoryId,
        categoryName = category.name,
        agencyName = agency.agencyName,
        experienceImages = experienceImages.map { it.url },
        includes = includes.map { it.description },
        schedule = schedule.map { it.time }
    )

    fun Experience.toEntity() = FavoriteExperienceEntity(
        id = id,
        title = title,
        description = description,
        location = location,
        duration = duration,
        price = price,
        frequencies = frequencies,
        categoryId = categoryId,
        categoryName = categoryName,
        agencyName = agencyName,
        experienceImages = experienceImages,
        includes = includes,
        schedule = schedule
    )

    fun toCreateCommand(
        title: String,
        description: String,
        location: String,
        duration: Int,
        price: Double,
        frequencies: String,
        scheduleUrls: List<String>,
        imageUrls: List<String>,
        includeDescriptions: List<String>,
        categoryId: Int,
        agencyUserId: String
    ): CreateExperienceCommand {
        return CreateExperienceCommand(
            title = title,
            description = description,
            location = location,
            duration = duration,
            price = price,
            frequencies = frequencies,
            schedules = scheduleUrls.map { Schedule(time = it) },
            experienceImages = imageUrls.map { ExperienceImage(url = it) },
            includes = includeDescriptions.map { Include(description = it) },
            categoryId = categoryId,
            agencyUserId = agencyUserId
        )
    }
}