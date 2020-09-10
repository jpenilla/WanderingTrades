import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.commons.io.output.ByteArrayOutputStream

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("kr.entree.spigradle") version "2.1.1"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

configurations.all {
    exclude(group = "org.checkerframework")
}

val projectName = "WanderingTrades"
group = "xyz.jpenilla"
version = "1.6.4.1+${getLastCommitHash()}-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://papermc.io/repo/repository/maven-public/")
    maven(url = "https://oss.sonatype.org/content/groups/public/")
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
    compileOnly("com.destroystokyo.paper", "paper-api", "1.14.4-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7")
    compileOnly("net.ess3", "EssentialsX", "2.17.2")
    compileOnly("org.jetbrains", "annotations", "20.0.0")
    compileOnly("com.sk89q.worldguard", "worldguard-bukkit", "7.0.2")
    compileOnly("com.sk89q.worldedit", "worldedit-bukkit", "7.1.0")
    compileOnly("com.github.Eunoians", "McRPG", "1.3.3.0-BETA")
    implementation("xyz.jpenilla", "jmplib", "1.0.0+103-SNAPSHOT")
    implementation("co.aikar", "acf-paper", "0.5.0-SNAPSHOT")
    implementation("org.bstats", "bstats-bukkit", "1.7")
    implementation("commons-io", "commons-io", "2.7")
    implementation("org.apache.commons", "commons-math3", "3.6.1")
}

spigot {
    name = projectName
    apiVersion = "1.14"
    description = "Customizable Trades for Wandering Traders. Inspired by Vanilla Tweaks"
    website = "https://www.spigotmc.org/resources/wandering-trades.79068/"
    authors("jmp")
    softDepends("McRPG", "WorldEdit", "WorldGuard", "Vault", "Prisma", "PlaceholderAPI")
}

val autoRelocate by tasks.register<ConfigureShadowRelocation>("configureShadowRelocation", ConfigureShadowRelocation::class) {
    target = tasks.getByName("shadowJar") as ShadowJar?
    val packageName = "${project.group}.${project.name.toLowerCase()}"
    prefix = "$packageName.shaded"
}

tasks {
    compileJava {
        options.compilerArgs.add("-parameters")
        options.isFork = true
        options.forkOptions.executable = "javac"
    }
    withType<ShadowJar> {
        archiveClassifier.set("")
        archiveFileName.set("$projectName-${project.version}.jar")
        dependsOn(autoRelocate)
        minimize()
    }
}

fun getLastCommitHash(): String {
    val byteOut = ByteArrayOutputStream()
    exec {
        commandLine = listOf("git", "rev-parse", "--short", "HEAD")
        standardOutput = byteOut
    }
    return byteOut.toString().trim()
}