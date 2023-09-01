package ru.rzn.gmyasoedov.jsonbuildsystem.wizard

import com.intellij.ide.projectWizard.generators.AssetsJavaNewProjectWizardStep
import com.intellij.ide.projectWizard.generators.BuildSystemJavaNewProjectWizard
import com.intellij.ide.projectWizard.generators.BuildSystemJavaNewProjectWizardData
import com.intellij.ide.projectWizard.generators.JavaNewProjectWizard
import com.intellij.ide.starters.local.StandardAssetsProvider
import com.intellij.ide.wizard.NewProjectWizardChainStep.Companion.nextStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.externalSystem.importing.ImportSpecBuilder
import com.intellij.openapi.externalSystem.model.ExternalSystemDataKeys
import com.intellij.openapi.externalSystem.model.ExternalSystemException
import com.intellij.openapi.externalSystem.service.project.manage.ExternalProjectsManagerImpl
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.observable.util.bindBooleanStorage
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.projectImport.ProjectOpenProcessor
import com.intellij.ui.UIBundle
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindSelected
import ru.rzn.gmyasoedov.jsonbuildsystem.settings.SystemSettings
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.Constants
import ru.rzn.gmyasoedov.jsonbuildsystem.utils.Constants.SYSTEM_ID
import java.nio.file.Path

class JsonBuildSystemNewProjectWizard : BuildSystemJavaNewProjectWizard {

    override val name = SYSTEM_ID.readableName

    override fun createStep(parent: JavaNewProjectWizard.Step): NewProjectWizardStep =
        Step(parent).nextStep(::AssetsStep)

    class Step(parent: JavaNewProjectWizard.Step) :
        JsonNewProjectWizardStep<JavaNewProjectWizard.Step>(parent),
        BuildSystemJavaNewProjectWizardData by parent {

        private val addSampleCodeProperty = propertyGraph.property(true)
            .bindBooleanStorage(NewProjectWizardStep.ADD_SAMPLE_CODE_PROPERTY_NAME)
        private val generateOnboardingTipsProperty = propertyGraph
            .property(AssetsJavaNewProjectWizardStep.proposeToGenerateOnboardingTipsByDefault())
            .bindBooleanStorage(NewProjectWizardStep.GENERATE_ONBOARDING_TIPS_NAME)

        var addSampleCode by addSampleCodeProperty
        var generateOnboardingTips by generateOnboardingTipsProperty

        private fun setupSampleCodeUI(builder: Panel) {
            builder.row {
                checkBox(UIBundle.message("label.project.wizard.new.project.add.sample.code"))
                    .bindSelected(addSampleCodeProperty)
            }
        }

        private fun setupSampleCodeWithOnBoardingTipsUI(builder: Panel) {
            builder.indent {
                row {
                    checkBox(UIBundle.message("label.project.wizard.new.project.generate.onboarding.tips"))
                        .bindSelected(generateOnboardingTipsProperty)
                }
            }.enabledIf(addSampleCodeProperty)
        }

        override fun setupSettingsUI(builder: Panel) {
            setupJavaSdkUI(builder)
            setupParentsUI(builder)
            setupSampleCodeUI(builder)
            setupSampleCodeWithOnBoardingTipsUI(builder)
        }

        override fun setupAdvancedSettingsUI(builder: Panel) {
            setupGroupIdUI(builder)
            setupArtifactIdUI(builder)
        }

        override fun setupProject(project: Project) {
            project.putUserData(ExternalSystemDataKeys.NEWLY_CREATED_PROJECT, true)
            project.putUserData(ExternalSystemDataKeys.NEWLY_IMPORTED_PROJECT, true)
            ExternalProjectsManagerImpl.setupCreatedProject(project)
            val modulePath = Path.of(parentStep.path, parentStep.name)
            ApplicationManager.getApplication().invokeLater { setupProjectFiles(modulePath, project) }
        }

        private fun setupProjectFiles(modulePath: Path, project: Project) {
            ApplicationManager.getApplication().invokeLater {
                if (context.isCreatingNewProject) {
                    createNewProject(modulePath, project)
                } else {
                    createSubModule(project, modulePath)
                }
            }
        }

        private fun createNewProject(modulePath: Path, project: Project) {
            runWriteAction {
                ModuleBuilderHelper.setupBuildScript(modulePath, groupId, artifactId, version)
            }
            val openProcessor = ProjectOpenProcessor.EXTENSION_POINT_NAME
                .findExtensionOrFail(JsonBuildSystemProjectOpenProcessor::class.java)
            val file = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(modulePath)
                ?: throw ExternalSystemException("file not found $modulePath")
            openProcessor.importProjectAfterwards(project, file)
        }

        private fun createSubModule(project: Project, modulePath: Path) {
            val projectSettings = (project.getService(SystemSettings::class.java)
                .getLinkedProjectSettings(modulePath.toString())
                ?: throw ExternalSystemException("settings not found $modulePath"))
            runWriteAction {
                ModuleBuilderHelper.setupModule(
                    projectSettings.configPath!!, groupId, artifactId, version, parentData!!
                )
            }
            ExternalProjectsManagerImpl.getInstance(project).runWhenInitialized {
                ExternalSystemUtil.refreshProject(
                    projectSettings.externalProjectPath, ImportSpecBuilder(project, SYSTEM_ID)
                )
            }
        }
    }

    private class AssetsStep(private val parent: Step) : AssetsJavaNewProjectWizardStep(parent) {

        override fun setupAssets(project: Project) {
            addAssets(StandardAssetsProvider().getMavenIgnoreAssets())
            if (parent.addSampleCode) {
                withJavaSampleCodeAsset("src/main/java", parent.groupId, parent.generateOnboardingTips)
            }
        }

        override fun setupProject(project: Project) {
            super.setupProject(project)
            if (parent.generateOnboardingTips) {
                prepareTipsInEditor(project)
            }
        }
    }
}