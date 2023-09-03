package ru.rzn.gmyasoedov.jsonbuildsystem.project.model

import com.intellij.openapi.externalSystem.model.Key
import com.intellij.openapi.externalSystem.model.ProjectKeys
import com.intellij.openapi.externalSystem.model.project.AbstractExternalEntityData
import com.intellij.serialization.PropertyMapping
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.Constants

class CompilerArgData @PropertyMapping("arguments") constructor(
    arguments: Collection<String>,
) : AbstractExternalEntityData(Constants.SYSTEM_ID) {
    val arguments: Collection<String>

    init {
        this.arguments = arguments
    }

    companion object {
        val KEY = Key.create(
            CompilerArgData::class.java,
            ProjectKeys.MODULE.processingWeight + 1
        )
    }
}