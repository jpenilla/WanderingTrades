import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import org.apache.commons.io.output.ByteArrayOutputStream

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("kr.entree.spigradle") version "2.2.3"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val projectName = "WanderingTrades"
group = "xyz.jpenilla"
version = "1.6.5+${getLastCommitHash()}-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/groups/public/")
    maven(url = "https://papermc.io/repo/repository/maven-public/")
    maven(url = "https://repo.aikar.co/content/groups/aikar/")
    maven(url = "https://repo.jpenilla.xyz/snapshots")
    maven(url = "https://ci.ender.zone/plugin/repository/everything/")
    maven(url = "https://repo.codemc.org/repository/maven-public")
    maven(url = "https://maven.enginehub.org/repo/")
    maven(url = "https://jitpack.io")
}

dependencies {
    annotationProcessor("org.projectlombok", "lombok", "1.18.12")

    compileOnly("org.projectlombok", "lombok", "1.18.12")
    compileOnly("com.destroystokyo.paper", "paper-api", "1.16.3-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7")
    compileOnly("net.ess3", "EssentialsX", "2.17.2")
    compileOnly("org.checkerframework", "checker-qual", "3.5.0")
    compileOnly("com.sk89q.worldguard", "worldguard-bukkit", "7.0.2")
    compileOnly("com.sk89q.worldedit", "worldedit-bukkit", "7.1.0")
    compileOnly("com.github.Eunoians", "McRPG", "1.3.3.0-BETA")

    implementation("xyz.jpenilla", "jmplib", "1.0.1+17-SNAPSHOT")
    implementation("org.bstats", "bstats-bukkit", "1.7")

    val cloudVersion = "1.0.1"
    implementation("cloud.commandframework", "cloud-paper", cloudVersion)
    implementation("cloud.commandframework", "cloud-minecraft-extras", cloudVersion)
}

spigot {
    name = projectName
    apiVersion = "1.14"
    description = "Customizable Trades for Wandering Traders. Inspired by Vanilla Tweaks"
    website = "https://github.com/jmanpenilla/WanderingTrades"
    authors("jmp")
    softDepends("McRPG", "WorldEdit", "WorldGuard", "Vault", "Prisma", "PlaceholderAPI", "ViaVersion")
}

val autoRelocate by tasks.register("configureShadowRelocation", ConfigureShadowRelocation::class) {
    target = tasks.shadowJar.get()
    val packageName = "${project.group}.${project.name.toLowerCase()}"
    prefix = "$packageName.shaded"
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        minimize()
        dependsOn(autoRelocate)
        archiveClassifier.set("")
        archiveFileName.set("$projectName-${project.version}.jar")
    }
}

fun getLastCommitHash(): String {
    val byteOut = ByteArrayOutputStream()
    exec {
        commandLine = listOf("git", "rev-parse", "--short", "HEAD")
        standardOutput = byteOut
    }
    return byteOut.toString(Charsets.UTF_8).trim()
}