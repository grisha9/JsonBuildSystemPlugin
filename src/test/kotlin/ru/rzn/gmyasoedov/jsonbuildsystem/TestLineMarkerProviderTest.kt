package ru.rzn.gmyasoedov.jsonbuildsystem

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase


class TestLineMarkerProviderTest : LightJavaCodeInsightFixtureTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(TestInspection::class.java)
    }

    fun testJavaLineProvider() {
        myFixture.configureByText(
            "MyAutoConfig.java",
            """
                public class MyAutoConfig {
                    private String testField = "test";
                }
            """.trimIndent()
        )

        val gutterMarks = myFixture.findGuttersAtCaret()
        println(gutterMarks)
    }

    fun testKtLineProvider() {
        myFixture.configureByText(
            "MyAutoConfig.kt",
            """
                class MyAutoConfig() {
                    private val testField = "test"
                }
            """.trimIndent()
        )
        val gutterMarks = myFixture.findGuttersAtCaret()
        println(gutterMarks)
    }
}