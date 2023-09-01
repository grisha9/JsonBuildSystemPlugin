package ru.rzn.gmyasoedov.jsonbuildsystem.project

import com.intellij.openapi.externalSystem.ExternalSystemAutoImportAware
import com.intellij.openapi.externalSystem.model.ProjectKeys
import com.intellij.openapi.externalSystem.service.project.ProjectDataManager
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.project.Project
import ru.rzn.gmyasoedov.jsonbuildsystem.settings.SystemSettings
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.JsonBuildSystemUtils
import java.io.File
import java.nio.file.Path
import kotlin.io.path.isDirectory

class JsonBuildSystemAutoImportAware : ExternalSystemAutoImportAware {

    override fun getAffectedExternalProjectPath(changedFileOrDirPath: String, project: Project): String? {
        val changedPath = Path.of(changedFileOrDirPath)
        if (changedPath.isDirectory()) return null
        val fileSimpleName = changedPath.fileName.toString()
        if (!JsonBuildSystemUtils.isBuildSystemFileName(fileSimpleName)) return null
        val systemSettings = project.getService(SystemSettings::class.java)
        return systemSettings.getLinkedProjectSettings(changedPath.parent.toString())?.externalProjectPath
    }

    override fun getAffectedExternalProjectFiles(projectPath: String?, project: Project): List<File> {
        projectPath ?: return listOf()
        val systemSettings = project.getService(SystemSettings::class.java)
        val projectSettings = systemSettings.getLinkedProjectSettings(projectPath) ?: return listOf()
        return projectSettings.configPath?.let { listOf(File(it)) } ?: listOf()
    }
}