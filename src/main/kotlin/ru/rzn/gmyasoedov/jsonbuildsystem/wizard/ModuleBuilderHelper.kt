package ru.rzn.gmyasoedov.jsonbuildsystem.wizard

import com.google.gson.Gson
import com.intellij.openapi.vfs.LocalFileSystem
import ru.rzn.gmyasoedov.jsonbuildsystem.buildmodel.JsonBuildModel
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Path

object ModuleBuilderHelper {
    fun setupBuildScript(moduleDir: Path, groupId: String, artifactId: String, version: String) {
        val jsonBuildModel = JsonBuildModel(
            groupId, artifactId, version,
            listOf("src/main/java"),
            listOf("src/main/resources"),
            listOf("src/test/java"),
            listOf("src/test/resources"),
            "17", emptyList(), emptyList(), mutableListOf()
        )
        Gson().toJson(jsonBuildModel, FileWriter(moduleDir.toFile()))
        createContentRootsDirs(moduleDir)
    }

    private fun createContentRootsDirs(moduleDir: Path) {
        createDirectory(moduleDir.resolve("src").resolve("main").resolve("java"))
        createDirectory(moduleDir.resolve("src").resolve("main").resolve("resources"))
        createDirectory(moduleDir.resolve("src").resolve("test").resolve("java"))
        createDirectory(moduleDir.resolve("src").resolve("test").resolve("resources"))
    }

    private fun createDirectory(srcMainJavaPath: Path) {
        try {
            srcMainJavaPath.toFile().mkdirs()
            LocalFileSystem.getInstance().refreshAndFindFileByPath(srcMainJavaPath.toString())
        } catch (_: Exception) {
        }
    }

    fun setupModule(
        configPath: String,
        groupId: String,
        artifactId: String,
        version: String,
        parentData: JsonBuildModel
    ) {
        val configFilePath = Path.of(configPath)
        val model = Gson().fromJson(FileReader(configFilePath.toFile()), JsonBuildModel::class.java)
        val parent = findParent(model, parentData, configFilePath.parent) ?: throw IllegalStateException()

        val jsonBuildModel = JsonBuildModel(
            groupId, artifactId, version,
            listOf("src/main/java"),
            listOf("src/main/resources"),
            listOf("src/test/java"),
            listOf("src/test/resources"),
            "17", emptyList(), emptyList(), mutableListOf()
        )
        parent.second.modules.add(jsonBuildModel)
        Gson().toJson(model, FileWriter(configFilePath.toFile()))
        createContentRootsDirs(parent.first)
    }

    private fun findParent(
        model: JsonBuildModel,
        parentData: JsonBuildModel,
        parentPath: Path
    ): Pair<Path, JsonBuildModel>? {
        if (model.groupId == parentData.groupId && model.artifactId == parentData.artifactId) {
            return parentPath to model;
        }
        val subParent = parentPath.resolve(model.artifactId)
        for (module in model.modules) {
            return findParent(model, parentData, subParent)
        }
        return null
    }
}