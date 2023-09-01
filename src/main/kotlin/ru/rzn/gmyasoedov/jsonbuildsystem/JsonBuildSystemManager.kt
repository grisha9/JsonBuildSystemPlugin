package ru.rzn.gmyasoedov.jsonbuildsystem

import com.intellij.execution.configurations.SimpleJavaParameters
import com.intellij.icons.AllIcons
import com.intellij.openapi.externalSystem.ExternalSystemAutoImportAware
import com.intellij.openapi.externalSystem.ExternalSystemConfigurableAware
import com.intellij.openapi.externalSystem.ExternalSystemManager
import com.intellij.openapi.externalSystem.ExternalSystemUiAware
import com.intellij.openapi.externalSystem.service.project.autoimport.CachingExternalSystemAutoImportAware
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.Function
import com.intellij.util.execution.ParametersListUtil
import ru.rzn.gmyasoedov.jsonbuildsystem.project.JsonBuildSystemAutoImportAware
import ru.rzn.gmyasoedov.jsonbuildsystem.project.JsonBuildSystemProjectResolver
import ru.rzn.gmyasoedov.jsonbuildsystem.project.JsonBuildSystemTaskManager
import ru.rzn.gmyasoedov.jsonbuildsystem.settings.*
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.Constants.SYSTEM_ID
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.JsonBuildSystemUtils.isBuildSystemFile
import java.io.File

class JsonBuildSystemManager : ExternalSystemConfigurableAware, ExternalSystemUiAware, ExternalSystemAutoImportAware,
    ExternalSystemManager<ProjectSettings, SettingsListener, SystemSettings, LocalSettings, ExecutionSettings> {

    private val autoImportAwareDelegate: ExternalSystemAutoImportAware = CachingExternalSystemAutoImportAware(
        JsonBuildSystemAutoImportAware()
    )

    override fun getConfigurable(project: Project) = JsonBuildSystemSettingsConfigurable(project)
    /*------1----*/

    override fun getProjectRepresentationName(targetProjectPath: String, rootProjectPath: String?): String {
        return ExternalSystemApiUtil.getProjectRepresentationName(targetProjectPath, rootProjectPath)
    }

    override fun getExternalProjectConfigDescriptor(): FileChooserDescriptor {
        return FileChooserDescriptorFactory.createSingleFolderDescriptor()
    }

    override fun getProjectIcon() = AllIcons.FileTypes.Json

    override fun getTaskIcon() = AllIcons.Nodes.ConfigFolder

    /*------2----*/

    override fun enhanceRemoteProcessing(parameters: SimpleJavaParameters) =
        throw java.lang.UnsupportedOperationException()

    override fun getSystemId() = SYSTEM_ID

    override fun getSettingsProvider(): Function<Project, SystemSettings> {
        return Function<Project, SystemSettings> { project: Project -> project.getService(SystemSettings::class.java) }
    }

    override fun getLocalSettingsProvider(): Function<Project, LocalSettings> {
        return Function<Project, LocalSettings> { project: Project -> project.getService(LocalSettings::class.java) }
    }

    override fun getExecutionSettingsProvider(): Function<Pair<Project, String>, ExecutionSettings> {
        return Function<Pair<Project, String>, ExecutionSettings> {
            val project = it.first
            val projectPath = it.second
            val systemSettings = project.getService(SystemSettings::class.java)
            val projectSettings = systemSettings.getLinkedProjectSettings(projectPath)
            val executionSettings = ExecutionSettings()
            executionSettings.configPath = projectSettings?.configPath
            executionSettings.jdkName = projectSettings?.jdkName
            projectSettings?.vmOptions
                ?.also { executionSettings.withVmOptions(ParametersListUtil.parse(it, true, true)) }
            executionSettings
        }
    }

    override fun getProjectResolverClass() = JsonBuildSystemProjectResolver::class.java

    override fun getTaskManagerClass() = JsonBuildSystemTaskManager::class.java

    override fun getExternalProjectDescriptor() = BuildFileChooserDescriptor()

    /*------3----*/
    override fun getAffectedExternalProjectPath(changedFileOrDirPath: String, project: Project): String? {
        return autoImportAwareDelegate.getAffectedExternalProjectPath(changedFileOrDirPath, project)
    }

    override fun getAffectedExternalProjectFiles(projectPath: String?, project: Project): List<File> {
        return autoImportAwareDelegate.getAffectedExternalProjectFiles(projectPath, project)
    }
}

class BuildFileChooserDescriptor : FileChooserDescriptor(false, true, false, false, false, false) {
    override fun isFileSelectable(file: VirtualFile?): Boolean {
        file ?: return false
        if (!super.isFileSelectable(file)) return false
        return file.children.any { isBuildSystemFile(it) }
    }
}