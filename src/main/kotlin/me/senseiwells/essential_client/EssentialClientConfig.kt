package me.senseiwells.essential_client

import com.mojang.blaze3d.platform.InputConstants
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.SerialEntry
import dev.isxander.yacl3.config.v2.api.autogen.*
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import me.senseiwells.essential_client.EssentialClient.id
import me.senseiwells.essential_client.utils.yacl.ReloadChunks
import me.senseiwells.essential_client.utils.yacl.ReloadResources
import me.senseiwells.keybinds.api.InputKeys
import me.senseiwells.keybinds.api.Keybind
import me.senseiwells.keybinds.api.KeybindManager
import me.senseiwells.keybinds.api.yacl.Keybinding
import net.fabricmc.fabric.api.util.TriState
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screens.Screen
import java.nio.file.Path
import kotlin.Boolean
import dev.isxander.yacl3.config.v2.api.autogen.Boolean as Bool

class EssentialClientConfig {
    @IntField
    @AutoGen(category = "gameplay")
    @SerialEntry var autoWalk: Int = 0

    @Bool(colored = true)
    @AutoGen(category = "gameplay")
    @CustomDescription
    @SerialEntry var betterAccurateBlockPlacement: Boolean = false

    @Bool(colored = true)
    @AutoGen(category = "gameplay")
    @SerialEntry var increaseSpectatorScrollSpeed: Boolean = false

    @IntSlider(min = 0, max = 10, step = 1)
    @AutoGen(category = "gameplay")
    @SerialEntry var increaseSpectatorScrollSensitivity: Int = 0

    @IntSlider(min = 0, max = 20, step = 1)
    @AutoGen(category = "gameplay")
    @SerialEntry var switchToTotem: Int = 0

    @IntField
    @AutoGen(category = "technical")
    @SerialEntry var announceAfk: Int = 0

    @StringField
    @AutoGen(category = "technical")
    @SerialEntry var announceAfkMessage: String = "I am now AFK"

    @StringField
    @AutoGen(category = "technical")
    @SerialEntry var announceBackMessage: String = ""

    @Bool(colored = true)
    @AutoGen(category = "technical")
    @SerialEntry var carpetAlwaysSetDefault: Boolean = false

    @DoubleField
    @AutoGen(category = "technical")
    @SerialEntry var creativeWalkSpeed: Double = 0.0

    @Bool(colored = true)
    @AutoGen(category = "rendering")
    @SerialEntry var disableArmorRendering: Boolean = false

    @Bool(colored = true)
    @AutoGen(category = "rendering")
    @SerialEntry var disableDamageTilt: Boolean = false

    @Bool(colored = true)
    @AutoGen(category = "rendering")
    @SerialEntry var disableBossbarRendering: Boolean = false

    @Bool(colored = true)
    @AutoGen(category = "rendering")
    @SerialEntry var disableMapRendering: Boolean = false

    @Bool(colored = true)
    @AutoGen(category = "rendering")
    @SerialEntry var disableNametagRendering: Boolean = false

    @Bool(colored = true)
    @AutoGen(category = "rendering")
    @SerialEntry var disableNightVisionFlashing: Boolean = false

    @Bool(colored = true)
    @AutoGen(category = "rendering")
    @SerialEntry var disableRecipeToasts: Boolean = false

    @Bool(colored = true)
    @AutoGen(category = "rendering")
    @SerialEntry var disableAdvancementToasts: Boolean = false

    @Bool(colored = true)
    @AutoGen(category = "rendering")
    @SerialEntry var disableTutorialToasts: Boolean = false

    @Bool(colored = true)
    @AutoGen(category = "rendering")
    @SerialEntry var disableJoinLeaveMessages: Boolean = false

    @Bool(colored = true)
    @AutoGen(category = "rendering")
    @SerialEntry var disableScreenshotMessages: Boolean = false

    @Bool(colored = true)
    @AutoGen(category = "rendering")
    @SerialEntry var displayPlayTime: Boolean = false

