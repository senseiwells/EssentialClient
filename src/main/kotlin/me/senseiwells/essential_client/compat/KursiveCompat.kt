package me.senseiwells.essential_client.compat

import net.minecraft.client.gui.screens.Screen

object KursiveCompat {
    private const val MENU_SCREEN_CLASS_NAME = "me.senseiwells.kursive.client.gui.menu.KursiveMenuScreen"

    private val menuScreenClass by lazy(::tryFindMenuScreenClass)

    fun hasKursiveInstalled(): Boolean {
        return this.menuScreenClass != null
    }

    fun createKursiveMenu(parent: Screen? = null): Screen? {
        val clazz = this.menuScreenClass ?: return null
        try {
            val constructor = clazz.getConstructor(Screen::class.java)
            return constructor.newInstance(parent) as Screen
        } catch (_: NoSuchMethodException) {
            return null
        }
    }

    private fun tryFindMenuScreenClass(): Class<*>? {
        return try {
            Class.forName(MENU_SCREEN_CLASS_NAME)
        } catch (_: ClassNotFoundException) {
            null
        }
    }
}