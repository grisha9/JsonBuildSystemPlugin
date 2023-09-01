package ru.rzn.gmyasoedov.jsonbuildsystem.settings

import com.intellij.openapi.externalSystem.service.settings.AbstractExternalSystemConfigurable
import com.intellij.openapi.project.Project
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.Constants.SYSTEM_ID

class JsonBuildSystemSettingsConfigurable(project: Project) :
    AbstractExternalSystemConfigurable<ProjectSettings, SettingsListener, SystemSettings>(project, SYSTEM_ID) {

    override fun getId() = "reference.settingsdialog.project.jsonBuildSystem"

    override fun newProjectSettings() = ProjectSettings()

    override fun createSystemSettingsControl(settings: SystemSettings) = SystemSettingsControl(settings)

    override fun createProjectSettingsControl(settings: ProjectSettings) = ProjectSettingsControl(settings)
}