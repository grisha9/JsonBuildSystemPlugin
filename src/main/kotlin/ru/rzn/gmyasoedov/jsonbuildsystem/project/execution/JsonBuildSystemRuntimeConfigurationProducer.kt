package ru.rzn.gmyasoedov.jsonbuildsystem.project.execution

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.externalSystem.service.execution.AbstractExternalSystemRunConfigurationProducer
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.Constants.SYSTEM_ID

class JsonBuildSystemRuntimeConfigurationProducer : AbstractExternalSystemRunConfigurationProducer() {
    override fun getConfigurationFactory(): ConfigurationFactory {
        return ExternalSystemUtil.findConfigurationType(SYSTEM_ID)?.factory ?: throw IllegalStateException()
    }
}