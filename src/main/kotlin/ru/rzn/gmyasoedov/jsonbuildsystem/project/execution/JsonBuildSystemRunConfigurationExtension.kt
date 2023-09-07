package ru.rzn.gmyasoedov.jsonbuildsystem.project.execution

import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemRunConfiguration
import com.intellij.openapi.externalSystem.service.execution.configuration.ExternalSystemReifiedRunConfigurationExtension
import com.intellij.openapi.externalSystem.service.execution.configuration.SettingsFragmentsContainer
import com.intellij.openapi.externalSystem.service.execution.configuration.addCommandLineFragment
import com.intellij.openapi.externalSystem.service.execution.configuration.addWorkingDirectoryFragment
import com.intellij.openapi.externalSystem.service.ui.command.line.CommandLineInfo
import com.intellij.openapi.externalSystem.service.ui.command.line.CompletionTableInfo
import com.intellij.openapi.externalSystem.service.ui.project.path.ExternalSystemWorkingDirectoryInfo
import com.intellij.openapi.project.Project
import com.intellij.util.execution.ParametersListUtil
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.Constants.SYSTEM_ID
import java.util.*

class JsonBuildSystemRunConfigurationExtension :
    ExternalSystemReifiedRunConfigurationExtension<ExternalSystemRunConfiguration>(
        ExternalSystemRunConfiguration::class.java
    ) {

    override fun SettingsFragmentsContainer<ExternalSystemRunConfiguration>.configureFragments(
        configuration: ExternalSystemRunConfiguration
    ) {
        val project = configuration.project
        addWorkingDirectoryFragment(project, ExternalSystemWorkingDirectoryInfo(project, SYSTEM_ID))
        addCommandLineFragment(project)
    }
    
    private fun getRawCommandLine(settings: ExternalSystemTaskExecutionSettings): String {
        val commandLine = StringJoiner(" ")
        for (taskName in settings.taskNames) {
            commandLine.add(taskName)
        }
        return commandLine.toString()
    }

    private fun parseCommandLine(settings: ExternalSystemTaskExecutionSettings, commandLine: String) {
        settings.taskNames = ParametersListUtil.parse(commandLine, true, true)
    }

    private fun SettingsFragmentsContainer<ExternalSystemRunConfiguration>.addCommandLineFragment(
        project: Project,
    ) = addCommandLineFragment(
        project,
        JsonCommandLineInfo(),
        { getRawCommandLine(settings) },
        { parseCommandLine(settings, it) }
    )
}

private class JsonCommandLineInfo : CommandLineInfo {
    override val dialogTitle: String get() = "command line options"
    override val dialogTooltip: String get() = ""
    override val fieldEmptyState: String get() = ""
    override val settingsHint: String get() = ""
    override val settingsName: String = ""
    override val tablesInfo: List<CompletionTableInfo> get() = emptyList()

}
