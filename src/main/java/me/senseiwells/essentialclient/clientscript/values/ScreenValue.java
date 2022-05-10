package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.BaseValue;
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

public class ScreenValue<T extends Screen> extends Value<T> {
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
	public boolean isEquals(Context context, Value<?> value) {
		return this.value == value.value;
	}

	@Override
	public String getTypeName() {
		return "Screen";
	}

	/**
	 * This method should not be called directly,
	 * if you want to convert a Screen to an ScreenValue
	 * you should use {@link Context#convertValue(Object)}
	 */
	@Deprecated
	public static Value<?> of(Screen screen) {
		return new ScreenValue<>(screen);
	}

	/**
	 * Screen class for Arucas. <br>
	 * Import the class with <code>import Screen from Minecraft;</code> <br>
	 * Fully Documented.
	 * @author senseiwells
	 */
	public static class ArucasScreenClass extends ArucasClassExtension {
		public ArucasScreenClass() {
			super("Screen");
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("getName", this::getName),
				new MemberFunction("getTitle", this::getTitle)
			);
		}

		/**
		 * Name: <code>&lt;Screen>.getName()</code> <br>
		 * Description: Gets the name of the specific screen <br>
		 * Returns - String: the screen name, if you are in the creative menu
		 * it will return the name of the tab you are on <br>
		 * Example: <code>screen.getName()</code>
		 */
		private Value<?> getName(Context context, MemberFunction function) throws CodeError {
			return StringValue.of(ScreenRemapper.getScreenName(this.getScreen(context, function).getClass()));
		}

		/**
		 * Name: <code>&lt;Screen>.getTitle()</code> <br>
		 * Description: Gets the title of the specific screen <br>
		 * Returns - Text: the screen title as text, this may include formatting,
		 * and custom names for the screen if applicable <br>
		 * Example: <code>screen.getTitle()</code>
		 */
		private Value<?> getTitle(Context context, MemberFunction function) throws CodeError {
			Screen screen = this.getScreen(context, function);
			Text title = screen.getTitle();
			if (screen instanceof CreativeInventoryScreen creativeInventoryScreen) {
				int tabIndex = creativeInventoryScreen.getSelectedTab();
				return new TextValue(new LiteralText(ItemGroup.GROUPS[tabIndex].getName()));
			}
			return title == null ? NullValue.NULL : new TextValue(title.copy());
		}

		private Screen getScreen(Context context, MemberFunction function) throws CodeError {
			ScreenValue<?> screen = function.getParameterValueOfType(context, ScreenValue.class, 0);
			if (screen.value == null) {
				throw new RuntimeError("Screen was null", function.syntaxPosition, context);
			}
			return screen.value;
		}

		@Override
		public Class<? extends BaseValue> getValueClass() {
			return ScreenValue.class;
		}
	}
}
