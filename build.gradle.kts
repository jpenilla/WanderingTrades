import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import org.apache.commons.io.output.ByteArrayOutputStream

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("kr.entree.spigradle") version "2.2.3"
}

group = "xyz.jpenilla"
version = "1.6.5.3+${getLastCommitHash()}-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://repo.incendo.org/content/repositories/snapshots")
    maven("https://repo.jpenilla.xyz/snapshots")
    maven("https://ci.ender.zone/plugin/repository/everything/")
    maven("https://repo.codemc.org/repository/maven-public")
    maven("https://maven.enginehub.org/repo/")
    maven("https://jitpack.io")
}

dependencies {
    annotationProcessor("org.projectlombok", "lombok", "1.18.12")
    compileOnly("org.projectlombok", "lombok", "1.18.12")

    compileOnly("com.destroystokyo.paper", "paper-api", "1.16.5-R0.1-SNAPSHOT")
    implementation("io.papermc", "paperlib", "1.0.6")
    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7")
    compileOnly("net.ess3", "EssentialsX", "2.17.2")
    compileOnly("org.checkerframework", "checker-qual", "3.11.0")
    compileOnly("com.sk89q.worldguard", "worldguard-bukkit", "7.0.2")
    compileOnly("com.sk89q.worldedit", "worldedit-bukkit", "7.1.0")
    compileOnly("com.github.Eunoians", "McRPG", "1.3.3.0-BETA")

    implementation("xyz.jpenilla", "jmplib", "1.0.1+33-SNAPSHOT")
    implementation("org.bstats", "bstats-bukkit", "2.2.1")

    val cloudVersion = "1.5.0-SNAPSHOT"
    implementation("cloud.commandframework", "cloud-paper", cloudVersion)
    implementation("cloud.commandframework", "cloud-minecraft-extras", cloudVersion)
}

java {
    sourceCompatibility = JavaVersion.toVersion(8)
    targetCompatibility = JavaVersion.toVersion(8)
}

spigot {
    name = project.name
    apiVersion = "1.14"
    description = "Customizable Trades for Wandering Traders. Inspired by Vanilla Tweaks"
    website = "https://github.com/jpenilla/WanderingTrades"
    authors("jmp")
    softDepends("McRPG", "WorldEdit", "WorldGuard", "Vault", "PlaceholderAPI", "ViaVersion")
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    register<ConfigureShadowRelocation>("autoRelocate") {
        target = shadowJar.get()
        val packageName = "${project.group}.${project.name.toLowerCase()}"
        prefix = "$packageName.lib"
    }
    shadowJar {
        minimize()
        dependsOn(withType<ConfigureShadowRelocation>())
        archiveClassifier.set("")
        archiveFileName.set("${project.name}-${project.version}.jar")
    }
    processResources {
        filter { string -> string.replace("\${project.version}", project.version as String) }
    }
}

fun getLastCommitHash(): String = ByteArrayOutputStream().apply {
    exec {
        commandLine = listOf("git", "rev-parse", "--short", "HEAD")
        standardOutput = this@apply
    }
}.toString(Charsets.UTF_8).trim()
