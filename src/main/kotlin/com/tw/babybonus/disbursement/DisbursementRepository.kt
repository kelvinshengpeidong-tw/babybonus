package com.tw.babybonus.disbursement

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DisbursementRepository : JpaRepository<Disbursement, UUID> {

}