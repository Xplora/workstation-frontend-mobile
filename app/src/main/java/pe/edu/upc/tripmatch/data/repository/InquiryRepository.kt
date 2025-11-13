package pe.edu.upc.tripmatch.data.repository

import android.util.Log
import pe.edu.upc.tripmatch.data.model.CreateResponseCommand
import pe.edu.upc.tripmatch.data.model.InquiryMapper.toDomain
import pe.edu.upc.tripmatch.data.remote.InquiryService
import pe.edu.upc.tripmatch.domain.model.Query

class InquiryRepository(
    private val inquiryService: InquiryService
) {
    suspend fun sendResponse(command: CreateResponseCommand) {
        val response = inquiryService.createResponse(command)
        if (!response.isSuccessful) {
            throw Exception("Fallo al enviar la respuesta. Código: ${response.code()}")
        }
    }
    suspend fun getAgencyInquiries(agencyId: String): List<Query> {
        val inquiries = inquiryService.getAgencyInquiries(agencyId)
        inquiries.forEach {
            Log.d(
                "InquiryCheck",
                "Inquiry ${it.id} -> isAnswered = ${it.isAnswered}, answer = ${it.answer}"
            )
        }
        return inquiries.map { it.toDomain() }
    }
}