package ru.rzn.gmyasoedov.jsonbuildsystem.project.model

import com.intellij.openapi.externalSystem.model.Key
import com.intellij.openapi.externalSystem.model.ProjectKeys
import com.intellij.serialization.PropertyMapping

class BuildActionData {

    companion object {
        val KEY = Key.create(
            BuildActionData::class.java,
            ProjectKeys.PROJECT.processingWeight + 1
        )
    }
}