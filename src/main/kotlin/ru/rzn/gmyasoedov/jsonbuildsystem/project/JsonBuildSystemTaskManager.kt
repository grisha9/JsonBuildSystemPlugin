package ru.rzn.gmyasoedov.jsonbuildsystem.project

import com.intellij.openapi.externalSystem.model.ExternalSystemException
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskId
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskNotificationListener
import com.intellij.openapi.externalSystem.task.ExternalSystemTaskManager
import com.intellij.openapi.util.io.FileUtil
import ru.rzn.gmyasoedov.jsonbuildsystem.settings.ExecutionSettings
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.JsonBuildSystemUtils

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
            throw ExternalSystemException("only clean implemented")
        }
        val configPath = settings.configPath ?: throw ExternalSystemException("config paths is empty")
        JsonBuildSystemUtils.getAllModulesWithPath(configPath)
            .map { it.modelPath.resolve("target") }
            .forEach { FileUtil.deleteRecursively(it) }
    }

    override fun cancelTask(id: ExternalSystemTaskId, listener: ExternalSystemTaskNotificationListener) = false
}