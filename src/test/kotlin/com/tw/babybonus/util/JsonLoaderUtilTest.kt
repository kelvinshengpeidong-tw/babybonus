package com.tw.babybonus.util

import com.tw.babybonus.exception.DataLoadException
import com.tw.babybonus.ica.domain.Child
import com.tw.babybonus.iroas.domain.Parent
import com.tw.babybonus.iroas.domain.ParentRelationship
import com.tw.babybonus.shared.Citizenship
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import tools.jackson.databind.ObjectMapper
import java.time.LocalDate
import kotlin.test.assertEquals

class JsonLoaderUtilTest {

    private val objectMapper = ObjectMapper()
    private val jsonLoaderUtil = JsonLoaderUtil(objectMapper)

    //valid children constants for testing
    val testValidChildrenResource: String = "/test-data/valid_ica_children.json"
    val testValidChildrenResourceSize: Int = 4
    val testValidChildrenResourceFirstChild: Child = Child(
        nric = "T1234567A",
        name = "Lim Wei Ting",
        dateOfBirth = LocalDate.parse("2026-02-09"),
        citizenship = Citizenship.SINGAPORE_CITIZEN
    )

    //valid parents constants for testing
    val testValidParentsResource: String = "/test-data/valid_iroas_parents.json"
    val testValidParentsResourceSize: Int = 5
    val testValidParentsResourceFirstParent: Parent = Parent(
        nric = "S1234567A",
        name = "Lim Ming",
        relationship = ParentRelationship.FATHER
    )

    val testMissingResource: String = "/test-data/util/missing_ica_children.json"
    val testInvalidJsonResource: String = "/test-data/invalid_ica_children.json"

    @Nested
    inner class LoadList {

        @Test
        fun `should load children list successfully`() {
            //load the data from the json file
            val children: List<Child> = jsonLoaderUtil.loadList(testValidChildrenResource, Array<Child>::class.java)

            //verify size of list
            assertEquals(testValidChildrenResourceSize, children.size)

            //verify first child data is read correctly
            val child = children.first()
            assertEquals(testValidChildrenResourceFirstChild.nric, child.nric)
            assertEquals(testValidChildrenResourceFirstChild.name, child.name)
            assertEquals(testValidChildrenResourceFirstChild.dateOfBirth, child.dateOfBirth)
            assertEquals(testValidChildrenResourceFirstChild.citizenship, child.citizenship)
        }

        @Test
        fun `should load parents list successfully`() {
            //load the data from the json file
            val parents: List<Parent> = jsonLoaderUtil.loadList(testValidParentsResource, Array<Parent>::class.java)

            //verify size of list
            assertEquals(testValidParentsResourceSize, parents.size)

            //verify first parent data is read correctly
            val parent = parents.first()
            assertEquals(testValidParentsResourceFirstParent.nric, parent.nric)
            assertEquals(testValidParentsResourceFirstParent.name, parent.name)
            assertEquals(testValidParentsResourceFirstParent.relationship, ParentRelationship.FATHER)
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
}