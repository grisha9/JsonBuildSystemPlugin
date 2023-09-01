package ru.rzn.gmyasoedov.jsonbuildsystem.settings

import com.intellij.openapi.externalSystem.settings.ExternalProjectSettings

class ProjectSettings : ExternalProjectSettings() {
    var configPath: String? = null
    var vmOptions: String? = null
    var jdkName: String? = null

    override fun clone(): ProjectSettings {
        val result = ProjectSettings()
        copyTo(result)
        result.vmOptions = vmOptions
        result.configPath = configPath
        result.jdkName = jdkName
        return result
    }
}