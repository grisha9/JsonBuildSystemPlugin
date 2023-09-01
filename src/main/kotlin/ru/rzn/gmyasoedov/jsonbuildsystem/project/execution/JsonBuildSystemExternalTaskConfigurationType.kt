package ru.rzn.gmyasoedov.jsonbuildsystem.project.execution

import com.intellij.openapi.externalSystem.service.execution.AbstractExternalSystemTaskConfigurationType
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.Constants.SYSTEM_ID

class JsonBuildSystemExternalTaskConfigurationType : AbstractExternalSystemTaskConfigurationType(SYSTEM_ID) {

    override fun getHelpTopic(): String {
        return "reference.dialogs.rundebug.JsonBuildSystemRunConfiguration"
    }

    override fun getConfigurationFactoryId() = SYSTEM_ID.readableName

    override fun isDumbAware() = true

    override fun isEditableInDumbMode() = true
}