package essentialclient.clientscript.extensions;

import essentialclient.clientscript.values.ScreenValue;
import essentialclient.utils.clientscript.ScreenRemapper;
import me.senseiwells.arucas.api.IArucasValueExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.ItemGroup;

import java.util.List;
import java.util.Set;

public class ArucasScreenMembers implements IArucasValueExtension {

	@Override
	public Set<MemberFunction> getDefinedFunctions() {
		return this.screenFunctions;
	}

	@Override
	public Class<ScreenValue> getValueType() {
		return ScreenValue.class;
	}

	@Override
	public String getName() {
		return "ScreenMemberFunctions";
	}

	private final Set<MemberFunction> screenFunctions = Set.of(
		new MemberFunction("getScreenName", List.of(), (context, function) -> new StringValue(ScreenRemapper.getScreenName(this.getScreen(context, function).getClass())), true),
		new MemberFunction("getName", (context, function) -> new StringValue(ScreenRemapper.getScreenName(this.getScreen(context, function).getClass()))),
		new MemberFunction("getTitle", this::getTitle)
	);

	private Value<?> getTitle(Context context, MemberFunction function) throws CodeError {
		Screen screen = this.getScreen(context, function);
		String title = screen.getTitle().getString();
        if (screen instanceof CreativeInventoryScreen creativeInventoryScreen) {
			int tabIndex = creativeInventoryScreen.getSelectedTab();
			title = ItemGroup.GROUPS[tabIndex].getName();
		}
		return title == null ? NullValue.NULL : new StringValue(title);
	}

	private Screen getScreen(Context context, MemberFunction function) throws CodeError {
		Screen screen = function.getParameterValueOfType(context, ScreenValue.class, 0).value;
		if (screen == null) {
			throw new RuntimeError("Screen was null", function.syntaxPosition, context);
		}
		return screen;
	}
}
