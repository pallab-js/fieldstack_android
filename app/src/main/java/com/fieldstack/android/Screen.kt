package com.fieldstack.android

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object TaskList  : Screen("tasks")
    data object Reports   : Screen("reports")
    data object Sync      : Screen("sync")
    data object Settings  : Screen("settings")

    // Auth
    data object Login     : Screen("login")

    // Detail screens (not in bottom nav)
    data object TaskDetail : Screen("tasks/{taskId}") {
        fun route(taskId: String) = "tasks/$taskId"
    }
    data object ReportBuilder : Screen("report_builder/{taskId}") {
        fun route(taskId: String) = "report_builder/$taskId"
    }
    data object Insights : Screen("insights")
    data object Admin   : Screen("admin")
}
