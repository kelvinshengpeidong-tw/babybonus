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
    fun handleChildNotFoundException(): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(
                message = ErrorMessages.CHILD_NOT_FOUND
            )
        )
    }

    @ExceptionHandler(ParentNotFoundException::class)
    fun handleParentNotFoundException(): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(
                message = ErrorMessages.PARENT_NOT_FOUND
            )
        )
    }

    @ExceptionHandler(DuplicateEnrollmentException::class)
    fun handleDuplicateEnrollmentException(): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            ErrorResponse(
                message = ErrorMessages.ENROLLMENT_ALREADY_EXISTS
            )
        )
    }

    @ExceptionHandler(EnrollmentNotFoundException::class)
    fun handleEnrollmentNotFoundException(): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(
                message = ErrorMessages.ENROLLMENT_NOT_FOUND
            )
        )
    }

    @ExceptionHandler(InvalidNricException::class)
    fun handleInvalidNricException(): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                message = ErrorMessages.INVALID_NRIC
            )
        )
    }

    @ExceptionHandler(DataLoadException::class)
    fun handleDataLoadException(): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse(
                message = ErrorMessages.INTERNAL_SERVER_ERROR
            )
        )
    }

}