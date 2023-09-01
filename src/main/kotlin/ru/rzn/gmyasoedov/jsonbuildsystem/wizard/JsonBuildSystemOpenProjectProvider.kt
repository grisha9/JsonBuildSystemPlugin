package ru.rzn.gmyasoedov.jsonbuildsystem.wizard

import com.intellij.openapi.externalSystem.importing.AbstractOpenProjectProvider
import com.intellij.openapi.externalSystem.importing.ImportSpecBuilder
import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.ProjectSystemId
import com.intellij.openapi.externalSystem.model.project.ProjectData
import com.intellij.openapi.externalSystem.service.project.ExternalProjectRefreshCallback
import com.intellij.openapi.externalSystem.service.project.ProjectDataManager
import com.intellij.openapi.externalSystem.service.project.manage.ExternalProjectsManagerImpl
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.Constants.SYSTEM_ID
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.JsonBuildSystemUtils
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.JsonBuildSystemUtils.setProjectJdkIfNeeded

class JsonBuildSystemOpenProjectProvider : AbstractOpenProjectProvider() {
    override val systemId: ProjectSystemId = SYSTEM_ID

    override fun isProjectFile(file: VirtualFile) = JsonBuildSystemUtils.isBuildSystemFile(file)

    override fun linkToExistingProject(projectFile: VirtualFile, project: Project) {
        ExternalProjectsManagerImpl.getInstance(project).setStoreExternally(true)
        val projectSettings = JsonBuildSystemUtils.createProjectSettings(projectFile, project)
        val externalProjectPath = projectSettings.externalProjectPath
        ExternalSystemApiUtil.getSettings(project, SYSTEM_ID).linkProject(projectSettings)
        ExternalProjectsManagerImpl.getInstance(project).runWhenInitialized {
            ExternalSystemUtil.refreshProject(
                externalProjectPath,
                ImportSpecBuilder(project, SYSTEM_ID)
                    .callback(createFinalImportCallback(project, externalProjectPath))
            )
        }
    }

    private fun createFinalImportCallback(
        project: Project,
        externalProjectPath: String
    ): ExternalProjectRefreshCallback {
        return object : ExternalProjectRefreshCallback {
            override fun onSuccess(externalProject: DataNode<ProjectData>?) {
                if (externalProject == null) return
                ProjectDataManager.getInstance().importData(externalProject, project)
                setProjectJdkIfNeeded(project, externalProjectPath)
            }
        }
    }
}