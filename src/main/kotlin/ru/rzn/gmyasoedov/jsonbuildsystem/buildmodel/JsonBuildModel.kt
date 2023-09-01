package ru.rzn.gmyasoedov.jsonbuildsystem.buildmodel

class JsonBuildModel(
    val groupId: String,
    val artifactId: String,
    val version: String,
    val sources: List<String>,
    val resources: List<String>,
    val sourcesTest: List<String>,
    val resourcesTest: List<String>,
    val compilerJdkVersion: String,
    val compilerArgs: List<String>,
    val dependencies: List<JsonDependencyModel>,
    val modules: MutableList<JsonBuildModel>
)

