package ru.rzn.gmyasoedov.jsonbuildsystem.buildmodel

class JsonBuildModel(
    val groupId: String,
    val artifactId: String,
    val version: String,
    val sources: MutableList<String>,
    val resources: MutableList<String>,
    val sourcesTest: MutableList<String>,
    val resourcesTest: MutableList<String>,
    val compilerJdkVersion: String,
    val compilerArgs: MutableList<String>,
    val dependencies: MutableList<JsonDependencyModel>,
    val modules: MutableList<JsonBuildModel>
)

