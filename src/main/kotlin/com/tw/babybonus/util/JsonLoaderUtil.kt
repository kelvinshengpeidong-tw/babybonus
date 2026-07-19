package com.tw.babybonus.util

import com.tw.babybonus.exception.DataLoadException
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

@Component
class JsonLoaderUtil(
    private val objectMapper: ObjectMapper
) {

    //generic JSON loader for any class type
    fun <T> loadList(resource: String, arrayClass: Class<Array<T>>): List<T> {

        try {
            val input = ClassPathResource(resource).inputStream
            return objectMapper.readValue(input, arrayClass).toList()
        }catch (e: Exception) {
            //throw exception if the required resource is missing or could not be loaded
            throw DataLoadException("Error: Failed to load json list '$resource'. Reason: $e", e)
        }
    }
}