plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.4.0"
    val indraVersion = "2.0.5"
    id("net.kyori.indra.git") version indraVersion
}

group = "xyz.jpenilla"
version = "1.6.5.3-SNAPSHOT+${lastCommitHash()}"
description = "Customizable trades for Wandering Traders."

repositories {
    //mavenLocal()
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
    compileOnly("com.destroystokyo.paper", "paper-api", "1.16.5-R0.1-SNAPSHOT")

    implementation("io.papermc", "paperlib", "1.0.6")
    implementation("xyz.jpenilla", "jmplib", "1.0.1+36-SNAPSHOT")
    implementation("org.bstats", "bstats-bukkit", "2.2.1")
    val cloudVersion = "1.5.0-SNAPSHOT"
    implementation("cloud.commandframework", "cloud-paper", cloudVersion)
    implementation("cloud.commandframework", "cloud-minecraft-extras", cloudVersion)

    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7")
    compileOnly("net.ess3", "EssentialsX", "2.18.2")
    compileOnly("org.checkerframework", "checker-qual", "3.13.0")
    compileOnly("com.sk89q.worldguard", "worldguard-bukkit", "7.0.2")
    compileOnly("com.sk89q.worldedit", "worldedit-bukkit", "7.1.0")
}

java {
    targetCompatibility = JavaVersion.toVersion(8)
    sourceCompatibility = JavaVersion.toVersion(8)
}

bukkit {
    main = "xyz.jpenilla.wanderingtrades.WanderingTrades"
    name = project.name
    apiVersion = "1.14"
    website = "https://github.com/jpenilla/WanderingTrades"
    authors = listOf("jmp")
    softDepend = listOf("McRPG", "WorldEdit", "WorldGuard", "Vault", "PlaceholderAPI", "ViaVersion")
}

tasks {
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
        filter { string -> string.replace("\${project.version}", project.version as String) }
    }
}

fun lastCommitHash(): String = indraGit.commit()?.name?.substring(0, 7)
    ?: error("Could not determine commit hash")
