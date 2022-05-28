package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.GenericValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.MemberFunction;
import me.senseiwells.essentialclient.utils.clientscript.ScreenRemapper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Objects;

import static me.senseiwells.arucas.utils.ValueTypes.STRING;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.SCREEN;

public class ScreenValue<T extends Screen> extends GenericValue<T> {
	protected ScreenValue(T screen) {
		super(Objects.requireNonNull(screen));
	}

	@Override
	public ScreenValue<T> copy(Context context) {
		return this;
	}

	@Override
	public String getAsString(Context context) {
		return "Screen{screen=%s}".formatted(ScreenRemapper.getScreenName(this.value.getClass()));
	}

	@Override
	public int getHashCode(Context context) {
		return this.value.hashCode();
	}

	@Override
	public boolean isEquals(Context context, Value value) {
		return this.value == value.getValue();
	}

	@Override
	public String getTypeName() {
		return SCREEN;
	}

	/**
	 * This method should not be called directly,
	 * if you want to convert a Screen to an ScreenValue
	 * you should use {@link Context#convertValue(Object)}
	 */
	@Deprecated
	public static Value of(Screen screen) {
		return new ScreenValue<>(screen);
	}

	@ClassDoc(
		name = SCREEN,
		desc = "This allows you to get information about the player's current screen.",
		importPath = "Minecraft"
	)
	public static class ArucasScreenClass extends ArucasClassExtension {
		public ArucasScreenClass() {
			super(SCREEN);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("getName", this::getName),
				MemberFunction.of("getTitle", this::getTitle)
			);
		}

		@FunctionDoc(
			name = "getName",
			desc = "Gets the name of the specific screen",
			returns = {STRING, "the screen name, if you are in the creative menu it will return the name of the tab you are on"},
			example = "screen.getName()"
		)
		private Value getName(Arguments arguments) throws CodeError {
			return StringValue.of(ScreenRemapper.getScreenName(this.getScreen(arguments).getClass()));
		}

		@FunctionDoc(
			name = "getTitle",
			desc = "Gets the title of the specific screen",
			returns = {STRING, "the screen title as text, this may include formatting, and custom names for the screen if applicable"},
			example = "screen.getTitle()"
		)
		private Value getTitle(Arguments arguments) throws CodeError {
			Screen screen = this.getScreen(arguments);
			Text title = screen.getTitle();
			if (screen instanceof CreativeInventoryScreen creativeInventoryScreen) {
				int tabIndex = creativeInventoryScreen.getSelectedTab();
				return new TextValue(new LiteralText(ItemGroup.GROUPS[tabIndex].getName()));
			}
			return title == null ? NullValue.NULL : new TextValue(title.copy());
		}

		private Screen getScreen(Arguments arguments) throws CodeError {
			ScreenValue<?> screen = arguments.getNext(ScreenValue.class);
			if (screen.value == null) {
				throw arguments.getError("Screen was null");
			}
			return screen.value;
		}

		@Override
		public Class<? extends Value> getValueClass() {
			return ScreenValue.class;
		}
	}
}
