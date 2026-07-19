package com.tw.babybonus.iroas.client

import com.tw.babybonus.iroas.domain.Parent
import com.tw.babybonus.util.JsonLoaderUtil
import org.springframework.stereotype.Component

@Component
class StubIroasClient(
    private val jsonLoaderUtil: JsonLoaderUtil
): IroasClient {

    //resource relative to class path
    private val resource: String = "/mock-data/iroas_parents.json"

    private val parents: Map<String, Parent> = loadMockData()

    private fun loadMockData(): Map<String, Parent> {
        return jsonLoaderUtil.loadList(resource, Array<Parent>::class.java).associateBy { it.nric }
    }

    override fun findParentByNric(nric: String): Parent? {
        //IROAS is still expected to return the full NRIC in the Parent object
        return parents[nric]
    }
}