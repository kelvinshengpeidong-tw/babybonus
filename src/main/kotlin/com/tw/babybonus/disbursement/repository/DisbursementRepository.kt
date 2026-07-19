package com.tw.babybonus.disbursement.repository

import com.tw.babybonus.disbursement.domain.Disbursement
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DisbursementRepository : JpaRepository<Disbursement, UUID> {

    //return a list to account for CDA_DEPOSIT in the future
    //let Spring JPA implement this method based on its name
    fun findAllByEnrollmentId(enrollmentId: UUID): List<Disbursement>

}