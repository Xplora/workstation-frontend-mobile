package pe.edu.upc.tripmatch.data.model

data class CreateExperienceCommand(
    val title: String,
    val description: String,
    val location: String,
    val duration: Int,
    val price: Double,
    val frequencies: String,
    val schedules: List<Schedule>,
    val experienceImages: List<ExperienceImage>,
    val includes: List<Include>,
    val categoryId: Int,
    val agencyUserId: String
)