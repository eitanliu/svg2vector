apply {
    plugin("application")
}

fun findMain(): String? {
    val extension = project.extensions.getByType<JavaPluginExtension>()
    val allSource = extension.sourceSets.getByName("main").allSource
    return allSource.find {
        it.readText().contains("fun main(")
    }?.let { file ->
        println("")
        println("Main-Class find $file")
        val parent = allSource.srcDirs.find {
            file.absolutePath.startsWith(it.absolutePath)
        } ?: return@let null
        val srcFile = file.relativeTo(parent)
        val packageName = srcFile.parent.orEmpty().replace(File.separator, ".")
        val className = srcFile.nameWithoutExtension
        println("Main-Class find $parent, $packageName${className}Kt")
        "$packageName${className}Kt"
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
