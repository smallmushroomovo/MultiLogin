dependencies {
    implementation(project(":multilogin-api"))

    compileOnly(fileTree(mapOf("dir" to "libraries", "include" to listOf("*.jar"))))

    "com.velocitypowered:velocity-api:3.3.0-SNAPSHOT".apply {
        annotationProcessor(this)
        compileOnly(this)
    }
}