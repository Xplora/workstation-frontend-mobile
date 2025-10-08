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
        categoryName = categoryName,
        agencyName = agencyName
    )
}