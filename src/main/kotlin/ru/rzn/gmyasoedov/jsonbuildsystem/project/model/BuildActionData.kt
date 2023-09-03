package ru.rzn.gmyasoedov.jsonbuildsystem.project.model

import com.intellij.openapi.externalSystem.model.Key
import com.intellij.openapi.externalSystem.model.ProjectKeys
import com.intellij.openapi.externalSystem.model.project.AbstractExternalEntityData
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.Constants.SYSTEM_ID

class BuildActionData : AbstractExternalEntityData(SYSTEM_ID) {
    companion object {
        val KEY = Key.create(
            BuildActionData::class.java,
            ProjectKeys.PROJECT.processingWeight + 1
        )
    }
}