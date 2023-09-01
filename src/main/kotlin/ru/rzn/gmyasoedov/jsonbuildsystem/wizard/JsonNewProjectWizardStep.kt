package ru.rzn.gmyasoedov.jsonbuildsystem.wizard

import com.intellij.icons.AllIcons
import com.intellij.ide.JavaUiBundle
import com.intellij.ide.wizard.NewProjectWizardBaseData
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.externalSystem.model.ProjectKeys
import com.intellij.openapi.externalSystem.model.project.ModuleData
import com.intellij.openapi.externalSystem.service.project.ProjectDataManager
import com.intellij.openapi.externalSystem.service.project.wizard.MavenizedNewProjectWizardData
import com.intellij.openapi.externalSystem.service.project.wizard.MavenizedNewProjectWizardStep
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.externalSystem.util.ExternalSystemBundle
import com.intellij.openapi.externalSystem.util.ui.DataView
import com.intellij.openapi.module.StdModuleTypes
import com.intellij.openapi.projectRoots.JavaSdkType
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.projectRoots.impl.DependentSdkType
import com.intellij.openapi.roots.ui.configuration.sdkComboBox
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.dsl.builder.BottomGap
import com.intellij.ui.dsl.builder.COLUMNS_MEDIUM
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.columns
import com.intellij.ui.layout.ValidationInfoBuilder
import icons.OpenapiIcons
import ru.rzn.gmyasoedov.jsonbuildsystem.buildmodel.JsonBuildModel
import ru.rzn.gmyasoedov.jsonbuildsystem.settings.SystemSettings
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.JsonBuildSystemUtils
import java.io.File
import javax.swing.Icon

abstract class JsonNewProjectWizardStep<ParentStep>(parent: ParentStep) :
    MavenizedNewProjectWizardStep<JsonBuildModel, ParentStep>(parent)
        where ParentStep : NewProjectWizardStep,
              ParentStep : NewProjectWizardBaseData {

    val sdkProperty = propertyGraph.property<Sdk?>(null)

    var sdk by sdkProperty

    protected fun setupJavaSdkUI(builder: Panel) {
        builder.row(JavaUiBundle.message("label.project.wizard.new.project.jdk")) {
            val sdkTypeFilter = { it: SdkTypeId -> it is JavaSdkType && it !is DependentSdkType }
            sdkComboBox(context, sdkProperty, StdModuleTypes.JAVA.id, sdkTypeFilter)
                .columns(COLUMNS_MEDIUM)
        }.bottomGap(BottomGap.SMALL)
    }

    override fun createView(data: JsonBuildModel) = MavenDataView(data)

    override fun findAllParents(): List<JsonBuildModel> {
        val project = context.project ?: return emptyList()
        val projectDirectory = context.projectFileDirectory
        val configPath = project.getService(SystemSettings::class.java)
            .getLinkedProjectSettings(projectDirectory)?.configPath ?: return emptyList()
        return JsonBuildSystemUtils.getAllModules(configPath)
    }


    override fun ValidationInfoBuilder.validateGroupId(): ValidationInfo? {
        return validateCoordinates()
    }

    override fun ValidationInfoBuilder.validateArtifactId(): ValidationInfo? {
        return validateCoordinates()
    }

    private fun ValidationInfoBuilder.validateCoordinates(): ValidationInfo? {
        val mavenIds = parentsData.map { it.groupId to it.artifactId }.toSet()
        if (groupId to artifactId in mavenIds) {
            val message = ExternalSystemBundle.message(
                "external.system.mavenized.structure.wizard.entity.coordinates.already.exists.error",
                if (context.isCreatingNewProject) 1 else 0, "$groupId:$artifactId"
            )
            return error(message)
        }
        return null
    }

    class MavenDataView(override val data: JsonBuildModel) : DataView<JsonBuildModel>() {
        override val location: String = ""
        override val icon: Icon = AllIcons.FileTypes.Json
        override val presentationName: String = data.artifactId
        override val groupId: String = data.groupId
        override val version: String = data.version

        override fun toString(): String {
            return "MavenDataView(data=$data, location='$location', presentationName='$presentationName', groupId='$groupId', version='$version')"
        }
    }
}