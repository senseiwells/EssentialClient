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
    mavenLocal()
}

val modVersion = "2.4.0"
val releaseVersion = "${modVersion}+${libs.versions.minecraft.get()}"
version = releaseVersion
group = "me.senseiwells"

dependencies {
    minecraft(libs.minecraft)

    implementation(libs.fabric.loader)
    implementation(libs.fabric.api)
    implementation(libs.fabric.kotlin)

    implementation(libs.yacl)
    include(implementation(libs.keybinds.get())!!)

    compileOnly(libs.mod.menu)
    compileOnly(libs.carpet)
    compileOnly(libs.chunk.debug)
    compileOnly(libs.sodium)

    localRuntime(libs.dev.auth)
}

java {
    withSourcesJar()
}

loom {
    runs {
        named("client") {
            // vmArgs("-Ddevauth.account=alt")
        }
    }
}

tasks {
    processResources {
        inputs.property("version", releaseVersion)
        filesMatching("fabric.mod.json") {
            expand(mutableMapOf(
                "version" to releaseVersion,
                "minecraft_dependency" to libs.versions.minecraft.get(),
                "yacl_dependency" to libs.versions.yacl.get(),
                "fabric_loader_dependency" to libs.versions.fabric.loader.get(),
            ))
        }
    }

    jar {
        from("LICENSE")
    }

    publishMods {
        file = jar.get().archiveFile
        changelog = """
        - Update to 26.1
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