fun findMain(): String? {
    val extension = project.extensions.getByType<JavaPluginExtension>()
    val allSource = extension.sourceSets.getByName("main").allSource
    return allSource.firstNotNullOfOrNull find@{ file ->
        val lines = file.readLines()
        return@find when {
            lines.any { line ->
                line.trim().startsWith("fun main(")
            } -> file to true

            lines.any { line ->
                line.trim().startsWith("public static void main(String")
            } -> file to false

            else -> null
        }
    }?.let { (file, isKotlin) ->
        println("")
        println("Main-Class find $file")
        val parent = allSource.srcDirs.find {
            file.absolutePath.startsWith(it.absolutePath)
        } ?: return@let null
        val srcFile = file.relativeTo(parent)
        val packageName = srcFile.parent?.run { "${replace(File.separator, ".")}." }.orEmpty()
        val className = srcFile.nameWithoutExtension + "Kt".takeIf { isKotlin }.orEmpty()
        println("Main-Class find ${srcFile.parent}, $packageName${className}")
        "$packageName${className}"
    }
}

afterEvaluate {

    tasks.register<Jar>("jarPackage") {
        group = "build"

        // archiveBaseName-archiveAppendix-archiveVersion-archiveClassifier
        // archiveClassifier = "dist"
        exclude("META-INF/**LICENSE*")
        exclude("META-INF/**NOTICE*")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        val runtimeClasses = configurations["runtimeClasspath"]
        val sourceSets = project.extensions.getByType<JavaPluginExtension>().sourceSets

        manifest {
            attributes["Main-Class"] = findMain()
            println("")
            println("jarPackage META-INF/MANIFEST.MF")
            attributes.forEach { t, u -> println("$t: $u") }
        }

        from(sourceSets["main"].output)
        from(runtimeClasses.map { if (it.isDirectory) it else zipTree(it) })
        destinationDirectory.set(layout.buildDirectory.file("distributions").get().asFile)
        dependsOn(runtimeClasses)
    }
}
