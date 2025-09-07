import me.modmuss50.mpp.ReleaseType
import xyz.jpenilla.resourcefactory.bukkit.Permission

plugins {
    `java-library`
    id("com.gradleup.shadow") version "9.1.0"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.3.1"
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.3.1"
    val indraVersion = "3.2.0"
    id("net.kyori.indra") version indraVersion
    id("net.kyori.indra.git") version indraVersion
    id("xyz.jpenilla.run-paper") version "3.0.0"
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
}

version = (version as String).decorateVersion()

repositories {
    mavenCentral {
        mavenContent { releasesOnly() }
    }
    maven("https://repo.jpenilla.xyz/snapshots/") {
        mavenContent {
            snapshotsOnly()
            includeGroup("xyz.jpenilla")
            includeModule("org.incendo.interfaces", "interfaces-paper")
            includeModule("org.incendo.interfaces", "interfaces-core")
            includeModule("net.kyori", "adventure-text-feature-pagination")
        }
    }
    maven("https://central.sonatype.com/repository/maven-snapshots/") {
        mavenContent { snapshotsOnly() }
    }
    maven("https://repo.papermc.io/repository/maven-public/") {
        mavenContent {
            includeGroup("io.papermc")
            includeGroup("io.papermc.paper")
            includeModule("com.mojang", "brigadier")
            includeModule("net.md-5", "bungeecord-chat")
        }
    }
    maven("https://repo.essentialsx.net/releases/") {
        mavenContent {
            releasesOnly()
            includeGroup("net.essentialsx")
        }
    }
    maven("https://maven.enginehub.org/repo/") {
        mavenContent {
            includeGroup("com.sk89q.worldguard")
            includeGroup("com.sk89q.worldguard.worldguard-libs")
            includeGroup("com.sk89q.worldedit")
            includeGroup("com.sk89q.worldedit.worldedit-libs")
        }
    }
    maven("https://jitpack.io") {
        content { includeGroup("com.github.MilkBowl") }
    }
}

dependencies {
    compileOnly("io.papermc.paper", "paper-api", "1.21.4-R0.1-SNAPSHOT")

    implementation("io.papermc:paper-trail:1.0.1")
    implementation("xyz.jpenilla", "legacy-plugin-base", "0.0.1+155-SNAPSHOT") {
        exclude("net.kyori")
    }
    implementation("org.bstats", "bstats-bukkit", "3.1.0")

    implementation(platform("org.incendo:cloud-bom:2.0.0"))
    implementation(platform("org.incendo:cloud-minecraft-bom:2.0.0-beta.11"))
    implementation("org.incendo:cloud-paper")
    implementation("org.incendo:cloud-minecraft-extras")
    implementation(platform("org.incendo:cloud-translations-bom:1.0.0-SNAPSHOT"))
    implementation("org.incendo:cloud-translations-bukkit")
    implementation("org.incendo:cloud-translations-minecraft-extras")

    implementation("org.incendo.interfaces", "interfaces-paper", "1.0.0-SNAPSHOT")

    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7.1")
    compileOnly("net.essentialsx", "EssentialsX", "2.21.2") {
        isTransitive = false
    }
    compileOnly("org.jspecify:jspecify:1.0.0")

    // Don't import their leaky constraints
    val worldGuardVer = "7.0.14"
    val worldEditVer = "7.3.16"
    compileOnly("com.sk89q.worldguard", "worldguard-bukkit", worldGuardVer) { isTransitive = false }
    compileOnly("com.sk89q.worldguard", "worldguard-core", worldGuardVer) { isTransitive = false }
    compileOnly("com.sk89q.worldedit", "worldedit-bukkit", worldEditVer) { isTransitive = false }
    compileOnly("com.sk89q.worldedit", "worldedit-core", worldEditVer) { isTransitive = false }
}

indra {
    javaVersions{
        target(21)
    }
}

paperPluginYaml {
    main = "xyz.jpenilla.wanderingtrades.WanderingTrades"
    apiVersion = "1.21.4"
    website = "https://github.com/jpenilla/WanderingTrades"
    authors = listOf("jmp")

    permissions {
        register("wanderingtrades.trader-spawn-notifications") {
            default = Permission.Default.TRUE
        }
    }

    dependencies.server.register("WorldEdit") {
        required = false
    }
    dependencies.server.register("WorldGuard") {
        required = false
    }
    dependencies.server.register("Vault") {
        required = false
    }
    dependencies.server.register("PlaceholderAPI") {
        required = false
    }
}

bukkitPluginYaml {
    main = "wanderingtrades.io.papermc.papertrail.RequiresPaperPlugins"
    apiVersion = "1.21.4"
    authors = listOf("jmp")
}

publishMods.modrinth {
    projectId = "ZfddU72x"
    type = ReleaseType.STABLE
    file = tasks.shadowJar.flatMap { it.archiveFile }
    changelog = providers.environmentVariable("RELEASE_NOTES")
    accessToken = providers.environmentVariable("MODRINTH_TOKEN")
    minecraftVersions.addAll(
        "1.21.4",
        "1.21.5",
        "1.21.6",
        "1.21.7",
        "1.21.8",
    )
    modLoaders.add("paper")
}

tasks {
    runServer {
        minecraftVersion("1.21.8")
    }
    assemble {
        dependsOn(shadowJar)
    }
    jar {
        archiveClassifier.set("noshade")
    }
    shadowJar {
        archiveFileName.set("${project.name}-${project.version}.jar")
        sequenceOf(
            "org.bstats",
            "org.incendo",
            "xyz.jpenilla.pluginbase",
            "io.papermc.lib",
            "io.leangen.geantyref",
            "io.papermc.papertrail"
        ).forEach {
            relocate(it, "wanderingtrades.$it")
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
