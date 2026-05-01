package com.fieldstack.android

import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationTest {

    @Test
    fun `Screen routes are unique`() {
        val routes = listOf(
            Screen.Dashboard.route,
            Screen.TaskList.route,
            Screen.Sync.route,
            Screen.Settings.route,
            Screen.Login.route,
            Screen.TaskDetail.route,
            Screen.ReportBuilder.route,
        )
        assertEquals(routes.size, routes.toSet().size)
    }

    @Test
    fun `TaskDetail route interpolates taskId`() {
        assertEquals("tasks/abc-123", Screen.TaskDetail.route("abc-123"))
    }

    @Test
    fun `ReportBuilder route interpolates taskId`() {
        assertEquals("report_builder/task-42", Screen.ReportBuilder.route("task-42"))
    }
}
