package com.tw.babybonus.disbursement.dto

import com.tw.babybonus.disbursement.domain.DisbursementStatus
import com.tw.babybonus.disbursement.domain.DisbursementType
import java.math.BigDecimal

data class DisbursementResponse(

    val type: DisbursementType,

    val amount: BigDecimal,

    val status: DisbursementStatus

) {

}