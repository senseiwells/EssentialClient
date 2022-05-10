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

		private Value<?> getName(Context context, MemberFunction function) throws CodeError {
			return StringValue.of(ScreenRemapper.getScreenName(this.getScreen(context, function).getClass()));
		}

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
