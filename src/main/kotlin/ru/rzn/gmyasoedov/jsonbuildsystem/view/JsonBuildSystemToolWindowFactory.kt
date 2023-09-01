package ru.rzn.gmyasoedov.jsonbuildsystem.view

import com.intellij.openapi.externalSystem.service.task.ui.AbstractExternalSystemToolWindowFactory
import com.intellij.openapi.externalSystem.settings.AbstractExternalSystemSettings
import com.intellij.openapi.project.Project
import ru.rzn.gmyasoedov.jsonbuildsystem.settings.SystemSettings
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.Constants.SYSTEM_ID

internal class JsonBuildSystemToolWindowFactory : AbstractExternalSystemToolWindowFactory(SYSTEM_ID) {

    override fun getSettings(project: Project): AbstractExternalSystemSettings<*, *, *> =
        project.getService(SystemSettings::class.java)

}
