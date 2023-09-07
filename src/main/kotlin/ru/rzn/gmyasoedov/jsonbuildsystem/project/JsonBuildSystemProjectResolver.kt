package ru.rzn.gmyasoedov.jsonbuildsystem.project

import com.intellij.externalSystem.JavaModuleData
import com.intellij.externalSystem.JavaProjectData
import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.ExternalSystemException
import com.intellij.openapi.externalSystem.model.ProjectKeys
import com.intellij.openapi.externalSystem.model.project.*
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskId
import com.intellij.openapi.externalSystem.model.task.ExternalSystemTaskNotificationListener
import com.intellij.openapi.externalSystem.model.task.TaskData
import com.intellij.openapi.externalSystem.service.project.ExternalSystemProjectResolver
import com.intellij.openapi.module.ModuleTypeManager
import com.intellij.openapi.roots.DependencyScope
import com.intellij.pom.java.LanguageLevel
import ru.rzn.gmyasoedov.jsonbuildsystem.buildmodel.JsonBuildModel
import ru.rzn.gmyasoedov.jsonbuildsystem.buildmodel.JsonDependencyModel
import ru.rzn.gmyasoedov.jsonbuildsystem.project.model.BuildActionData
import ru.rzn.gmyasoedov.jsonbuildsystem.project.model.CompilerArgData
import ru.rzn.gmyasoedov.jsonbuildsystem.settings.ExecutionSettings
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.Constants.SYSTEM_ID
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.JsonBuildSystemUtils
import java.nio.file.Path


class JsonBuildSystemProjectResolver : ExternalSystemProjectResolver<ExecutionSettings> {

    override fun resolveProjectInfo(
        id: ExternalSystemTaskId,
        projectPath: String,
        isPreviewMode: Boolean,
        settings: ExecutionSettings?,
        listener: ExternalSystemTaskNotificationListener
    ): DataNode<ProjectData> {
        settings ?: throw ExternalSystemException("settings is empty")
        val configPath = settings.configPath ?: throw ExternalSystemException("config paths is empty")
        val buildModel = JsonBuildSystemUtils.fromJson(configPath)
        val languageLevel = getLanguageLevel(buildModel)
        val mainModulePath = Path.of(configPath).parent

        val projectDataNode = createProjectNode(buildModel, mainModulePath)

        projectDataNode.createChild(ProjectKeys.TASK, TaskData(SYSTEM_ID, "clean", mainModulePath.toString(), null))
        projectDataNode.createChild(BuildActionData.KEY, BuildActionData())

        setupJdkData(settings, projectDataNode, mainModulePath, languageLevel)
        setupModulesData(buildModel, mainModulePath.parent, projectDataNode)
        listener.onTaskOutput(id, "import finished", true)
        return projectDataNode
    }

    override fun cancelTask(taskId: ExternalSystemTaskId, listener: ExternalSystemTaskNotificationListener) = false

    private fun createProjectNode(
        buildModel: JsonBuildModel,
        mainModulePath: Path
    ): DataNode<ProjectData> {
        val projectData = ProjectData(
            SYSTEM_ID, buildModel.artifactId, mainModulePath.toString(), mainModulePath.toString()
        )
        projectData.version = buildModel.version
        projectData.group = buildModel.groupId

        return DataNode(ProjectKeys.PROJECT, projectData, null)
    }

    private fun setupJdkData(
        settings: ExecutionSettings,
        projectDataNode: DataNode<ProjectData>,
        mainModulePath: Path,
        languageLevel: LanguageLevel
    ) {
        val projectSdkData = ProjectSdkData(settings.jdkName)
        projectDataNode.createChild(ProjectSdkData.KEY, projectSdkData)

        val javaProjectData = JavaProjectData(
            SYSTEM_ID, mainModulePath.resolve("target").resolve("classes").toString(), languageLevel,
            languageLevel.toJavaVersion().toFeatureString()
        )
        projectDataNode.createChild(JavaProjectData.KEY, javaProjectData)
    }

