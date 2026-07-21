package com.tw.babybonus.enrollment.service

import com.tw.babybonus.disbursement.domain.Disbursement
import com.tw.babybonus.disbursement.domain.DisbursementStatus
import com.tw.babybonus.disbursement.domain.DisbursementType
import com.tw.babybonus.disbursement.dto.DisbursementResponse
import com.tw.babybonus.disbursement.repository.DisbursementRepository
import com.tw.babybonus.enrollment.domain.Enrollment
import com.tw.babybonus.enrollment.domain.EnrollmentStatus
import com.tw.babybonus.enrollment.dto.request.CreateEnrollmentRequest
import com.tw.babybonus.enrollment.dto.response.EnrollmentCreatedResponse
import com.tw.babybonus.enrollment.dto.response.EnrollmentGetResponse
import com.tw.babybonus.enrollment.repository.EnrollmentRepository
import com.tw.babybonus.exception.ChildNotFoundException
import com.tw.babybonus.exception.DuplicateEnrollmentException
import com.tw.babybonus.exception.EnrollmentNotFoundException
import com.tw.babybonus.exception.ParentNotFoundException
import com.tw.babybonus.ica.client.IcaClient
import com.tw.babybonus.iroas.client.IroasClient
import com.tw.babybonus.privacy.DataMasker
import com.tw.babybonus.shared.Citizenship
import com.tw.babybonus.validator.NricValidator
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class EnrollmentService(
    private val enrollmentRepository: EnrollmentRepository,
    private val disbursementRepository: DisbursementRepository,
    private val icaClient: IcaClient,
    private val iroasClient: IroasClient,
) {

    //method to do the enrollment and returns back the enrollment created response if enrollment is created
    fun enroll(request: CreateEnrollmentRequest): EnrollmentCreatedResponse {

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

        //change enrollment status depending on eligibility
        if(eligible) {
            enrollment.status = EnrollmentStatus.ENROLLED
            enrollment.enrolledAt = Instant.now()
            enrollmentRepository.save(enrollment)

            //initiate a cash gift disbursement of $3,000
            createCashGift(enrollment)
        }
        else {
            enrollment.status = EnrollmentStatus.INELIGIBLE
            //enrolledAt will remain null since enrollment is not successful
            enrollmentRepository.save(enrollment)

            //no disbursement is created
        }

        val enrollmentCreatedResponse = EnrollmentCreatedResponse(
            enrollment.id
        )

        return enrollmentCreatedResponse
    }

    //method to get the enrollment and returns back the response body
    fun getEnrollment(enrollmentId: UUID): EnrollmentGetResponse {

        val enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow { EnrollmentNotFoundException() }

        val disbursements: List<Disbursement> = disbursementRepository.findAllByEnrollmentId(enrollmentId)

        //account for empty list when enrollment status is INELIGIBLE
        //if not null, get the first disbursement in list which should be CASH_GIFT_AT_BIRTH and set values for the DisbursementResponse
        val disbursementResponse: DisbursementResponse? = disbursements.firstOrNull()?.let {
            DisbursementResponse(
                type = it.type,
                amount = it.amount,
                status = it.status
            )
        }

        //child NRIC will be masked in response
        val enrollmentGetResponse = EnrollmentGetResponse(
            id = enrollment.id,
            childNric = DataMasker.maskNric(enrollment.childNric),
            status = enrollment.status,
            enrolledAt = enrollment.enrolledAt,
            disbursement = disbursementResponse
        )

        return enrollmentGetResponse
    }

    private fun isEligibleForEnrollment(request: CreateEnrollmentRequest): Boolean {

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

    private fun createCashGift(enrollment : Enrollment) {
        val disbursement = Disbursement(
            enrollmentId = enrollment.id,
            type = DisbursementType.CASH_GIFT,
            amount = BabyBonusConstants.CASH_GIFT_AMOUNT_AT_BIRTH,
            status = DisbursementStatus.PROCESSED,
            processedAt = Instant.now()
        )

        disbursementRepository.save(disbursement)
    }

}