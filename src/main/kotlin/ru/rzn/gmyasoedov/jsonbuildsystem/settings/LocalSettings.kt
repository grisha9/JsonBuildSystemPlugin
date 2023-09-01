package ru.rzn.gmyasoedov.jsonbuildsystem.settings

import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.externalSystem.settings.AbstractExternalSystemLocalSettings
import com.intellij.openapi.project.Project
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.Constants

@State(name = "MavenLocalSettings", storages = [Storage(StoragePathMacros.CACHE_FILE)])
class LocalSettings(project: Project) : AbstractExternalSystemLocalSettings<LocalSettings.JsonLocalState>(
    Constants.SYSTEM_ID,
    project,
    JsonLocalState()
) {
    class JsonLocalState : State()
}