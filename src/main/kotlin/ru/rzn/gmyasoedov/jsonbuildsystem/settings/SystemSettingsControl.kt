package ru.rzn.gmyasoedov.jsonbuildsystem.settings

import com.intellij.openapi.externalSystem.util.ExternalSystemSettingsControl
import com.intellij.openapi.externalSystem.util.ExternalSystemUiUtil
import com.intellij.openapi.externalSystem.util.PaintAwarePanel
import com.intellij.ui.components.JBCheckBox

class SystemSettingsControl(private var initialSettings: SystemSettings) :
    ExternalSystemSettingsControl<SystemSettings> {

    private var skipTestsCheckBox: JBCheckBox? = null

    override fun fillUi(canvas: PaintAwarePanel, indentLevel: Int) {
        skipTestsCheckBox = JBCheckBox("Skip tests")
        canvas.add(skipTestsCheckBox, ExternalSystemUiUtil.getFillLineConstraints(0))
    }

    override fun reset() {
        skipTestsCheckBox?.setSelected(initialSettings.skipTests)
    }

    override fun isModified(): Boolean {
        if (skipTestsCheckBox?.isSelected != initialSettings.skipTests) {
            return true
        }
        return false
    }

    override fun disposeUIResources() {
        ExternalSystemUiUtil.disposeUi(this)
    }

    override fun showUi(show: Boolean) {
        ExternalSystemUiUtil.showUi(this, show)
    }

    override fun validate(settings: SystemSettings) = true

    override fun apply(settings: SystemSettings) {
        settings.skipTests = skipTestsCheckBox?.isSelected ?: false
    }
}