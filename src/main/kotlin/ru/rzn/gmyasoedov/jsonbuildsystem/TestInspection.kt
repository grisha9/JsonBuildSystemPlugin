package ru.rzn.gmyasoedov.jsonbuildsystem

import com.intellij.codeInspection.AbstractBaseUastLocalInspectionTool
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import org.jetbrains.uast.UClass


class TestInspection : AbstractBaseUastLocalInspectionTool(UClass::class.java) {
    override fun checkClass(
        aClass: UClass,
        manager: InspectionManager,
        isOnTheFly: Boolean
    ): Array<ProblemDescriptor>? {
        println("inspection")
        return super.checkClass(aClass, manager, isOnTheFly)
    }
}