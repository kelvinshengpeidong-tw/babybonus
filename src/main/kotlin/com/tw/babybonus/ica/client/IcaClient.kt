package com.tw.babybonus.ica.client

import com.tw.babybonus.ica.domain.Child

interface IcaClient {

    fun findChildByNric(nric: String): Child?

}