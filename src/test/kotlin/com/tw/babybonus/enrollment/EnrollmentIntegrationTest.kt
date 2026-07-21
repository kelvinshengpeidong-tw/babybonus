package com.tw.babybonus.enrollment

import com.tw.babybonus.disbursement.domain.DisbursementStatus
import com.tw.babybonus.disbursement.domain.DisbursementType
import com.tw.babybonus.disbursement.repository.DisbursementRepository
import com.tw.babybonus.enrollment.domain.EnrollmentStatus
import com.tw.babybonus.enrollment.dto.request.CreateEnrollmentRequest
import com.tw.babybonus.enrollment.dto.response.EnrollmentCreatedResponse
import com.tw.babybonus.enrollment.repository.EnrollmentRepository
import com.tw.babybonus.enrollment.service.BabyBonusConstants
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
import org.springframework.http.MediaType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.ObjectMapper
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EnrollmentIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var enrollmentRepository: EnrollmentRepository

    @Autowired
    private lateinit var disbursementRepository: DisbursementRepository

    private val postUrl: String = "/api/v1/enrollments"

    @Test
    fun `should enroll and mark status as ENROLLED for a SINGAPORE_CITIZEN child with a PROCESSED cash gift disbursement initiated`() {

        val postRequest = CreateEnrollmentRequest(
            childNric = "T2400001A", // Tan Wei Xuan, SINGAPORE_CITIZEN
            parentNric = "S8001234A"  // Tan Ah Kow
        )

        val postRequestJson = objectMapper.writeValueAsString(postRequest)

        //1. POST enrollment
        val postResult = mockMvc.perform(
            post(postUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(postRequestJson)
        ).andExpect(
            status().isCreated
        ).andReturn()

        //deserialize to EnrollmentCreatedResponse object
        val enrollmentCreatedResponse = objectMapper.readValue(
            postResult.response.contentAsString,
            EnrollmentCreatedResponse::class.java
        )

        val enrollmentId = enrollmentCreatedResponse.enrollmentId

        //2. verify enrollment has been saved in repository
        val savedEnrollment = enrollmentRepository.findById(enrollmentId).orElseThrow()
        assertEquals(enrollmentId, savedEnrollment.id)
        assertEquals(postRequest.childNric, savedEnrollment.childNric)
        assertEquals(postRequest.parentNric, savedEnrollment.parentNric)
        assertEquals(EnrollmentStatus.ENROLLED, savedEnrollment.status)
        assertNotNull(savedEnrollment.enrolledAt)
        assertNotNull(savedEnrollment.createdAt)

        //3. verify disbursement has been saved in repository
        val savedDisbursements = disbursementRepository.findAllByEnrollmentId(enrollmentId)
        assertEquals(1, savedDisbursements.size)
        assertEquals(enrollmentId, savedDisbursements[0].enrollmentId)
        assertEquals(DisbursementType.CASH_GIFT, savedDisbursements[0].type)
        assertEquals(BabyBonusConstants.CASH_GIFT_AMOUNT_AT_BIRTH, savedDisbursements[0].amount)
        assertEquals(DisbursementStatus.PROCESSED, savedDisbursements[0].status)
        assertNotNull(savedDisbursements[0].processedAt)

        //4. GET the enrollment through API endpoint to verify its response
        val getUrl = "/api/v1/enrollments/${enrollmentId}"

        mockMvc.perform(
            get(getUrl)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            status().isOk
        ).andExpect(
            jsonPath("$.id").value(enrollmentId.toString())
        ).andExpect(
            jsonPath("$.childNric").value("T240****A")
        ).andExpect(
            jsonPath("$.status").value(EnrollmentStatus.ENROLLED.name)
        ).andExpect(
            jsonPath("$.enrolledAt", notNullValue())
        ).andExpect(
            jsonPath("$.disbursement.type").value(DisbursementType.CASH_GIFT.name)
        ).andExpect(
            jsonPath("$.disbursement.amount").value(BabyBonusConstants.CASH_GIFT_AMOUNT_AT_BIRTH.toDouble())
        ).andExpect(
            jsonPath("$.disbursement.status").value(DisbursementStatus.PROCESSED.name)
        )
    }

    @Test
    fun `should enroll but mark status as INELIGIBLE for a non-citizen child with no disbursement initiated`() {

        val postRequest = CreateEnrollmentRequest(
            childNric = "T2400002B", // Lee Hui Ling, PERMANENT_RESIDENT
            parentNric = "S8205678B"  // Lin Mei Ling
        )

        val postRequestJson = objectMapper.writeValueAsString(postRequest)

        //1. POST enrollment
        val postResult = mockMvc.perform(
            post(postUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(postRequestJson)
        ).andExpect(
            status().isCreated
        ).andReturn()

        //deserialize to EnrollmentCreatedResponse object
        val enrollmentCreatedResponse = objectMapper.readValue(
            postResult.response.contentAsString,
            EnrollmentCreatedResponse::class.java
        )

        val enrollmentId = enrollmentCreatedResponse.enrollmentId

        //2. verify enrollment has been saved in repository
        val savedEnrollment = enrollmentRepository.findById(enrollmentId).orElseThrow()
        assertEquals(enrollmentId, savedEnrollment.id)
        assertEquals(postRequest.childNric, savedEnrollment.childNric)
        assertEquals(postRequest.parentNric, savedEnrollment.parentNric)
        assertEquals(EnrollmentStatus.INELIGIBLE, savedEnrollment.status)
        assertNull(savedEnrollment.enrolledAt)
        assertNotNull(savedEnrollment.createdAt)

        //3. verify no disbursement was saved in repository for this INELIGIBLE enrollment
        val savedDisbursements = disbursementRepository.findAllByEnrollmentId(enrollmentId)
        assertEquals(0, savedDisbursements.size)

        //4. GET the enrollment through API endpoint to verify its response
        val getUrl = "/api/v1/enrollments/${enrollmentId}"

        mockMvc.perform(
            get(getUrl)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            status().isOk
        ).andExpect(
            jsonPath("$.id").value(enrollmentId.toString())
        ).andExpect(
            jsonPath("$.childNric").value("T240****B")
        ).andExpect(
            jsonPath("$.status").value(EnrollmentStatus.INELIGIBLE.name)
        ).andExpect(
            jsonPath("$.enrolledAt", nullValue())
        ).andExpect(
            jsonPath("$.disbursement", nullValue())
        )
    }

}