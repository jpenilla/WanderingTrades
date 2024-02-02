import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
    val indraVersion = "3.1.3"
    id("net.kyori.indra") version indraVersion
    id("net.kyori.indra.git") version indraVersion
    id("xyz.jpenilla.run-paper") version "2.2.3"
}

group = "xyz.jpenilla"
version = "1.8.4-SNAPSHOT".decorateVersion()
description = "Customizable trades for Wandering Traders."

val mcVersion = "1.20.2"

repositories {
    mavenCentral()
    sonatype.s01Snapshots()
    sonatype.ossSnapshots()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.jpenilla.xyz/snapshots/") {
        mavenContent { snapshotsOnly() }
    }
    maven("https://repo.essentialsx.net/releases/") {
        mavenContent { includeGroup("net.essentialsx") }
    }
    maven("https://maven.enginehub.org/repo/")
    maven("https://jitpack.io") {
        content { includeGroup("com.github.MilkBowl") }
    }
}

dependencies {
    compileOnly("io.papermc.paper", "paper-api", "1.20.1-R0.1-SNAPSHOT")

    implementation("io.papermc", "paperlib", "1.0.8")
    implementation("xyz.jpenilla", "legacy-plugin-base", "0.0.1+98-SNAPSHOT")
    implementation("org.bstats", "bstats-bukkit", "3.0.2")

    implementation(platform("cloud.commandframework:cloud-bom:1.8.4"))
    implementation("cloud.commandframework", "cloud-paper")
    implementation("cloud.commandframework", "cloud-minecraft-extras")

    implementation("org.incendo.interfaces", "interfaces-paper", "1.0.0-SNAPSHOT")

    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7.1")
    compileOnly("net.essentialsx", "EssentialsX", "2.20.1") {
        isTransitive = false
    }
    compileOnly("org.checkerframework", "checker-qual", "3.40.0")
    compileOnly("com.sk89q.worldguard", "worldguard-bukkit", "7.0.9") {
        exclude("org.bukkit")
    }
    compileOnly("com.sk89q.worldedit", "worldedit-bukkit", "7.2.17")
}

indra {
    javaVersions().target(17)
}

bukkit {
    main = "xyz.jpenilla.wanderingtrades.WanderingTrades"
    name = project.name
    apiVersion = "1.16"
    website = "https://github.com/jpenilla/WanderingTrades"
    authors = listOf("jmp")
    softDepend = listOf("WorldEdit", "WorldGuard", "Vault", "PlaceholderAPI", "ViaVersion")
    permissions {
        register("wanderingtrades.trader-spawn-notifications") {
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
    }
}

tasks {
    runServer {
        minecraftVersion(mcVersion)
    }
    assemble {
        dependsOn(shadowJar)
    }
    jar {
        archiveClassifier.set("noshade")
    }
    shadowJar {
        minimize()
        archiveFileName.set("${project.name}-${project.version}.jar")
        sequenceOf(
            "org.bstats",
            "cloud.commandframework",
            "xyz.jpenilla.pluginbase",
            "net.kyori",
            "io.papermc.lib",
            "io.leangen.geantyref",
            "org.incendo.interfaces",
        ).forEach {
            relocate(it, "xyz.jpenilla.wanderingtrades.lib.$it")
        }
    }
    processResources {
        val tokens = mapOf(
            "project.version" to project.version
        )
        inputs.properties(tokens)
        filesMatching("**/*.yml") {
            // Some of our files are too large to use Groovy templating
            filter { string ->
                var result = string
                for ((key, value) in tokens) {
                    result = result.replace("\${$key}", value.toString())
                }
                result
            }
        }
    }
}

fun lastCommitHash(): String = indraGit.commit()?.name?.substring(0, 7)
    ?: error("Could not determine commit hash")

fun String.decorateVersion(): String = if (endsWith("-SNAPSHOT")) "$this+${lastCommitHash()}" else this
