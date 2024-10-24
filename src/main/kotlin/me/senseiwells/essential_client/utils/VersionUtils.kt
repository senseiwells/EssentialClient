package me.senseiwells.essential_client.utils

import net.minecraft.WorldVersion

object VersionUtils {
    fun WorldVersion.getMajorVersion(): String {
        return this.name.replaceAfterLast('.', "")
    }
}