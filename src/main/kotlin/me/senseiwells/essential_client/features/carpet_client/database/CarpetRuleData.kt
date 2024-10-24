package me.senseiwells.essential_client.features.carpet_client.database

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import me.senseiwells.essential_client.features.carpet_client.yacl.CarpetOptionType
import me.senseiwells.essential_client.utils.VersionUtils.getMajorVersion
import net.minecraft.SharedConstants

@Serializable
data class CarpetRuleData(
    val name: String,
    val description: String,
    @SerialName("type")
    val typeAsString: String,
    @SerialName("value")
    val defaultValueAsString: String,
    val strict: Boolean,
    val categories: List<String>,
    val options: List<String>,
    val extras: List<String> = listOf(),
    val validators: List<String> = listOf(),
    @SerialName("config_files")
    val settingManagers: List<String> = listOf(),
    @SerialName("mod_name")
    val modName: String,
    @SerialName("mod_slug")
    val modSlug: String,
    @SerialName("mod_url")
    val modUrl: String,
    @SerialName("minecraft_versions")
    val minecraftVersions: List<String>,
    @Transient
    val type: CarpetOptionType<*> = CarpetOptionType.resolve(typeAsString, categories),
    @Transient
    val defaultValue: Any? = type.mapFromString(defaultValueAsString),
    @Transient
    var value: Any? = defaultValue
) {
    val defaultManager: String
        get() = this.settingManagers.firstOrNull() ?: "carpet"

    fun compareMinecraftVersions(other: CarpetRuleData): Int {
        val versionA = this.minecraftVersions.lastOrNull()
        val versionB = other.minecraftVersions.lastOrNull()
        if (versionA == null) {
            if (versionB == null) {
                return 0
            }
            return -1
        }
        if (versionB == null) {
            return 1
        }
        return versionA.compareTo(versionB)
    }

    companion object {
        fun createDefault(name: String, value: String, manager: String): CarpetRuleData {
            return CarpetRuleData(
                name,
                "",
                "String",
                value,
                false,
                listOf(),
                listOf(),
                listOf(),
                listOf(),
                listOf(manager),
                "Unknown",
                "",
                "",
                listOf(SharedConstants.getCurrentVersion().getMajorVersion())
            )
        }
    }
}