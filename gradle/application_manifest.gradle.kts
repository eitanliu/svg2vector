apply {
    plugin("application")
}

fun findMain(): String? {
    val extension = project.extensions.getByType<JavaPluginExtension>()
    val allSource = extension.sourceSets.getByName("main").allSource
    return allSource.firstNotNullOfOrNull find@{ file ->
        val text = file.readText()
        return@find when {
            text.contains("fun main(") -> file to true
            text.contains("public static void main(String") -> file to false
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

        manifest {
            attributes["Main-Class"] = project.the<JavaApplication>().mainClass.get()
            val classpath = configurations["runtimeClasspath"]
                .filter { it.exists() }.joinToString(" ") { it.name }
            println("")
            println("jar META-INF/MANIFEST.MF")
            attributes["Class-Path"] = classpath
            attributes.forEach { t, u ->
                println("$t: $u")
            }
        }
    }
}
