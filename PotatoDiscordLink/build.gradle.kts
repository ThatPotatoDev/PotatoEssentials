import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("java")
}

group = "com.thatpotatodev"
version = "1.0.1"

repositories {
    mavenCentral()
}

dependencies {

    // JDA
    implementation(libs.jda) {
        exclude(module="opus-java")
        exclude(module="tink")
    }
    // Gson
    implementation(libs.gson)
    // Paper
    compileOnly(libs.paper)

    compileOnly(project(":PotatoEssentials"))
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("")
        relocate("net.dv8tion.jda", "com.thatpotatodev.potatodiscordlink.jda")
    }
    compileJava {
        dependsOn(":PotatoEssentials:shadowJar")
    }
    processResources {
        filter<ReplaceTokens>("tokens" to mapOf(
            "version" to project.version.toString()))
    }
}