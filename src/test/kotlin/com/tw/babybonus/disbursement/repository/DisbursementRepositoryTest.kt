package com.tw.babybonus.disbursement.repository

import com.tw.babybonus.disbursement.domain.Disbursement
import com.tw.babybonus.disbursement.domain.DisbursementStatus
import com.tw.babybonus.disbursement.domain.DisbursementType
import com.tw.babybonus.enrollment.service.BabyBonusConstants
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals

@DataJpaTest
class DisbursementRepositoryTest {

    @Autowired
    private lateinit var disbursementRepository: DisbursementRepository

    @Nested
    inner class FindAll {

        val enrollmentAId: UUID = UUID.randomUUID()
        val enrollmentBId: UUID = UUID.randomUUID()

        @Test
        fun `should return all disbursements for a given enrollment id`() {

            val firstDisbursementForEnrollmentA = Disbursement(
                enrollmentId = enrollmentAId,
                type = DisbursementType.CASH_GIFT,
                amount = BabyBonusConstants.CASH_GIFT_AMOUNT_AT_BIRTH,
                status = DisbursementStatus.PROCESSED,
                processedAt = Instant.now()
            )

            val secondDisbursementForEnrollmentA = Disbursement(
                enrollmentId = enrollmentAId,
                type = DisbursementType.CASH_GIFT,
                amount = BabyBonusConstants.CASH_GIFT_AMOUNT_AT_6MONTHS,
                status = DisbursementStatus.PENDING,
                processedAt = Instant.now()
            )

            val firstDisbursementForEnrollmentB = Disbursement(
                enrollmentId = enrollmentBId,
                type = DisbursementType.CASH_GIFT,
                amount = BabyBonusConstants.CASH_GIFT_AMOUNT_AT_BIRTH,
                status = DisbursementStatus.PROCESSED,
                processedAt = Instant.now()
            )

            val disbursements = listOf<Disbursement>(firstDisbursementForEnrollmentA, secondDisbursementForEnrollmentA, firstDisbursementForEnrollmentB)
            disbursementRepository.saveAll(disbursements)

            val resultsForEnrollmentA = disbursementRepository.findAllByEnrollmentId(enrollmentAId)
            val resultsForEnrollmentB = disbursementRepository.findAllByEnrollmentId(enrollmentBId)

            //verify results for Enrollment A
            assertEquals(2, resultsForEnrollmentA.size)
            //verify first disbursement
            assertEquals(enrollmentAId, resultsForEnrollmentA[0].enrollmentId)
            assertEquals(DisbursementStatus.PROCESSED, resultsForEnrollmentA[0].status)
            //verify second disbursement
            assertEquals(enrollmentAId, resultsForEnrollmentA[1].enrollmentId)
            assertEquals(DisbursementStatus.PENDING, resultsForEnrollmentA[1].status)

            //verify results for Enrollment B
            assertEquals(1, resultsForEnrollmentB.size)
            assertEquals(enrollmentBId, resultsForEnrollmentB[0].enrollmentId)
            assertEquals(DisbursementStatus.PROCESSED, resultsForEnrollmentB[0].status)
        }

        @Test
        fun `should return empty list if no disbursements exists for a given enrollment id`() {

            val resultsForEnrollment = disbursementRepository.findAllByEnrollmentId(enrollmentAId)
            assertEquals(0, resultsForEnrollment.size)
        }

    }

}