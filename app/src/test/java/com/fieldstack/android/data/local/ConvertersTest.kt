package com.fieldstack.android.data.local

import com.fieldstack.android.domain.model.CustomField
import com.fieldstack.android.domain.model.CustomFieldType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `empty list round-trips correctly`() {
        val json = converters.fromCustomFields(emptyList())
        val result = converters.toCustomFields(json)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `single field round-trips correctly`() {
        val field = CustomField(
            id = "f1", label = "Site Code", type = CustomFieldType.Text,
            value = "SC-001", required = true,
        )
        val json = converters.fromCustomFields(listOf(field))
        val result = converters.toCustomFields(json)
        assertEquals(1, result.size)
        assertEquals(field, result[0])
    }

    @Test
    fun `multiple fields of different types round-trip`() {
        val fields = listOf(
            CustomField("f1", "Name", CustomFieldType.Text, "Alice"),
            CustomField("f2", "Count", CustomFieldType.Number, "42"),
            CustomField("f3", "Active", CustomFieldType.Checkbox, "true"),
            CustomField("f4", "Date", CustomFieldType.Date, "2026-05-01"),
        )
        val result = converters.toCustomFields(converters.fromCustomFields(fields))
        assertEquals(fields, result)
    }

    @Test
    fun `blank json returns empty list`() {
        assertTrue(converters.toCustomFields("").isEmpty())
        assertTrue(converters.toCustomFields("[]").isEmpty())
    }

    @Test
    fun `unknown type defaults to Text`() {
        val json = """[{"id":"f1","label":"X","type":"UNKNOWN","value":"","required":false}]"""
        val result = converters.toCustomFields(json)
        assertEquals(CustomFieldType.Text, result[0].type)
    }
}
