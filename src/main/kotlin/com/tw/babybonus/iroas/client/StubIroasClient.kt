package com.tw.babybonus.iroas.client

import com.tw.babybonus.ica.domain.Child
import com.tw.babybonus.iroas.domain.Parent
import com.tw.babybonus.util.JsonLoaderUtil

class StubIroasClient(
    private val jsonLoaderUtil: JsonLoaderUtil
): IroasClient {

    //resource relative to class path
    private val resource: String = "/mock-data/iroas_parent.json"

    private val parents: Map<String, Parent> = loadMockData()

    private fun loadMockData(): Map<String, Parent> {
        return jsonLoaderUtil.loadList(resource, Array<Parent>::class.java).associateBy { it.nric }
    }

    override fun findParentByNric(nric: String): Parent? {
        TODO("Not yet implemented")

        //parent nric should be masked when returning the new Parent object
    }
}