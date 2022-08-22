package me.senseiwells.essentialclient.utils.interfaces;

import net.minecraft.client.gui.hud.ChatHudLine;

import java.util.List;

public interface ChatHudAccessor {
	List<ChatHudLine> getMessages();
}
