package ru.rzn.gmyasoedov.jsonbuildsystem.view

import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.Key
import com.intellij.openapi.externalSystem.view.ExternalProjectsView
import com.intellij.openapi.externalSystem.view.ExternalSystemNode
import com.intellij.openapi.externalSystem.view.ExternalSystemViewContributor
import com.intellij.util.containers.MultiMap
import ru.rzn.gmyasoedov.jsonbuildsystem.project.model.BuildActionData
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.Constants.SYSTEM_ID

class JsonBuildSystemExternalViewContributor : ExternalSystemViewContributor() {
    override fun getSystemId() = SYSTEM_ID

    override fun getKeys(): List<Key<*>> = listOf(BuildActionData.KEY)

    override fun createNodes(
        externalProjectsView: ExternalProjectsView,
        dataNodes: MultiMap<Key<*>?, DataNode<*>?>
    ): List<ExternalSystemNode<*>> {
        val buildActionNodes = dataNodes[BuildActionData.KEY]
        return buildActionNodes.map { BuildActionViewNode(externalProjectsView, it as DataNode<BuildActionData>) }
    }
}