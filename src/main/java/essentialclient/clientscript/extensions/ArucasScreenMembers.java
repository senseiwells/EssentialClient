package essentialclient.clientscript.extensions;

import essentialclient.clientscript.values.ScreenValue;
import essentialclient.utils.clientscript.ScreenRemapper;
import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.AbstractBuiltInFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.ItemGroup;

import java.util.Set;

public class ArucasScreenMembers implements IArucasExtension {

	@Override
	public Set<? extends AbstractBuiltInFunction<?>> getDefinedFunctions() {
		return this.screenFunctions;
	}

	@Override
	public String getName() {
		return "ScreenMemberFunctions";
	}

	private final Set<? extends AbstractBuiltInFunction<?>> screenFunctions = Set.of(
		new MemberFunction("getScreenName", (context, function) -> new StringValue(ScreenRemapper.getScreenName(this.getScreen(context, function).getClass()))),
		new MemberFunction("getTitle", this::getTitle)
	);

	private Value<?> getTitle(Context context, MemberFunction function) throws CodeError {
		Screen screen = this.getScreen(context, function);
		String title = screen.getTitle().getString();
        if (screen instanceof CreativeInventoryScreen creativeInventoryScreen) {
			int tabIndex = creativeInventoryScreen.getSelectedTab();
			title = ItemGroup.GROUPS[tabIndex].getName();
		}
		return title == null ? new NullValue() : new StringValue(title);
	}

	private Screen getScreen(Context context, MemberFunction function) throws CodeError {
		Screen screen = function.getParameterValueOfType(context, ScreenValue.class, 0).value;
		if (screen == null) {
			throw new RuntimeError("Screen was null", function.syntaxPosition, context);
		}
		return screen;
	}
}
