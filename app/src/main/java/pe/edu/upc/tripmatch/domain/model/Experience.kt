package pe.edu.upc.tripmatch.domain.model

data class Experience(
    val id: Int,
    val title: String,
    val description: String,
    val location: String,
    val duration: Int,
    val price: Double,
    val frequencies: String,
    val categoryId: Int,
    val categoryName: String,
    val agencyName: String,
    val experienceImages: List<String> = emptyList(),
    val includes: List<String> = emptyList(),
    val schedule: List<String> = emptyList(),
    var isFavorite: Boolean = false
)