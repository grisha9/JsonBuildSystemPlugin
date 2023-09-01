package ru.rzn.gmyasoedov.jsonbuildsystem.project

import com.google.gson.Gson
import com.intellij.openapi.externalSystem.model.ExternalSystemException
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskId
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskNotificationListener
import com.intellij.openapi.externalSystem.task.ExternalSystemTaskManager
import com.intellij.openapi.util.io.FileUtil
import ru.rzn.gmyasoedov.jsonbuildsystem.buildmodel.JsonBuildModel
import ru.rzn.gmyasoedov.jsonbuildsystem.settings.ExecutionSettings
import java.io.FileReader
import java.nio.file.Path

class JsonBuildSystemTaskManager : ExternalSystemTaskManager<ExecutionSettings> {

    override fun executeTasks(
        id: ExternalSystemTaskId,
        taskNames: MutableList<String>,
        projectPath: String,
        settings: ExecutionSettings?,
        jvmParametersSetup: String?,
        listener: ExternalSystemTaskNotificationListener
    ) {
        settings ?: throw ExternalSystemException("settings is empty")
        if (taskNames != listOf("clean")) {
            // sorry only clean work
            return
        }
        val configPath = settings.configPath ?: throw ExternalSystemException("config paths is empty")
        val buildModel = Gson().fromJson(FileReader(configPath), JsonBuildModel::class.java)
        removeTargetDir(buildModel, Path.of(configPath).parent.parent)
    }

    private fun removeTargetDir(buildModel: JsonBuildModel, rootPath: Path) {
        val modulePath = rootPath.resolve(buildModel.artifactId)
        val targetPath = modulePath.resolve("target")
        FileUtil.deleteRecursively(targetPath)
        buildModel.modules.forEach { removeTargetDir(it, modulePath) }
    }

    override fun cancelTask(id: ExternalSystemTaskId, listener: ExternalSystemTaskNotificationListener) = false
}