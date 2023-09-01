package ru.rzn.gmyasoedov.jsonbuildsystem.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction
import ru.rzn.gmyasoedov.jsonbuildsystem.settings.SystemSettings

class SkipTestsAction : ToggleAction() {

    override fun isSelected(e: AnActionEvent): Boolean {
        val project = e.project ?: return false
        return project.getService(SystemSettings::class.java).skipTests
    }

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        val project = e.project ?: return
        val settings = project.getService(SystemSettings::class.java)
        settings.skipTests = !settings.skipTests
    }

    init {
        templatePresentation.icon = AllIcons.RunConfigurations.ShowIgnored
        templatePresentation.text = "Skip Tests"
    }
}