plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.0"
    val indraVersion = "2.0.6"
    id("net.kyori.indra") version indraVersion
    id("net.kyori.indra.git") version indraVersion
    id("xyz.jpenilla.run-paper") version "1.0.4"
}

group = "xyz.jpenilla"
version = "1.6.7-SNAPSHOT"
    .run { if (endsWith("-SNAPSHOT")) "$this+${lastCommitHash()}" else this }
description = "Customizable trades for Wandering Traders."

repositories {
    //mavenLocal()
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.incendo.org/content/repositories/snapshots")
    maven("https://repo.jpenilla.xyz/snapshots")
    maven("https://ci.ender.zone/plugin/repository/everything/")
    maven("https://repo.codemc.org/repository/maven-public")
    maven("https://maven.enginehub.org/repo/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.destroystokyo.paper", "paper-api", "1.16.5-R0.1-SNAPSHOT")

    implementation("io.papermc", "paperlib", "1.0.6")
    implementation("xyz.jpenilla", "jmplib", "1.0.1+41-SNAPSHOT")
    implementation("org.bstats", "bstats-bukkit", "2.2.1")
    val cloudVersion = "1.5.0"
    implementation("cloud.commandframework", "cloud-paper", cloudVersion)
    implementation("cloud.commandframework", "cloud-minecraft-extras", cloudVersion)

    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7.1")
    compileOnly("net.ess3", "EssentialsX", "2.18.2")
    compileOnly("org.checkerframework", "checker-qual", "3.13.0")
    compileOnly("com.sk89q.worldguard", "worldguard-bukkit", "7.0.2")
    compileOnly("com.sk89q.worldedit", "worldedit-bukkit", "7.1.0")
}

indra {
    javaVersions().target(8)
}

bukkit {
    main = "xyz.jpenilla.wanderingtrades.WanderingTrades"
    name = project.name
    apiVersion = "1.14"
    website = "https://github.com/jpenilla/WanderingTrades"
    authors = listOf("jmp")
    softDepend = listOf("WorldEdit", "WorldGuard", "Vault", "PlaceholderAPI", "ViaVersion")
}

tasks {
    runServer {
        minecraftVersion("1.17.1")
    }
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        minimize()
        archiveFileName.set("${project.name}-${project.version}.jar")
        archiveClassifier.set("")
        sequenceOf(
            "org.bstats",
            "cloud.commandframework",
            "xyz.jpenilla.jmplib",
            "net.kyori",
            "io.papermc.lib",
            "io.leangen.geantyref"
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
