import me.modmuss50.mpp.ReleaseType
import xyz.jpenilla.resourcefactory.bukkit.Permission

plugins {
    `java-library`
    alias(libs.plugins.shadow)
    alias(libs.plugins.resourceFactoryBukkit)
    alias(libs.plugins.resourceFactoryPaper)
    alias(libs.plugins.indra)
    alias(libs.plugins.indraGit)
    alias(libs.plugins.runPaper)
    alias(libs.plugins.modPublishPlugin)
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
    compileOnly(libs.paper.api)

    implementation(libs.paper.trail)
    implementation(libs.legacy.plugin.base) {
        exclude("net.kyori")
    }
    implementation(libs.bstats.bukkit)

    implementation(platform(libs.cloud.bom))
    implementation(platform(libs.cloud.minecraft.bom))
    implementation(libs.cloud.paper)
    implementation(libs.cloud.minecraft.extras)
    implementation(platform(libs.cloud.translations.bom))
    implementation(libs.cloud.translations.bukkit)
    implementation(libs.cloud.translations.minecraft.extras)

    implementation(libs.interfaces.paper)

    compileOnly(libs.vaultApi)
    compileOnly(libs.essentialsX) {
        isTransitive = false
    }
    compileOnly(libs.jspecify)

    // Don't import their leaky constraints
    compileOnly(libs.worldguard.bukkit) { isTransitive = false }
    compileOnly(libs.worldguard.core) { isTransitive = false }
    compileOnly(libs.worldedit.bukkit) { isTransitive = false }
    compileOnly(libs.worldedit.core) { isTransitive = false }
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
        "1.21.9",
        "1.21.10",
        "1.21.11",
    )
    modLoaders.add("paper")
}

tasks {
    runServer {
        minecraftVersion("1.21.11")
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
        // Needed for mergeServiceFiles to work properly in Shadow 9+
        filesMatching("META-INF/services/**") {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
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
    compileJava {
        options.compilerArgs.add("-Xlint:-classfile,-processing")
    }
}

fun lastCommitHash(): String = indraGit.commit().orNull?.name?.substring(0, 7)
    ?: error("Could not determine commit hash")

fun String.decorateVersion(): String = if (endsWith("-SNAPSHOT")) "$this+${lastCommitHash()}" else this
