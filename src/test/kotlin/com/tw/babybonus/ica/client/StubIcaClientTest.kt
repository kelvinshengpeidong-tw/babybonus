package com.tw.babybonus.ica.client

import com.tw.babybonus.ica.domain.Child
import com.tw.babybonus.shared.Citizenship
import com.tw.babybonus.util.JsonLoaderUtil
import org.junit.jupiter.api.Test
import tools.jackson.databind.ObjectMapper
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StubIcaClientTest {

    val objectMapper = ObjectMapper()
    val jsonLoaderUtil = JsonLoaderUtil(objectMapper)
    val stubIcaClient = StubIcaClient(jsonLoaderUtil)

    //constants for testing
    val childA: Child = Child(
        nric = "T2400001A",
        name = "Tan Wei Xuan",
        dateOfBirth = LocalDate.parse("2024-01-15"),
        citizenship = Citizenship.SINGAPORE_CITIZEN
    )

    val missingChildNric: String = "T7654321F"

    @Test
    fun `should return child with matching NRIC if found`() {
        val child = stubIcaClient.findChildByNric(childA.nric)
        assertEquals(childA, child)
    }

    @Test
    fun `should return null if no child with matching NRIC is found`() {
        val child = stubIcaClient.findChildByNric(missingChildNric)
        assertNull(child)
    }

}