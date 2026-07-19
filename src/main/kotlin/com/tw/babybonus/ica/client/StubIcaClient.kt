package com.tw.babybonus.ica.client

import com.tw.babybonus.ica.domain.Child
import com.tw.babybonus.util.JsonLoaderUtil

class StubIcaClient(
    private val jsonLoaderUtil: JsonLoaderUtil
): IcaClient {

    //resource relative to class path
    private val resource: String = "/mock-data/ica_children.json"

    private val children: Map<String, Child> = loadMockData()

    private fun loadMockData(): Map<String, Child> {
        return jsonLoaderUtil.loadList(resource, Array<Child>::class.java).associateBy { it.nric }
    }

    override fun findChildByNric(nric: String): Child? {
        TODO("Not yet implemented")

        //child nric should be masked when returning the new Child object
    }

}