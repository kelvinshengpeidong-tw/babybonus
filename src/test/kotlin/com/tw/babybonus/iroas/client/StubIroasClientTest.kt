package com.tw.babybonus.iroas.client

import com.tw.babybonus.iroas.domain.Parent
import com.tw.babybonus.iroas.domain.ParentRelationship
import com.tw.babybonus.util.JsonLoaderUtil
import org.junit.jupiter.api.Test
import tools.jackson.databind.ObjectMapper
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StubIroasClientTest {

    val objectMapper = ObjectMapper()
    val jsonLoaderUtil = JsonLoaderUtil(objectMapper)
    val stubIroasClient = StubIroasClient(jsonLoaderUtil)

    //constants for testing
    val parentA: Parent = Parent(
        nric = "S8001234A",
        name = "Tan Ah Kow",
        relationship = ParentRelationship.FATHER
    )

    val missingParentNric: String = "S7654377F"

    @Test
    fun `should return parent with matching NRIC if found`() {
        val parent = stubIroasClient.findParentByNric(parentA.nric)
        assertEquals(parentA, parent)
    }

    @Test
    fun `should return null if no parent with matching NRIC is found`() {
        val parent = stubIroasClient.findParentByNric(missingParentNric)
        assertNull(parent)
    }
}