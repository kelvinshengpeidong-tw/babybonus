package com.tw.babybonus.enrollment.repository

import com.tw.babybonus.enrollment.domain.Enrollment
import com.tw.babybonus.enrollment.domain.EnrollmentStatus
import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DataJpaTest
class EnrollmentRepositoryTest {

    @Autowired
    private lateinit var enrollmentRepository: EnrollmentRepository

    @Nested
    inner class EnrollmentExistence {

        val testChildNric: String = "T1234567A"
        val testParentNric: String = "S0099887B"

        @Test
        fun `should return false when child has no PENDING or ENROLLED enrollment`() {

            val exists = enrollmentRepository.existsByChildNricAndStatusIn(
                testChildNric,
                listOf(EnrollmentStatus.PENDING, EnrollmentStatus.ENROLLED)
            )

            assertFalse(exists)
        }

        @Test
        fun `should return false when child only has an INELIGIBLE enrollment`() {
            enrollmentRepository.save(
                Enrollment(
                    childNric = testChildNric,
                    parentNric = testParentNric,
                    status = EnrollmentStatus.INELIGIBLE,
                    enrolledAt = null,
                    createdAt = Instant.now()
                )
            )

            val exists = enrollmentRepository.existsByChildNricAndStatusIn(
                testChildNric,
                listOf(EnrollmentStatus.PENDING, EnrollmentStatus.ENROLLED)
            )

            assertFalse(exists)
        }

        @Test
        fun `should return true when child has a PENDING enrollment`() {

            enrollmentRepository.save(
                Enrollment(
                    childNric = testChildNric,
                    parentNric = testParentNric,
                    status = EnrollmentStatus.PENDING,
                    enrolledAt = null,
                    createdAt = Instant.now()
                )
            )

            val exists = enrollmentRepository.existsByChildNricAndStatusIn(
                testChildNric,
                listOf(EnrollmentStatus.PENDING, EnrollmentStatus.ENROLLED)
            )

            assertTrue(exists)
        }

        @Test
        fun `should return true when child has an ENROLLED enrollment`() {

            enrollmentRepository.save(
                Enrollment(
                    childNric = testChildNric,
                    parentNric = testParentNric,
                    status = EnrollmentStatus.ENROLLED,
                    enrolledAt = null,
                    createdAt = Instant.now()
                )
            )

            val exists = enrollmentRepository.existsByChildNricAndStatusIn(
                testChildNric,
                listOf(EnrollmentStatus.PENDING, EnrollmentStatus.ENROLLED)
            )

            assertTrue(exists)
        }

    }
}