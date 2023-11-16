package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.api.docs.annotations.ClassDoc;
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc;
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc;
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.classes.instance.ClassInstance;
import me.senseiwells.arucas.compiler.LocatableTrace;
import me.senseiwells.arucas.functions.builtin.Arguments;
import me.senseiwells.arucas.functions.builtin.MemberFunction;
import me.senseiwells.arucas.interpreter.Interpreter;
import me.senseiwells.arucas.utils.misc.Language;
import me.senseiwells.essentialclient.clientscript.core.MinecraftAPI;
import me.senseiwells.essentialclient.utils.clientscript.ScreenRemapper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.SCREEN;

@ClassDoc(
	name = SCREEN,
	desc = "This allows you to get information about the player's current screen.",
	language = Language.Java
)
public class ScreenDef extends PrimitiveDefinition<Screen> {
	public ScreenDef(Interpreter interpreter) {
		super(MinecraftAPI.SCREEN, interpreter);
	}

	@Deprecated
	@NotNull
	@Override
	public ClassInstance create(@NotNull Screen value) {
		return super.create(value);
	}

	@NotNull
	@Override
	public String toString(@NotNull ClassInstance instance, @NotNull Interpreter interpreter, @NotNull LocatableTrace trace) {
		return "Screen{screen=" + ScreenRemapper.getScreenName(instance.asPrimitive(this).getClass()) + "}";
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("getName", this::getName),
			MemberFunction.of("getTitle", this::getTitle)
		);
	}

	@FunctionDoc(
		name = "getName",
		desc = "Gets the name of the specific screen",
		returns = @ReturnDoc(type = StringDef.class, desc = "the screen name, if you are in the creative menu it will return the name of the tab you are on"),
		examples = "screen.getName()"
	)
	private String getName(Arguments arguments) {
		return ScreenRemapper.getScreenName(arguments.nextPrimitive(this).getClass());
	}

	@FunctionDoc(
		name = "getTitle",
		desc = "Gets the title of the specific screen",
		returns = @ReturnDoc(type = StringDef.class, desc = "the screen title as text, this may include formatting, and custom names for the screen if applicable"),
		examples = "screen.getTitle()"
	)
	private MutableText getTitle(Arguments arguments) {
		Screen screen = arguments.nextPrimitive(this);
		Text title = screen.getTitle();
		//#if MC < 11903
		//$$if (screen instanceof CreativeInventoryScreen creativeInventoryScreen) {
		//$$	int tabIndex = creativeInventoryScreen.getSelectedTab();
		//$$	return Text.literal(ItemGroup.GROUPS[tabIndex].getName());
		//$$}
		//#endif
		return title == null ? null : title.copy();
	}
}
