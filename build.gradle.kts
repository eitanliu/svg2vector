// import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("java")
    // id("org.jetbrains.compose")
}

group = "com.eitanliu"
version = "1.0.1"

apply(from = rootProject.file("gradle/application_manifest.gradle.kts"))
apply(from = rootProject.file("gradle/jar_manifest.gradle.kts"))

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation("com.google.guava:guava:33.0.0-jre")
    implementation("commons-cli:commons-cli:1.4")
    // implementation("xerces:xercesImpl:2.12.1")
    // implementation("javax.xml.bind:jaxb-api:2.3.1")
    // implementation("org.glassfish.jaxb:jaxb-runtime:2.3.1")
    // implementation(compose.desktop.currentOs)
}

// compose.desktop {
//     application {
//         mainClass = "MainKt"
//
//         nativeDistributions {
//             targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
//             packageName = "svg2vector"
//             packageVersion = "1.0.0"
//         }
//     }
// }
