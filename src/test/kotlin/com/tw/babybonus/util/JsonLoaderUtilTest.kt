package com.tw.babybonus.util

import com.tw.babybonus.exception.DataLoadException
import com.tw.babybonus.ica.domain.Child
import com.tw.babybonus.shared.Citizenship
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import tools.jackson.databind.ObjectMapper
import java.time.LocalDate
import kotlin.test.assertEquals

class JsonLoaderUtilTest {

    private val jsonLoaderUtil = JsonLoaderUtil(objectMapper = ObjectMapper())

    //constants for testing
    val testValidResource: String = "/test-data/test_valid_ica_children.json"
    val testValidResourceSize: Number = 2
    val testValidResourceFirstChild: Child = Child(
        nric = "T1234567D",
        name = "Lim Ko Tong",
        dateOfBirth = LocalDate.parse("2026-01-01"),
        citizenship = Citizenship.SINGAPORE_CITIZEN
    )

    val testMissingResource: String = "/test-data/test_missing_ica_children.json"
    val testInvalidJsonResource: String = "/test-data/test_missing_ica_children.json"

    @Test
    fun `should load child list successfully`() {
        //load the data from the json file
        val children: List<Child> = jsonLoaderUtil.loadList(testValidResource, Array<Child>::class.java)

        //verify size of list
        assertEquals(testValidResourceSize, children.size)

        //verify first child data is read correctly
        val child = children.first()
        assertEquals(testValidResourceFirstChild.nric, child.nric)
        assertEquals(testValidResourceFirstChild.name, child.name)
        assertEquals(testValidResourceFirstChild.dateOfBirth, child.dateOfBirth)
        assertEquals(testValidResourceFirstChild.citizenship, child.citizenship)
    }

    @Test
    fun `should throw exception when resource is missing`() {
        assertThrows<DataLoadException>{
            jsonLoaderUtil.loadList(testMissingResource, Array<Child>::class.java)
        }
    }

    @Test
    fun `should throw exception when resource is invalid`() {
        assertThrows<DataLoadException>{
            jsonLoaderUtil.loadList(testInvalidJsonResource, Array<Child>::class.java)
        }
    }

}