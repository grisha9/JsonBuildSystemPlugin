package ru.rzn.gmyasoedov.jsonbuildsystem.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemJdkUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import ru.rzn.gmyasoedov.jsonbuildsystem.buildmodel.JsonBuildModel
import ru.rzn.gmyasoedov.jsonbuildsystem.settings.ProjectSettings
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.Constants.DEFAULT_BUILD_FILE_NAME
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Path


object JsonBuildSystemUtils {
    fun isBuildSystemFile(file: VirtualFile?): Boolean {
        if (file == null) return false
        return isBuildSystemFileName(file.name)
    }

    fun isBuildSystemFileName(fileName: String): Boolean {
        return fileName == DEFAULT_BUILD_FILE_NAME
    }

    fun createProjectSettings(projectFile: VirtualFile, project: Project): ProjectSettings {
        val projectDirectory = if (projectFile.isDirectory) projectFile else projectFile.parent
        val settings = ProjectSettings()
        settings.externalProjectPath = projectDirectory.canonicalPath
        settings.configPath = getBuildFileFullPath(projectFile)
        settings.jdkName = ExternalSystemJdkUtil.getJdk(project, ExternalSystemJdkUtil.USE_INTERNAL_JAVA)?.name
        return settings
    }

    fun getAllModulesWithPath(configPath: String): List<BuildModelWithPath> {
        val buildModel = fromJson(configPath)
        val result = mutableListOf<BuildModelWithPath>()
        collectAllModules(buildModel, Path.of(configPath).parent, result)
        return result
    }

    fun toJson(configPath: String, model: JsonBuildModel) {
        FileWriter(configPath).use { GsonBuilder().setPrettyPrinting().create().toJson(model, it) }
    }

    fun fromJson(configPath: String): JsonBuildModel {
        return FileReader(configPath).use { Gson().fromJson(it, JsonBuildModel::class.java) }
    }

    private fun collectAllModules(parent: JsonBuildModel, modulePath: Path, result: MutableList<BuildModelWithPath>) {
        result.add(BuildModelWithPath(parent, modulePath))
        for (module in parent.modules) {
            collectAllModules(module, modulePath.resolve(module.artifactId), result)
        }
    }

    fun getAllModules(parent: JsonBuildModel, configPath: Path): List<JsonBuildModel> {
        val result = mutableListOf<BuildModelWithPath>()
        collectAllModules(parent, configPath.parent, result)
        return result.map { it.buildModel }
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

class BuildModelWithPath(val buildModel: JsonBuildModel, val modelPath: Path)