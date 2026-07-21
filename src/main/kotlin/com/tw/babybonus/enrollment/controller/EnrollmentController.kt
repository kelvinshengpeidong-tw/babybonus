package com.tw.babybonus.enrollment.controller

import com.tw.babybonus.enrollment.dto.request.CreateEnrollmentRequest
import com.tw.babybonus.enrollment.dto.response.EnrollmentCreatedResponse
import com.tw.babybonus.enrollment.dto.response.EnrollmentGetResponse
import com.tw.babybonus.enrollment.service.EnrollmentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/enrollments")
class EnrollmentController(
    private val enrollmentService: EnrollmentService
) {

    @PostMapping
    fun enroll(@RequestBody request: CreateEnrollmentRequest): ResponseEntity<EnrollmentCreatedResponse> {
        val enrollmentCreatedResponse: EnrollmentCreatedResponse = enrollmentService.enroll(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentCreatedResponse)
    }

    @GetMapping("/{id}")
    fun getEnrollment(@PathVariable id: UUID): ResponseEntity<EnrollmentGetResponse> {
        val response = enrollmentService.getEnrollment(id)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }
}