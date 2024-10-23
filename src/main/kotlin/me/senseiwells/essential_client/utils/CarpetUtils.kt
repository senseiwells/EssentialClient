package me.senseiwells.essential_client.utils

import carpet.CarpetExtension
import carpet.CarpetServer
import carpet.api.settings.CarpetRule
import carpet.api.settings.SettingsManager
import me.senseiwells.essential_client.features.carpet_client.database.CarpetRuleData

object CarpetUtils {
    fun rules(): List<CarpetRule<*>> {
        val managers = arrayListOf<SettingsManager>(CarpetServer.settingsManager)
        CarpetServer.extensions.mapTo(managers, CarpetExtension::extensionSettingsManager)
        return managers.flatMap { it.carpetRules }
    }

    fun CarpetRule<*>.matches(data: CarpetRuleData): Boolean {
        if (data.settingManagers.contains(this.settingsManager().identifier())) {
            if (this.name() == data.name && this.type() == data.type.clazz()) {
                return true
            }
        }
        return false
    }
}