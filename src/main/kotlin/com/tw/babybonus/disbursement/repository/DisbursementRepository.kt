package com.tw.babybonus.disbursement.repository

import com.tw.babybonus.disbursement.domain.Disbursement
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DisbursementRepository : JpaRepository<Disbursement, UUID> {

}