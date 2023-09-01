package ru.rzn.gmyasoedov.jsonbuildsystem.wizard

import com.intellij.icons.AllIcons
import com.intellij.openapi.progress.ModalTaskOwner
import com.intellij.openapi.progress.runBlockingModal
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.projectImport.ProjectOpenProcessor
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.Constants

class JsonBuildSystemProjectOpenProcessor : ProjectOpenProcessor() {
    private val importProvider = JsonBuildSystemOpenProjectProvider()

    override fun canOpenProject(file: VirtualFile): Boolean = importProvider.canOpenProject(file)

    override fun doOpenProject(
        virtualFile: VirtualFile,
        projectToClose: Project?,
        forceOpenInNewFrame: Boolean
    ): Project? {
        return runBlockingModal(ModalTaskOwner.guess(), "") {
            importProvider.openProject(virtualFile, projectToClose, forceOpenInNewFrame)
        }
    }

    override val name = Constants.SYSTEM_ID.readableName

    override val icon = AllIcons.FileTypes.Json

    override fun canImportProjectAfterwards(): Boolean = true

    override fun importProjectAfterwards(project: Project, file: VirtualFile) {
        importProvider.linkToExistingProject(file, project)
    }
}