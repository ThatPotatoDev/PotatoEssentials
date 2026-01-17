import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    alias(libs.plugins.shadowJar)
    alias(libs.plugins.lombok)
    alias(libs.plugins.runPaper)
}
repositories {
    mavenCentral()
}
fun Project.getSubProjectJars(): List<TaskProvider<ShadowJar>> {
    return listOf(
        project(":PotatoEssentials").tasks.shadowJar,
        project(":PotatoDiscordLink").tasks.shadowJar
    )
}

tasks {
    register<Copy>("serverDevelopment") {
        from(getSubProjectJars())
        into("\\\\192.168.1.46\\dev\\plugins") // Change this to wherever you want your jar to build
    }
    register<Copy>("serverProduction") {
        from(getSubProjectJars())
        into("\\\\192.168.1.46\\event\\plugins")
    }
    build {
        dependsOn("copyShadowJars")
    }
    jar.configure {
        enabled = false
    }
    runServer {
        minecraftVersion("1.21.11")
        pluginJars.setFrom(getSubProjectJars())
    }
    runPaper.detectPluginJar = false

    register<Copy>("copyShadowJars") {
        dependsOn(subprojects.map { it.tasks.shadowJar })

        val destinationDir = layout.buildDirectory.dir("libs").get().asFile

        from(subprojects.map {
            it.tasks.named<ShadowJar>("shadowJar").map { task ->
                task.archiveFile.get().asFile
            }
        })

        into(destinationDir)
    }

}

lombok.disableConfig = true

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "io.freefair.lombok")
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}
