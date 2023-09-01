package ru.rzn.gmyasoedov.jsonbuildsystem.settings

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.externalSystem.settings.AbstractExternalSystemSettings
import com.intellij.openapi.externalSystem.settings.ExternalSystemSettingsListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.messages.Topic
import com.intellij.util.xmlb.annotations.XCollection
import java.nio.file.Path
import java.util.*

@State(name = "JsonBuildSystemSettings", storages = [Storage("jsonBuildSystem.xml")])
class SystemSettings(project: Project) :
    AbstractExternalSystemSettings<SystemSettings, ProjectSettings, SettingsListener>(
        SettingsListener.TOPIC, project
    ), PersistentStateComponent<SystemSettingsState> {

    var skipTests = false

    override fun copyExtraSettingsFrom(settings: SystemSettings) {}

    override fun checkSettings(old: ProjectSettings, current: ProjectSettings) {}

    override fun loadState(state: SystemSettingsState) {
        super.loadState(state)
        skipTests = state.skipTests
    }

    override fun getState(): SystemSettingsState {
        val state = SystemSettingsState()
        fillState(state)
        state.skipTests = skipTests
        return state
    }

    override fun getLinkedProjectSettings(projectPath: String): ProjectSettings? {
        val projectAbsolutePath = Path.of(projectPath).toAbsolutePath()
        val projectSettings: ProjectSettings? = super.getLinkedProjectSettings(projectPath)
        if (projectSettings == null) {
            for (setting in linkedProjectsSettings) {
                val settingPath = Path.of(setting.externalProjectPath).toAbsolutePath()
                if (FileUtil.isAncestor(settingPath.toFile(), projectAbsolutePath.toFile(), false)) {
                    return setting
                }
            }
        }
        return projectSettings
    }

    override fun subscribe(
        listener: ExternalSystemSettingsListener<ProjectSettings?>,
        parentDisposable: Disposable
    ) {
    }
}


class SystemSettingsState : AbstractExternalSystemSettings.State<ProjectSettings> {
    private val projectSettings: MutableSet<ProjectSettings> = TreeSet<ProjectSettings>()
    var skipTests = false

    @XCollection(elementTypes = [ProjectSettings::class])
    override fun getLinkedExternalProjectsSettings(): Set<ProjectSettings> {
        return projectSettings
    }

    override fun setLinkedExternalProjectsSettings(settings: Set<ProjectSettings>?) {
        if (settings != null) {
            projectSettings.addAll(settings)
        }
    }
}


interface SettingsListener : ExternalSystemSettingsListener<ProjectSettings?> {

    companion object {
        val TOPIC = Topic(SettingsListener::class.java, Topic.BroadcastDirection.NONE)
    }
}
