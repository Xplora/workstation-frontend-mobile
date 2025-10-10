package pe.edu.upc.tripmatch.data.model

import pe.edu.upc.tripmatch.domain.model.Booking

fun BookingDto.toDomain(traveler: UserDetailsDto, experience: ExperienceSummaryDto): Booking {
    return Booking(
        travelerName = "${traveler.firstName} ${traveler.lastName}",
        travelerImage = traveler.avatarUrl,
        experienceName = experience.title,
        date = this.bookingDate,
        people = this.numberOfPeople,
        totalPaid = this.price
    )
}