    @Bool(colored = true)
    @AutoGen(category = "rendering")
    @SerialEntry var essentialClientButton: Boolean = false

    @ReloadChunks
    @FloatSlider(min = 0.0F, max = 1.0F, step = 0.05F)
    @AutoGen(category = "rendering")
    @SerialEntry var lavaOpacity: Float = 1.0F

    @ReloadChunks
    @Bool(colored = true)
    @AutoGen(category = "rendering")
    @SerialEntry var highlightLavaSources: Boolean = false

    @ReloadChunks
    @Bool(colored = true)
    @AutoGen(category = "rendering")
    @SerialEntry var highlightWaterSources: Boolean = false

    @Bool(colored = true)
    @AutoGen(category = "rendering")
    @SerialEntry var persistentChatHistory: Boolean = false

    @Bool(colored = true)
    @AutoGen(category = "rendering")
    @SerialEntry var toggleTab: Boolean = false

    @Keybinding(id = ESSENTIAL_MENU_KEYBIND)
    @AutoGen(category = "keybinds")
    @SerialEntry var essentialClientMenuKeys: InputKeys = InputKeys.of(InputConstants.KEY_F7)

    @Keybinding(id = ACCURATE_REVERSE_KEYBIND)
    @AutoGen(category = "keybinds")
    @SerialEntry var accurateReverseKeys: InputKeys = InputKeys.EMPTY

    @Keybinding(id = ACCURATE_INTO_KEYBIND)
    @AutoGen(category = "keybinds")
    @SerialEntry var accurateIntoKeys: InputKeys = InputKeys.EMPTY

    companion object {
        private const val ESSENTIAL_CATEGORY = "key.categories.essential-client"
        private const val ESSENTIAL_MENU_KEYBIND = "${EssentialClient.MOD_ID}:open_menu"
        private const val ACCURATE_REVERSE_KEYBIND = "${EssentialClient.MOD_ID}:accurate_reverse"
        private const val ACCURATE_INTO_KEYBIND = "${EssentialClient.MOD_ID}:accurate_into"

        private val directory: Path = FabricLoader.getInstance().configDir.resolve("EssentialClient")
        private val handler: ConfigClassHandler<EssentialClientConfig> = createHandler()

        @JvmStatic
        val instance: EssentialClientConfig
            get() = this.handler.instance()

        val essentialMenuKeybind: Keybind
        val accurateReverseKeybind: Keybind
        val accurateIntoKeybind: Keybind

        init {
            this.handler.load()

            val config = this.instance
            this.essentialMenuKeybind = register(ESSENTIAL_MENU_KEYBIND, config.essentialClientMenuKeys)
            this.accurateReverseKeybind = register(ACCURATE_REVERSE_KEYBIND, config.accurateReverseKeys)
            this.accurateIntoKeybind = register(ACCURATE_INTO_KEYBIND, config.accurateIntoKeys)
        }

        fun screen(parent: Screen? = null): Screen {
            return this.handler.generateGui().generateScreen(parent)
        }

        fun resolve(path: String): Path {
            return this.directory.resolve(path)
        }

        internal fun load() {

        }

        private fun register(id: String, keys: InputKeys): Keybind {
            val keybind = KeybindManager.register(id(id), keys)
            KeybindManager.addToControlsScreen(ESSENTIAL_CATEGORY, keybind)
            return keybind
        }

        private fun createHandler(): ConfigClassHandler<EssentialClientConfig> {
            return ConfigClassHandler.createBuilder(EssentialClientConfig::class.java)
                .id(id("config"))
                .serializer { config ->
                    GsonConfigSerializerBuilder.create(config)
                        .setPath(this.directory.resolve("config.json5"))
                        .appendGsonBuilder { obj ->
                            obj.setPrettyPrinting()
                            obj.registerTypeAdapter(InputKeys::class.java, InputKeys.Serializer.INSTANCE)
                        }
                        .build()
                }
                .build()
        }
    }
}