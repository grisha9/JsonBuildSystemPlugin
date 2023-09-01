package ru.rzn.gmyasoedov.jsonbuildsystem.settings

import com.intellij.openapi.externalSystem.service.settings.AbstractExternalProjectSettingsControl
import com.intellij.openapi.externalSystem.util.ExternalSystemUiUtil
import com.intellij.openapi.externalSystem.util.PaintAwarePanel
import com.intellij.ui.components.JBLabel
import javax.swing.Box
import javax.swing.JTextField

class ProjectSettingsControl(initialSettings: ProjectSettings) :
    AbstractExternalProjectSettingsControl<ProjectSettings>(initialSettings) {

    private var vmOptionsField: JTextField? = null

    override fun validate(settings: ProjectSettings) = true

    override fun resetExtraSettings(isDefaultModuleCreation: Boolean) {
        if (vmOptionsField != null) {
            vmOptionsField!!.text = initialSettings.vmOptions
        }
    }

    override fun fillExtraControls(content: PaintAwarePanel, indentLevel: Int) {
        val vmOptionsLabel = JBLabel("Vm options")
        vmOptionsField = JTextField()
        content.add(vmOptionsLabel, ExternalSystemUiUtil.getLabelConstraints(indentLevel))
        content.add(vmOptionsField, ExternalSystemUiUtil.getLabelConstraints(0))
        content.add(Box.createGlue(), ExternalSystemUiUtil.getFillLineConstraints(indentLevel))
        vmOptionsLabel.setLabelFor(vmOptionsField)
    }

    override fun isExtraSettingModified(): Boolean {
        if (vmOptionsField?.text != (initialSettings.vmOptions ?: "")) {
            return true
        }
        return false
    }

    override fun applyExtraSettings(settings: ProjectSettings) {
        if (vmOptionsField != null) {
            settings.vmOptions = vmOptionsField!!.getText()
        }
    }
}