package ru.rzn.gmyasoedov.jsonbuildsystem.utils

import com.intellij.openapi.externalSystem.model.ProjectSystemId
import java.util.*

object Constants {
    const val JSON_BUILD_SYSTEM = "JsonBuildSystem"
    const val DEFAULT_BUILD_FILE_NAME = "build.json"
    val SYSTEM_ID = ProjectSystemId(JSON_BUILD_SYSTEM.uppercase(Locale.getDefault()), JSON_BUILD_SYSTEM)
}