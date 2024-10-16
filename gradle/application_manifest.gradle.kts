apply {
    plugin("application")
}

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

project.configure<JavaApplication> {
    mainClass = findMain() ?: "MainKt"
}

afterEvaluate {

    project.tasks.getByName<Jar>("jar") {

        exclude("META-INF/**LICENSE*")
        exclude("META-INF/**NOTICE*")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        val runtimeClasses = configurations["runtimeClasspath"]

        manifest {
            attributes["Main-Class"] = findMain()
            attributes["Class-Path"] = runtimeClasses.filter { it.exists() }
                .joinToString(" ") { it.name }
            println("")
            println("jar META-INF/MANIFEST.MF")
            attributes.forEach { t, u -> println("$t: $u") }
        }
    }
}
