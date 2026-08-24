import org.gradle.kotlin.dsl.implementation

plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "3.1.0"
    id("com.gradleup.shadow") version "9.4.2"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.extendedclip.com/releases/")
}

dependencies {
    // PAPER
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")

    implementation(platform("com.intellectualsites.bom:bom-newest:1.55"))
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit")
    compileOnly("me.clip:placeholderapi:2.12.3")
    implementation("com.zaxxer:HikariCP:7.1.0")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        destinationDirectory.set(file("D:/Minecraft/servers/26.2/plugins"))
        archiveClassifier.set("")
        relocate ("com.zaxxer.hikari", "me.stivendarsi.libs.hikari")
      //  relocate("io.lettuce", "me.stivendarsi.libs.lettuce")
    }

    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("26.2")
        jvmArgs("-Xms2G", "-Xmx2G")
    }

    processResources {
        val props = mapOf("version" to version)
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}
