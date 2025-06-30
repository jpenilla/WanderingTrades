import xyz.jpenilla.resourcefactory.bukkit.Permission

plugins {
    `java-library`
    id("com.gradleup.shadow") version "8.3.7"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.3.0"
    val indraVersion = "3.1.3"
    id("net.kyori.indra") version indraVersion
    id("net.kyori.indra.git") version indraVersion
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "xyz.jpenilla"
version = "1.8.6-SNAPSHOT".decorateVersion()
description = "Customizable trades for Wandering Traders."

val mcVersion = "1.21.4"

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
    compileOnly("io.papermc.paper", "paper-api", "1.21.1-R0.1-SNAPSHOT")

    implementation("io.papermc", "paperlib", "1.0.8")
    implementation("xyz.jpenilla", "legacy-plugin-base", "0.0.1+143-SNAPSHOT")
    implementation("org.bstats", "bstats-bukkit", "3.1.0")

    implementation(platform("org.incendo:cloud-bom:2.0.0"))
    implementation(platform("org.incendo:cloud-minecraft-bom:2.0.0-beta.10"))
    implementation("org.incendo:cloud-paper")
    implementation("org.incendo:cloud-minecraft-extras")
    implementation(platform("org.incendo:cloud-translations-bom:1.0.0-SNAPSHOT"))
    implementation("org.incendo:cloud-translations-bukkit")
    implementation("org.incendo:cloud-translations-minecraft-extras")

    implementation("org.incendo.interfaces", "interfaces-paper", "1.0.0-SNAPSHOT")

    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7.1")
    compileOnly("net.essentialsx", "EssentialsX", "2.21.0") {
        isTransitive = false
    }
    compileOnly("org.checkerframework", "checker-qual", "3.49.3")
    compileOnly("com.sk89q.worldguard", "worldguard-bukkit", "7.0.12") {
        exclude("org.bukkit")
    }
    compileOnly("com.sk89q.worldedit", "worldedit-bukkit", "7.3.9")
}

java {
    disableAutoTargetJvm()
}

indra {
    javaVersions{
        minimumToolchain(21)
        target(17)
    }
}

bukkitPluginYaml {
    main = "xyz.jpenilla.wanderingtrades.WanderingTrades"
    apiVersion = "1.16"
    website = "https://github.com/jpenilla/WanderingTrades"
    authors = listOf("jmp")
    softDepend = listOf("WorldEdit", "WorldGuard", "Vault", "PlaceholderAPI", "ViaVersion")
    permissions {
        register("wanderingtrades.trader-spawn-notifications") {
            default = Permission.Default.TRUE
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
        manifest {
            attributes("paperweight-mappings-namespace" to "mojang")
        }
    }
    shadowJar {
        archiveFileName.set("${project.name}-${project.version}.jar")
        sequenceOf(
            "org.bstats",
            "org.incendo",
            "xyz.jpenilla.pluginbase",
            "net.kyori",
            "io.papermc.lib",
            "io.leangen.geantyref",
        ).forEach {
            relocate(it, "xyz.jpenilla.wanderingtrades.lib.$it")
        }
        mergeServiceFiles()
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
    compileJava {
        options.compilerArgs.add("-Xlint:-classfile,-processing")
    }
}

fun lastCommitHash(): String = indraGit.commit()?.name?.substring(0, 7)
    ?: error("Could not determine commit hash")

fun String.decorateVersion(): String = if (endsWith("-SNAPSHOT")) "$this+${lastCommitHash()}" else this
