package ru.rzn.gmyasoedov.jsonbuildsystem.project.service

import com.intellij.compiler.CompilerConfiguration
import com.intellij.compiler.CompilerConfigurationImpl
import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.project.ModuleData
import com.intellij.openapi.externalSystem.model.project.ProjectData
import com.intellij.openapi.externalSystem.service.project.IdeModifiableModelsProvider
import com.intellij.openapi.externalSystem.service.project.manage.AbstractProjectDataService
import com.intellij.openapi.project.Project
import ru.rzn.gmyasoedov.jsonbuildsystem.project.model.CompilerArgData

class CompilerArgDataService : AbstractProjectDataService<CompilerArgData, Void>() {

    override fun getTargetDataKey() = CompilerArgData.KEY

    override fun postProcess(
        toImport: Collection<DataNode<CompilerArgData>>,
        projectData: ProjectData?,
        project: Project,
        modifiableModelsProvider: IdeModifiableModelsProvider
    ) {
        val config = CompilerConfiguration.getInstance(project) as CompilerConfigurationImpl
        for (node in toImport) {
            val moduleData = node.parent?.data as? ModuleData ?: continue
            val ideModule = modifiableModelsProvider.findIdeModule(moduleData) ?: continue
            config.setAdditionalOptions(ideModule, ArrayList(node.data.arguments))
        }
    }
}
