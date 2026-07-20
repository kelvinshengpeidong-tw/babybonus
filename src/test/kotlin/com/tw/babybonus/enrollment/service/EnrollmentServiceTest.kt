package com.tw.babybonus.enrollment.service

import com.tw.babybonus.disbursement.domain.Disbursement
import com.tw.babybonus.disbursement.domain.DisbursementStatus
import com.tw.babybonus.disbursement.domain.DisbursementType
import com.tw.babybonus.disbursement.repository.DisbursementRepository
import com.tw.babybonus.enrollment.domain.Enrollment
import com.tw.babybonus.enrollment.domain.EnrollmentStatus
import com.tw.babybonus.enrollment.dto.request.EnrollmentRequest
import com.tw.babybonus.enrollment.repository.EnrollmentRepository
import com.tw.babybonus.exception.ChildNotFoundException
import com.tw.babybonus.exception.DuplicateEnrollmentException
import com.tw.babybonus.exception.ParentNotFoundException
import com.tw.babybonus.ica.client.IcaClient
import com.tw.babybonus.ica.domain.Child
import com.tw.babybonus.iroas.client.IroasClient
import com.tw.babybonus.iroas.domain.Parent
import com.tw.babybonus.iroas.domain.ParentRelationship
import com.tw.babybonus.shared.Citizenship
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import org.mockito.kotlin.whenever
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import java.math.BigDecimal
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class EnrollmentServiceTest
{
    @Mock
    private lateinit var icaClient: IcaClient

    @Mock
    private lateinit var iroasClient: IroasClient

    @Mock
    private lateinit var enrollmentRepository: EnrollmentRepository

    @Mock
    private lateinit var disbursementRepository: DisbursementRepository

    @InjectMocks
    private lateinit var enrollmentService: EnrollmentService

    @Nested
    inner class ChildEnrollmentEligibility {

        @Test
        fun `should create enrolled enrollment when request is valid and child is eligible`() {
            val request = EnrollmentRequest(
                childNric = "T9988776A",
                parentNric = "S1234567A"
            )

            val child = Child(
                nric = "T9988776A",
                name = "Mack Lim",
                dateOfBirth = LocalDate.parse("2026-02-09"),
                citizenship = Citizenship.SINGAPORE_CITIZEN
            )
            
            val parent = Parent(
                nric = "S1234567A",
                name = "Lim Ming",
                relationship = ParentRelationship.FATHER
            )

            whenever(icaClient.findChildByNric(request.childNric))
                .thenReturn(child)

            whenever(iroasClient.findParentByNric(request.parentNric))
                .thenReturn(parent)

            //mock there is no existing enrollments yet for the child
            whenever(enrollmentRepository.existsByChildNricAndStatusIn(
                request.childNric, listOf(EnrollmentStatus.PENDING, EnrollmentStatus.ENROLLED))
            ).thenReturn(false)

            var savedEnrollment: Enrollment? = null //use this only for id comparison
            var savedDisbursement: Disbursement? = null //use this only for id comparison

            //mock the enrollment being returned back from save and also save it in variable
            whenever(enrollmentRepository.save(any<Enrollment>()))
                .thenAnswer {
                    savedEnrollment = it.arguments[0] as Enrollment
                    savedEnrollment
                }

            //mock the disbursement being returned back from save
            whenever(disbursementRepository.save(any<Disbursement>()))
                .thenAnswer {
                    savedDisbursement = it.arguments[0] as Disbursement
                    savedDisbursement
                }

            //mock the enrollment response
            val response = enrollmentService.enroll(request)

            //verify the saves were called during the enroll process
            verify(enrollmentRepository).save(any())
            verify(disbursementRepository).save(any())

            //verify enrollment data
            assertNotNull(savedEnrollment)
            assertEquals(savedEnrollment.id, response.id)
            assertEquals("T998****A", response.childNric)
            assertEquals(EnrollmentStatus.ENROLLED, response.status)
            assertNotNull(response.enrolledAt)
            //verify disbursement data
            assertNotNull(savedDisbursement)
            assertEquals(savedDisbursement.enrollmentId, response.id) //verify enrollment id in disbursement
            //verify content of the nested disbursement object
            assertNotNull(response.disbursement)
            assertEquals(response.disbursement.type, DisbursementType.CASH_GIFT)
            assertEquals(BigDecimal("3000.00"), response.disbursement.amount)
            assertEquals(response.disbursement.status, DisbursementStatus.PROCESSED)
        }

        //Repeat the test twice for citizenship=PERMANENT_RESIDENT and FOREIGNER
        @ParameterizedTest
        @EnumSource(Citizenship::class, mode = EnumSource.Mode.EXCLUDE, names = ["SINGAPORE_CITIZEN"])
        fun `should create ineligible enrollment when request is valid but child is not SINGAPORE_CITIZEN`(citizenship: Citizenship) {
            val request = EnrollmentRequest(
                childNric = "T7654321B",
                parentNric = "S1231239C"
            )

            val child = Child(
                nric = "T7654321B",
                name = "Tan Jia Wei",
                dateOfBirth = LocalDate.parse("2026-03-20"),
                citizenship = citizenship
            )

            val parent = Parent(
                nric = "S1231239C",
                name = "Michael En",
                relationship = ParentRelationship.FATHER
            )

            whenever(icaClient.findChildByNric(request.childNric))
                .thenReturn(child)

            whenever(iroasClient.findParentByNric(request.parentNric))
                .thenReturn(parent)

            //mock there is no existing enrollments yet
            whenever(enrollmentRepository.existsByChildNricAndStatusIn(
                request.childNric, listOf(EnrollmentStatus.PENDING, EnrollmentStatus.ENROLLED))
            ).thenReturn(false)

            var savedEnrollment: Enrollment? = null //use this only for id comparison

            //mock the enrollment being returned back from save and also save it in variable
            whenever(enrollmentRepository.save(any<Enrollment>()))
                .thenAnswer {
                    savedEnrollment = it.arguments[0] as Enrollment
                    savedEnrollment
                }

            //mock the enrollment response
            val response = enrollmentService.enroll(request)

            //verify the save was called during the enroll process
            verify(enrollmentRepository).save(any())

            //verify enrollment data
            assertNotNull(savedEnrollment)
            assertEquals(savedEnrollment.id, response.id)
            assertEquals("T765****B", response.childNric)
            assertEquals(EnrollmentStatus.INELIGIBLE, response.status)
            assertNull(response.enrolledAt)
            //verify disbursement is null
            assertNull(response.disbursement)
        }

        @Test
        fun `should throw exception if child does not exist in records`() {
            val request = EnrollmentRequest(
                childNric = "T1111111B",
                parentNric = "S1231239C"
            )

            //mock child not found
            whenever(icaClient.findChildByNric(request.childNric))
                .thenReturn(null)

            //expect exception to be thrown
            assertThrows<ChildNotFoundException> {
                enrollmentService.enroll(request)
            }
        }

        @Test
        fun `should throw exception if parent does not exist in records`() {
            val request = EnrollmentRequest(
                childNric = "T9988776A",
                parentNric = "S1111111C"
            )

            val child = Child(
                nric = "T9988776A",
                name = "Mack Lim",
                dateOfBirth = LocalDate.parse("2026-02-09"),
                citizenship = Citizenship.SINGAPORE_CITIZEN
            )

            whenever(icaClient.findChildByNric(request.childNric))
                .thenReturn(child)

            //mock parent not found
            whenever(iroasClient.findParentByNric(request.parentNric))
                .thenReturn(null)

            //expect exception to be thrown
            assertThrows<ParentNotFoundException> {
                enrollmentService.enroll(request)
            }
        }

        @Test
        fun `should throw exception if child is already enrolled`() {
            val request = EnrollmentRequest(
                childNric = "T9988776A",
                parentNric = "S1234567A"
            )

            val child = Child(
                nric = "T9988776A",
                name = "Mack Lim",
                dateOfBirth = LocalDate.parse("2026-02-09"),
                citizenship = Citizenship.SINGAPORE_CITIZEN
            )

            val parent = Parent(
                nric = "S1234567A",
                name = "Lim Ming",
                relationship = ParentRelationship.FATHER
            )

            whenever(icaClient.findChildByNric(request.childNric))
                .thenReturn(child)

            whenever(iroasClient.findParentByNric(request.parentNric))
                .thenReturn(parent)

            //mock enrollment for the child exists
            whenever(enrollmentRepository.existsByChildNricAndStatusIn(
                request.childNric, listOf(EnrollmentStatus.PENDING, EnrollmentStatus.ENROLLED))
            ).thenReturn(true)

            //expect exception to be thrown
            assertThrows<DuplicateEnrollmentException> {
                enrollmentService.enroll(request)
            }
        }

    }
}