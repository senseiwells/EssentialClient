plugins {
    val jvmVersion = libs.versions.fabric.kotlin.get()
        .split("+kotlin.")[1]
        .split("+")[0]

    kotlin("jvm").version(jvmVersion)
    kotlin("plugin.serialization").version(jvmVersion)
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.mod.publish)
    `maven-publish`
    java
}

repositories {
    mavenCentral()
    maven("https://maven.parchmentmc.org/")
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.isxander.dev/releases")
    maven("https://maven.supersanta.me/snapshots")
    maven("https://api.modrinth.com/maven")
    maven("https://jitpack.io")
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

val modVersion = "2.2.3"
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
    modCompileOnly(libs.sodium)

    modRuntimeOnly(libs.dev.auth)
}

java {
    withSourcesJar()
}

tasks {
    processResources {
        inputs.property("version", releaseVersion)
        filesMatching("fabric.mod.json") {
            expand(mutableMapOf(
                "version" to releaseVersion,
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
        - Backport newest features/fixes to 1.21.1
        """.trimIndent()
        type = STABLE
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