    private fun setupModulesData(
        buildModule: JsonBuildModel,
        parentPath: Path,
        parentDataNode: DataNode<*>
    ) {

        val parentExternalName = getModuleName(parentDataNode.data, true)
        val parentInternalName = getModuleName(parentDataNode.data, false)
        val id = if (parentExternalName == null) buildModule.artifactId else ":" + buildModule.artifactId
        val modulePath = parentPath.resolve(buildModule.artifactId)

        val moduleData = ModuleData(
            id,
            SYSTEM_ID,
            getDefaultModuleTypeId(),
            buildModule.artifactId,
            modulePath.toString(),
            modulePath.toString()
        )
        moduleData.internalName = getInternalModuleName(parentInternalName, buildModule.artifactId)
        moduleData.group = buildModule.groupId
        moduleData.version = buildModule.version
        moduleData.moduleName = buildModule.artifactId

        moduleData.isInheritProjectCompileOutputPath = false
        moduleData.setCompileOutputPath(
            ExternalSystemSourceType.SOURCE,
            modulePath.resolve("target").resolve("classes").toString()
        )
        moduleData.setCompileOutputPath(
            ExternalSystemSourceType.TEST,
            modulePath.resolve("target").resolve("test-classes").toString()
        )
        moduleData.useExternalCompilerOutput(false)

        val moduleDataNode = parentDataNode.createChild(ProjectKeys.MODULE, moduleData)
        moduleDataNode.createChild(ModuleSdkData.KEY, ModuleSdkData(null))
        moduleDataNode.createChild(CompilerArgData.KEY, CompilerArgData(buildModule.compilerArgs))

        val languageLevel = getLanguageLevel(buildModule)
        moduleDataNode.createChild(
            JavaModuleData.KEY,
            JavaModuleData(SYSTEM_ID, languageLevel, languageLevel.toJavaVersion().toFeatureString())
        )
        setupContentRoots(buildModule, modulePath, moduleDataNode)
        setupDependencies(buildModule, modulePath, moduleDataNode)

        for (childModule in buildModule.modules) {
            setupModulesData(childModule, modulePath, moduleDataNode)
        }
    }

    private fun getLanguageLevel(buildModule: JsonBuildModel) =
        (LanguageLevel.parse(buildModule.compilerJdkVersion)
            ?: throw ExternalSystemException("compilerJdkVersion not found ${buildModule.compilerJdkVersion}"))

    private fun setupContentRoots(
        buildModule: JsonBuildModel, projectPath: Path, moduleDataNode: DataNode<ModuleData>
    ) {
        val rootData = ContentRootData(SYSTEM_ID, projectPath.toString())
        rootData.storePath(ExternalSystemSourceType.EXCLUDED, projectPath.resolve("target").toString())
        buildModule.sources.forEach {
            rootData.storePath(ExternalSystemSourceType.SOURCE, projectPath.resolve(it).toString())
        }
        buildModule.sourcesTest.forEach {
            rootData.storePath(ExternalSystemSourceType.TEST, projectPath.resolve(it).toString())
        }
        buildModule.resources.forEach {
            rootData.storePath(ExternalSystemSourceType.RESOURCE, projectPath.resolve(it).toString())
        }
        buildModule.resourcesTest.forEach {
            rootData.storePath(ExternalSystemSourceType.TEST_RESOURCE, projectPath.resolve(it).toString())
        }
        moduleDataNode.createChild(ProjectKeys.CONTENT_ROOT, rootData)
    }

    private fun setupDependencies(
        buildModule: JsonBuildModel,
        modulePath: Path,
        moduleDataNode: DataNode<ModuleData>
    ) {
        for (dependency in buildModule.dependencies) {
            addLibrary(dependency, modulePath, moduleDataNode)
        }
    }

    private fun getModuleName(data: Any, external: Boolean): String? {
        return if (data is ModuleData) {
            if (external) data.externalName else data.internalName
        } else null
    }

    private fun getDefaultModuleTypeId(): String {
        return ModuleTypeManager.getInstance().defaultModuleType.id
    }

    private fun getInternalModuleName(parentName: String?, moduleName: String): String {
        return if (parentName == null) moduleName else "$parentName.$moduleName"
    }

    private fun addLibrary(
        dependency: JsonDependencyModel, modulePath: Path, moduleNode: DataNode<ModuleData>
    ) {
        val library = LibraryData(SYSTEM_ID, dependency.artifactId)
        library.artifactId = dependency.artifactId
        library.setGroup(dependency.groupId)
        library.version = dependency.version
        library.addPath(LibraryPathType.BINARY, modulePath.resolve(dependency.relativePath).toString())

        val libraryDependencyData = LibraryDependencyData(moduleNode.data, library, LibraryLevel.PROJECT)
        libraryDependencyData.scope = DependencyScope.COMPILE
        moduleNode.createChild(ProjectKeys.LIBRARY_DEPENDENCY, libraryDependencyData)
    }
}