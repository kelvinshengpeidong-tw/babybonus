package com.tw.babybonus.exception

import com.tw.babybonus.exception.dto.response.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    //***error messages in this class should be more generic to avoid exposing implementation details***

    @ExceptionHandler(ChildNotFoundException::class)
    fun handleChildNotFoundException(e: ChildNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(
                message = "Child not found"
            )
        )
    }

    @ExceptionHandler(ParentNotFoundException::class)
    fun handleParentNotFoundException(e: ParentNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(
                message = "Parent not found"
            )
        )
    }

    @ExceptionHandler(DuplicateEnrollmentException::class)
    fun handleDuplicateEnrollmentException(e: DuplicateEnrollmentException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            ErrorResponse(
                message = "Enrollment already exists"
            )
        )
    }

    @ExceptionHandler(EnrollmentNotFoundException::class)
    fun handleEnrollmentNotFoundException(e: EnrollmentNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(
                message = "Enrollment not found"
            )
        )
    }

    @ExceptionHandler(InvalidNricException::class)
    fun handleInvalidNricException(e: InvalidNricException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                message = "Invalid NRIC"
            )
        )
    }

    @ExceptionHandler(DataLoadException::class)
    fun handleDataLoadException(e: DataLoadException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse(
                message = "An internal server error has occurred"
            )
        )
    }

}