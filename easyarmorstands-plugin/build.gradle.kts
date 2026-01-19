import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    id("easyarmorstands.base")
    alias(libs.plugins.shadow)
    alias(libs.plugins.hangar.publish)
    alias(libs.plugins.minotaur)
    alias(libs.plugins.run.paper)
}

dependencies {
    compileOnly(libs.bukkit)
    // Folia support
    compileOnly("io.papermc.folia:folia-api:1.21.11-R0.1-SNAPSHOT")
    compileOnlyApi(libs.jetbrains.annotations)
    compileOnlyApi(libs.checker.qual)
    api(project(":easyarmorstands-api"))
    api(project(":easyarmorstands-assets"))
    api(project(":easyarmorstands-plugin-dependencies", configuration = "shadow"))
    runtimeOnly(project(":easyarmorstands-bentobox"))
    runtimeOnly(project(":easyarmorstands-display"))
    runtimeOnly(project(":easyarmorstands-fancyholograms"))
    runtimeOnly(project(":easyarmorstands-griefdefender"))
    runtimeOnly(project(":easyarmorstands-griefprevention"))
    runtimeOnly(project(":easyarmorstands-headdatabase"))
    runtimeOnly(project(":easyarmorstands-huskclaims"))
    runtimeOnly(project(":easyarmorstands-lands"))
    runtimeOnly(project(":easyarmorstands-plotsquared"))
    runtimeOnly(project(":easyarmorstands-residence"))
    runtimeOnly(project(":easyarmorstands-towny"))
    runtimeOnly(project(":easyarmorstands-traincarts"))
    runtimeOnly(project(":easyarmorstands-worldguard-v6"))
    runtimeOnly(project(":easyarmorstands-worldguard-v7"))
}

tasks {
    runServer {
        minecraftVersion("1.21.11")
        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    processResources {
        inputs.property("version", version)
        filesMatching("*.yml") {
            expand("version" to version)
        }
    }

    shadowJar {
        exclude("pack.mcmeta")
        mergeServiceFiles()
        archiveBaseName.set("EasyArmorStands")
        archiveClassifier.set("")
        destinationDirectory.set(layout.buildDirectory)
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    val staticJar by registering(Copy::class) {
        from(shadowJar)
        into(layout.buildDirectory.dir("static"))
        rename { "EasyArmorStands.jar" }
    }

    assemble {
        dependsOn(staticJar)
    }
}

fun registerSourceSet(name: String) {
    val sourceSet = sourceSets.register(name) {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }

    configurations.named("${name}CompileOnly") {
        extendsFrom(configurations.compileOnlyApi.get())
    }

    configurations.named("${name}Implementation") {
        extendsFrom(configurations.implementation.get())
    }

    tasks {
        shadowJar {
            from(sourceSet.map { it.output })
        }
    }
}

fun registerVersion(name: String, api: String) {
    registerSourceSet(name)
    dependencies {
        "${name}CompileOnly"(api)
    }
}

// Support only 1.21.10 and 1.21.11 with Folia
registerVersion("v1_21_10_paper", "io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")
registerVersion("v1_21_11_paper", "io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
registerVersion("v1_21_11_folia", "io.papermc.folia:folia-api:1.21.11-R0.1-SNAPSHOT")

val supportedGameVersions = listOf(
    "1.21.10",
    "1.21.11",
)

modrinth {
    projectId = "easyarmorstands"
    uploadFile.set(tasks.shadowJar)
    versionType = "release"
    changelog = provider { rootProject.file("CHANGELOG.md").readText() }
    syncBodyFrom = provider { rootProject.file("README.md").readText() }
    gameVersions = supportedGameVersions
    loaders = listOf("paper", "spigot", "bukkit")
}

hangarPublish {
    publications.register("plugin") {
        id = "EasyArmorStands"
        channel = "Release"
        version = project.version.toString()
        changelog = provider { rootProject.file("CHANGELOG.md").readText() }
        apiKey = System.getenv("HANGAR_API_TOKEN")
        platforms {
            register(Platforms.PAPER) {
                jar = tasks.shadowJar.flatMap { it.archiveFile }
                platformVersions = supportedGameVersions
            }
        }
        pages {
            resourcePage(provider { rootProject.file("README.md").readText() })
        }
    }
}
