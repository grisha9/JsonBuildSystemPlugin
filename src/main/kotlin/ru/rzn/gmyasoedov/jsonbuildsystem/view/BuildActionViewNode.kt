package ru.rzn.gmyasoedov.jsonbuildsystem.view

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.openapi.externalSystem.util.Order
import com.intellij.openapi.externalSystem.view.ExternalProjectsView
import com.intellij.openapi.externalSystem.view.ExternalSystemNode

@Order(ExternalSystemNode.BUILTIN_TASKS_DATA_NODE_ORDER)
class BuildActionViewNode(externalProjectsView: ExternalProjectsView) :
    ExternalSystemNode<Void>(externalProjectsView, null, null) {

    override fun update(presentation: PresentationData) {
        super.update(presentation)
        presentation.setIcon(AllIcons.Nodes.ConfigFolder)
    }

    override fun getName() = "Build"

    override fun getActionId() = "CompileProject"
}