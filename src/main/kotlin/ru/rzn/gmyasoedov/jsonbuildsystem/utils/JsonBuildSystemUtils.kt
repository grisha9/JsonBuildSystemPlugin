package ru.rzn.gmyasoedov.jsonbuildsystem.utils

import com.google.gson.Gson
import com.intellij.openapi.externalSystem.model.ExternalSystemException
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemJdkUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import ru.rzn.gmyasoedov.jsonbuildsystem.buildmodel.JsonBuildModel
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.Constants.DEFAULT_BUILD_FILE_NAME
import ru.rzn.gmyasoedov.jsonbuildsystem.settings.ProjectSettings
import ru.rzn.gmyasoedov.jsonbuildsystem.settings.SystemSettings
import java.io.FileReader

object JsonBuildSystemUtils {
    fun isBuildSystemFile(file: VirtualFile?): Boolean {
        if (file == null) return false
        return isBuildSystemFileName(file.name)
    }

    fun isBuildSystemFileName(fileName: String): Boolean {
        return fileName == DEFAULT_BUILD_FILE_NAME
    }

    fun setProjectJdkIfNeeded(project: Project, externalProjectPath: String) {
        val settings = project.getService(SystemSettings::class.java)
        val projectSettings = settings.getLinkedProjectSettings(externalProjectPath) ?: return
        val jdkName = projectSettings.jdkName ?: return
        val projectRootManager = ProjectRootManager.getInstance(project)
        val projectSdk = projectRootManager.projectSdk ?: return
        if (projectSdk.name != jdkName) return
        projectSettings.jdkName = ExternalSystemJdkUtil.USE_PROJECT_JDK
    }

    fun createProjectSettings(projectFile: VirtualFile, project: Project): ProjectSettings {
        val projectDirectory = if (projectFile.isDirectory) projectFile else projectFile.parent
        val settings = ProjectSettings()
        settings.externalProjectPath = projectDirectory.canonicalPath
        settings.configPath = getBuildFileFullPath(projectFile)
        settings.jdkName = ExternalSystemJdkUtil.getJdk(project, ExternalSystemJdkUtil.USE_INTERNAL_JAVA)?.name
        return settings
    }

    fun getAllModules(configPath: String): List<JsonBuildModel> {
        val buildModel = Gson().fromJson(FileReader(configPath), JsonBuildModel::class.java)
        val result = mutableListOf<JsonBuildModel>()
        collectAllModules(buildModel, result)
        return result
    }

    private fun collectAllModules(parent: JsonBuildModel, result: MutableList<JsonBuildModel>) {
        result.add(parent)
        for (module in parent.modules) {
            collectAllModules(module, result)
        }
    }

    private fun getBuildFileFullPath(projectFile: VirtualFile) =
        if (projectFile.isDirectory) {
            projectFile.toNioPath().resolve(DEFAULT_BUILD_FILE_NAME).toString()
        } else {
            projectFile.toNioPath().toString()
        }
}