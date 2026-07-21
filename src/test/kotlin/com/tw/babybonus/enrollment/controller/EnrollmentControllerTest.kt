package com.tw.babybonus.enrollment.controller

import com.tw.babybonus.enrollment.dto.request.CreateEnrollmentRequest
import com.tw.babybonus.enrollment.dto.response.EnrollmentCreatedResponse
import com.tw.babybonus.enrollment.service.EnrollmentService
import com.tw.babybonus.exception.ChildNotFoundException
import com.tw.babybonus.exception.DuplicateEnrollmentException
import com.tw.babybonus.exception.GlobalExceptionHandler
import com.tw.babybonus.exception.InvalidNricException
import com.tw.babybonus.exception.ParentNotFoundException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.databind.ObjectMapper
import java.util.UUID

@WebMvcTest(EnrollmentController::class)
@Import(GlobalExceptionHandler::class)
class EnrollmentControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var enrollmentService: EnrollmentService

    @Nested
    inner class Post {

        private val postUrl: String = "/api/v1/enrollments"

        @Test
        fun `should return http status 201 with enrollment id when request is valid`() {

            val request = CreateEnrollmentRequest(
                childNric = "T1234567A",
                parentNric = "S0099887B"
            )

            val enrollmentId = UUID.randomUUID()
            val requestJson: String = objectMapper.writeValueAsString(request)

            whenever(enrollmentService.enroll(request))
                .thenReturn(EnrollmentCreatedResponse(enrollmentId))

            mockMvc.perform(
                post(postUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
            ).andExpect(
                status().isCreated
            ).andExpect(
                jsonPath("$.enrollmentId").value(enrollmentId.toString())
            )
        }

        @Test
        fun `should return http status 400 when childNric is missing`() {

            val invalidJsonRequest: String = """{ "parentNric": "S0099887B" }"""

            mockMvc.perform(
                post(postUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJsonRequest)
            ).andExpect(
                status().isBadRequest
            )
        }

        @Test
        fun `should return http status 400 when parentNric is missing`() {

            val invalidJsonRequest: String = """{ "childNric": "T1234567A" }"""

            mockMvc.perform(
                post(postUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJsonRequest)
            ).andExpect(
                status().isBadRequest
            )
        }

        @Test
        fun `should return http status 400 when request body is malformed JSON`() {

            val invalidJsonRequest: String = """{ "childNric": "T1234567A", """

            mockMvc.perform(
                post(postUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJsonRequest)
            ).andExpect(
                status().isBadRequest
            )
        }

        @Test
        fun `should return http status 400 when NRIC is invalid`() {
            val request = CreateEnrollmentRequest(
                childNric = " T1267A",
                parentNric = "S0099887B"
            )

            val requestJson: String = objectMapper.writeValueAsString(request)

            whenever(enrollmentService.enroll(request))
                .thenThrow(InvalidNricException("Invalid NRIC"))

            mockMvc.perform(
                post(postUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
            ).andExpect(
                status().isBadRequest
            )
        }

        @Test
        fun `should return http status 404 when child is not found`() {

            val request = CreateEnrollmentRequest(
                childNric = "T1111111A",
                parentNric = "S0099887B"
            )

            val requestJson: String = objectMapper.writeValueAsString(request)

            whenever(enrollmentService.enroll(request))
                .thenThrow(ChildNotFoundException())

            mockMvc.perform(
                post(postUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
            ).andExpect(
                status().isNotFound
            )
        }

        @Test
        fun `should return http status 404 when parent is not found`() {

            val request = CreateEnrollmentRequest(
                childNric = "T1234567A",
                parentNric = "S1111111B"
            )

            val requestJson: String = objectMapper.writeValueAsString(request)

            whenever(enrollmentService.enroll(request))
                .thenThrow(ParentNotFoundException())

            mockMvc.perform(
                post(postUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
            ).andExpect(
                status().isNotFound
            )
        }

        @Test
        fun `should return http status 409 when enrollment already exists for child`() {
            val request = CreateEnrollmentRequest(
                childNric = "T1234567A",
                parentNric = "S0099887B"
            )

            val requestJson: String = objectMapper.writeValueAsString(request)

            whenever(enrollmentService.enroll(request))
                .thenThrow(DuplicateEnrollmentException())

            mockMvc.perform(
                post(postUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
            ).andExpect(
                status().isConflict
            )
        }
    }

    @Nested
    inner class Get {

    }
}