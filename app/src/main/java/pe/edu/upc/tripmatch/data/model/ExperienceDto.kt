package pe.edu.upc.tripmatch.data.model

data class ExperienceDto(
    val agency: Agency,
    val category: Category,
    val categoryId: Int,
    val description: String,
    val duration: Int,
    val experienceImages: List<ExperienceImage>,
    val frequencies: String,
    val id: Int,
    val includes: List<Include>,
    val location: String,
    val price: Double,
    val schedule: List<Schedule>,
    val title: String
)