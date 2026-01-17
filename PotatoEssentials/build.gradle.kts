import io.papermc.paperweight.userdev.ReobfArtifactConfiguration
import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    java
    alias(libs.plugins.paperweight)
    `maven-publish`
}

group = "dev.thatpotato"
version = "1.0.1"

repositories {
    mavenCentral()
    // Paper
    maven("https://repo.papermc.io/repository/maven-public/")
    // VaultAPI
    maven("https://jitpack.io")
    // ConfigUpdater
    maven("https://oss.sonatype.org/content/groups/public")
}

dependencies {
    // Paper
    compileOnly(libs.paper)
    // Paper NMS
    paperweight.paperDevBundle(libs.paper.get().version)
    // VaultAPI
    compileOnly(libs.vaultApi)
    // Config-Updater
    implementation(libs.configUpdater)
    // CommandAPI
    implementation(libs.commandApi)
    // Commons Collections
    implementation(libs.commonsCollections)
    // bStats
    implementation(libs.bStats)
}

paperweight {
    reobfArtifactConfiguration = ReobfArtifactConfiguration.MOJANG_PRODUCTION
}

publishing.publications.create<MavenPublication>("maven") {
    groupId = "dev.thatpotato"
    artifactId = "PotatoEssentials"
    version = project.version.toString()
    artifact(tasks.shadowJar)
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveClassifier.set("")
        val libPrefix = "dev.thatpotato.potatoessentials.libs"
        relocate("dev.jorel.commandapi",
            "${libPrefix}.commandapi")
        relocate("com.tchristofferson.configupdater",
            "${libPrefix}.configupdater")
        relocate("org.bstats", "${libPrefix}.bstats")
    }
    withType<JavaCompile>().configureEach {
        options.compilerArgs.addAll(listOf("-source", "21", "-target", "21"))
        options.compilerArgs.addAll(listOf("-Xlint:unchecked", "-Xlint:deprecation"))
    }
    processResources {
        filter<ReplaceTokens>("tokens" to mapOf(
            "version" to project.version.toString()))
    }
    named("publishMavenPublicationToMavenLocal") {
        dependsOn(jar)
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

