plugins {
    val jvmVersion = libs.versions.fabric.kotlin.get()
        .split("+kotlin.")[1]
        .split("+")[0]

    kotlin("jvm").version(jvmVersion)
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.mod.publish)
    alias(libs.plugins.kotlin.serialization)
    `maven-publish`
    java
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.parchmentmc.org/")
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.isxander.dev/releases")
    maven("https://maven.supersanta.me/snapshots")
    maven("https://jitpack.io")
}

val modVersion = "2.0.0-beta.2"
val releaseVersion = "${modVersion}+${libs.versions.minecraft.get()}"
version = releaseVersion
group = "me.senseiwells"

dependencies {
    minecraft(libs.minecraft)
    @Suppress("UnstableApiUsage")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${libs.versions.parchment.get()}@zip")
    })

    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
    modImplementation(libs.fabric.kotlin)

    modImplementation(libs.mod.menu)
    modImplementation(libs.yacl)
    include(modImplementation(libs.keybinds.get())!!)

    modCompileOnly(libs.carpet)
    modCompileOnly(libs.chunk.debug)
}

java {
    withSourcesJar()
}

tasks {
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(mutableMapOf(
                "version" to project.version,
                "minecraft_dependency" to libs.versions.minecraft.get().replaceAfterLast('.', "x"),
                "yacl_dependency" to libs.versions.yacl.get(),
                "fabric_loader_dependency" to libs.versions.fabric.loader.get(),
            ))
        }
    }

    jar {
        from("LICENSE")
    }

    publishMods {
        file = remapJar.get().archiveFile
        changelog = """
        ## EssentialClient $modVersion
        
        Started re-writing EssentialClient.
        
        EssentialClient had many major design flaws making it challenging to maintain,
        thus this rewrite aims to keep the mod more maintainable throughout the future
        updates of the game.
        
        Some features from previous versions have been removed, either because I do not
        believe them to fit into this mod or because they are challenging to maintain.
        
        I have completely removed the scripting ability from EssentialClient as it had
        major flaws in its design that made it awkward to maintain and also work with.
        The long-term goal is to implement a better solution; however, this will take some
        time.
        
        ### Improvements:
        - Shifted to using YACL for managing configs
        - `highlightWaterSources` and `highlightLavaSources` has been improved
        - CarpetClient has been vastly improved
        - Better implementation of many features (better compatability)
        """.trimIndent()
        type = BETA
        modLoaders.add("fabric")

        displayName = "EssentialClient $modVersion for ${libs.versions.minecraft.get()}"
        version = releaseVersion

        modrinth {
            accessToken = providers.environmentVariable("MODRINTH_API_KEY")
            projectId = "sH0dfrKf"
            minecraftVersions.add(libs.versions.minecraft)

            requires {
                id = "P7dR8mSH"
            }
            requires {
                id = "Ha28R6CL"
            }
            requires {
                id = "1eAoo2KR"
            }
            optional {
                id = "zQxjhDPq"
            }
            optional {
                id = "TQTTVgYE"
            }
            optional {
                id = "mOgUt4GM"
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(project.components.getByName("java"))
        }
    }
}