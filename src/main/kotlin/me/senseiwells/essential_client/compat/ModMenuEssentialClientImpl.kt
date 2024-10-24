package me.senseiwells.essential_client.compat

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.senseiwells.essential_client.EssentialClientConfig
import me.senseiwells.essential_client.gui.EssentialClientScreen

object ModMenuEssentialClientImpl: ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory(::EssentialClientScreen)
    }
}