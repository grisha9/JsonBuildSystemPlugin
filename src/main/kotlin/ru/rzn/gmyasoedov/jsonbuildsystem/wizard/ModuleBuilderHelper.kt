package ru.rzn.gmyasoedov.jsonbuildsystem.wizard

import ru.rzn.gmyasoedov.jsonbuildsystem.buildmodel.JsonBuildModel
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.BuildModelWithPath
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.Constants.DEFAULT_BUILD_FILE_NAME
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.JsonBuildSystemUtils
import java.nio.file.Path

object ModuleBuilderHelper {
    fun setupBuildScript(moduleDir: Path, groupId: String, artifactId: String, version: String) {
        val jsonBuildModel = JsonBuildModel(
            groupId, artifactId, version,
            mutableListOf("src/main/java"),
            mutableListOf("src/main/resources"),
            mutableListOf("src/test/java"),
            mutableListOf("src/test/resources"),
            "17", mutableListOf(), mutableListOf(), mutableListOf()
        )
        JsonBuildSystemUtils.toJson(moduleDir.resolve(DEFAULT_BUILD_FILE_NAME).toString(), jsonBuildModel)
    }

    fun setupModule(
        configPath: String,
        groupId: String,
        artifactId: String,
        version: String,
        parentData: BuildModelWithPath
    ) {
        val parentBuildModel = parentData.buildModel
        val configFilePath = Path.of(configPath)
        val model = JsonBuildSystemUtils.fromJson(configPath)
        val modules = JsonBuildSystemUtils.getAllModules(model, configFilePath)
        val parent = modules
            .find { it.groupId == parentBuildModel.groupId && it.artifactId == parentBuildModel.artifactId} ?: return

        val jsonBuildModel = JsonBuildModel(
            groupId, artifactId, version,
            mutableListOf("src/main/java"),
            mutableListOf("src/main/resources"),
            mutableListOf("src/test/java"),
            mutableListOf("src/test/resources"),
            "17", mutableListOf(), mutableListOf(), mutableListOf()
        )
        parent.modules.add(jsonBuildModel)
        JsonBuildSystemUtils.toJson(configFilePath.toString(), model)
    }
}