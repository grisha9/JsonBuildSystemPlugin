package ru.rzn.gmyasoedov.jsonbuildsystem

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.psi.PsiElement
import org.jetbrains.uast.UField
import org.jetbrains.uast.toUElement

class TestLineMarkerProvider : RelatedItemLineMarkerProvider() {

    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        super.collectNavigationMarkers(element, result)
        val toUElement = element.toUElement()
        toUElement as? UField ?: return
        println("test")
    }

}