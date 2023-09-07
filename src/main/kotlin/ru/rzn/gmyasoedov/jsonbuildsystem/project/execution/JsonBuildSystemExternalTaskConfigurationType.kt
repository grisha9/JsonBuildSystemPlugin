package ru.rzn.gmyasoedov.jsonbuildsystem.project.execution

import com.intellij.openapi.externalSystem.service.execution.AbstractExternalSystemTaskConfigurationType
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.Constants.SYSTEM_ID

class JsonBuildSystemExternalTaskConfigurationType : AbstractExternalSystemTaskConfigurationType(SYSTEM_ID) {

    override fun getConfigurationFactoryId() = SYSTEM_ID.readableName

    override fun isDumbAware() = true

    override fun isEditableInDumbMode() = true
}