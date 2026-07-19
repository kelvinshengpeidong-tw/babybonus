package com.tw.babybonus.iroas.client

import com.tw.babybonus.iroas.domain.Parent

interface IroasClient {

    fun findParentByNric(nric: String): Parent?

}