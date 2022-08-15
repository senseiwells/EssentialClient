package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.essentialclient.clientscript.core.MinecraftAPI;
import net.minecraft.text.Text;

// TODO
public class TextDef extends CreatableDefinition<Text> {
	public TextDef(Interpreter interpreter) {
		super(MinecraftAPI.TEXT, interpreter);
	}
}
