package essentialclient.clientscript.extensions;

import essentialclient.clientscript.values.ScreenValue;
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

import java.util.Set;

public class ArucasScreenMembers implements IArucasExtension {

	@Override
	public Set<? extends AbstractBuiltInFunction<?>> getDefinedFunctions() {
		return this.livingEntityFunctions;
	}

	@Override
	public String getName() {
		return "ScreenMemberFunctions";
	}

	private final Set<? extends AbstractBuiltInFunction<?>> livingEntityFunctions = Set.of(
		new MemberFunction("getClassName", (context, function) -> new StringValue(this.getTitle(context, function).getClass().getSimpleName())),
		new MemberFunction("getTitle", this::getTitle)
	);

	private Value<?> getTitle(Context context, MemberFunction function) throws CodeError {
		String title = this.getScreen(context, function).getTitle().getString();
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
