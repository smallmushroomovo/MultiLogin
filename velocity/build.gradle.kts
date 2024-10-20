import org.apache.tools.ant.filters.ReplaceTokens


dependencies {
    compileOnly(fileTree(mapOf("dir" to "libraries", "include" to listOf("*.jar"))))
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")


    implementation(project(":multilogin-api"))
    implementation(libs.exposedcore)
    implementation(libs.exposeddao)
    implementation(libs.exposedjdbc)
    implementation(libs.hikaricp)

    // test
    implementation("org.xerial:sqlite-jdbc:3.46.1.3")
}


tasks.processResources{
    val buildData: Map<String, String> by rootProject.extra

    filesMatching("velocity-plugin.json") {
        filter(
            ReplaceTokens::class, mapOf(
                "tokens" to buildData,
                "beginToken" to "@",
                "endToken" to "@"
            )
        )
    }
}
