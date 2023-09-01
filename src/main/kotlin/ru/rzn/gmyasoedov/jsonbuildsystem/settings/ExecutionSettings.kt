package ru.rzn.gmyasoedov.jsonbuildsystem.settings

import com.intellij.openapi.externalSystem.model.settings.ExternalSystemExecutionSettings

class ExecutionSettings : ExternalSystemExecutionSettings() {
    var configPath: String? = null
    var jdkName: String? = null
}