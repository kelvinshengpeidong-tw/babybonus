package com.tw.babybonus.enrollment.service

import com.tw.babybonus.disbursement.domain.Disbursement
import com.tw.babybonus.disbursement.domain.DisbursementStatus
import com.tw.babybonus.disbursement.domain.DisbursementType
import com.tw.babybonus.disbursement.dto.DisbursementResponse
import com.tw.babybonus.disbursement.repository.DisbursementRepository
import com.tw.babybonus.enrollment.domain.Enrollment
import com.tw.babybonus.enrollment.domain.EnrollmentStatus
import com.tw.babybonus.enrollment.dto.request.EnrollmentRequest
import com.tw.babybonus.enrollment.dto.response.EnrollmentResponse
import com.tw.babybonus.enrollment.repository.EnrollmentRepository
import com.tw.babybonus.exception.ChildNotFoundException
import com.tw.babybonus.exception.DuplicateEnrollmentException
import com.tw.babybonus.exception.ParentNotFoundException
import com.tw.babybonus.ica.client.IcaClient
import com.tw.babybonus.iroas.client.IroasClient
import com.tw.babybonus.privacy.DataMasker
import com.tw.babybonus.shared.Citizenship
import com.tw.babybonus.validator.NricValidator
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class EnrollmentService(
    private val enrollmentRepository: EnrollmentRepository,
    private val disbursementRepository: DisbursementRepository,
    private val icaClient: IcaClient,
    private val iroasClient: IroasClient,
) {

    //EnrollmentService is expected to return the masked NRIC in the response

    fun enroll(request: EnrollmentRequest): EnrollmentResponse {

        //normalize and validate the format of the child and parent NRIC
        val normalizedChildNric: String = NricValidator.formatAndValidateNric(request.childNric)
        val normalizedParentNric: String = NricValidator.formatAndValidateNric(request.parentNric)

        //flag to determine the enrollment status
        val eligible: Boolean = isEligibleForEnrollment(request)

        //create pending enrollment for child
        val enrollment = Enrollment(
            childNric = normalizedChildNric,
            parentNric = normalizedParentNric,
            status = EnrollmentStatus.PENDING,
            enrolledAt = null,
            createdAt = Instant.now()
        )

        var disbursement: Disbursement? = null
        var disbursementResponse: DisbursementResponse? = null

        //change enrollment status depending on eligibility
        if(eligible) {
            enrollment.status = EnrollmentStatus.ENROLLED
            enrollment.enrolledAt = Instant.now()
            enrollmentRepository.save(enrollment)

            //initiate a cash gift disbursement of $3,000
            disbursement = createCashGift(enrollment)

            disbursementResponse = DisbursementResponse(
                type = disbursement.type,
                amount = disbursement.amount,
                status = disbursement.status
            )
        }
        else {
            enrollment.status = EnrollmentStatus.INELIGIBLE
            //enrolledAt will remain null since enrollment is not successful
            enrollmentRepository.save(enrollment)

            //disbursement and disbursementResponse will also remain null
        }

        //child NRIC will be masked in response
        val enrollmentResponse = EnrollmentResponse(
            id = enrollment.id,
            childNric = DataMasker.maskNric(enrollment.childNric),
            status = enrollment.status,
            enrolledAt = enrollment.enrolledAt,
            disbursement = disbursementResponse
        )

        return enrollmentResponse
    }

    private fun isEligibleForEnrollment(request: EnrollmentRequest): Boolean {

        //*** checks 1 to 3 will check if inputs are valid. Invalid inputs will throw exception ***//

        //1. check if child exists in ICA records
        val child = icaClient.findChildByNric(request.childNric)
            ?: throw ChildNotFoundException()

        //2. check if parent/guardian exists in IROAS records
        val parent = iroasClient.findParentByNric(request.parentNric)
            ?: throw ParentNotFoundException()

        //3. check if child has not been enrolled successfully before and do not have a pending record
        //only continue with enrollment process if no record found or existing record(s) have INELIGIBLE status
        if(enrollmentRepository.existsByChildNricAndStatusIn(
                child.nric,
                    listOf(
                                EnrollmentStatus.PENDING,
                                EnrollmentStatus.ENROLLED
                            )
        )) {
            throw DuplicateEnrollmentException()
        }

        //4. check if child is Singapore Citizen
        if(child.citizenship != Citizenship.SINGAPORE_CITIZEN) {
            return false
        }

        return true
    }

    private fun createCashGift(enrollment : Enrollment): Disbursement {
        val disbursement = Disbursement(
            enrollmentId = enrollment.id,
            type = DisbursementType.CASH_GIFT,
            amount = BabyBonusConstants.CASH_GIFT_AMOUNT_AT_BIRTH,
            status = DisbursementStatus.PROCESSED,
            processedAt = Instant.now()
        )

        return disbursementRepository.save(disbursement)
    }

}