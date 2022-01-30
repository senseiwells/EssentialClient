package essentialclient.clientscript.values;

import essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import essentialclient.utils.clientscript.ScreenRemapper;
import essentialclient.utils.render.FakeInventoryScreen;
import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandler;

import java.util.List;
import java.util.stream.Collectors;

public class ScreenValue<T extends Screen> extends Value<T> {
	protected ScreenValue(T screen) {
		super(screen);
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

	public static Value<?> of(Screen screen) {
		if (screen instanceof MerchantScreen merchantScreen) {
			return new MerchantScreenValue(merchantScreen);
		}
		if (screen instanceof FakeInventoryScreen fakeInventoryScreen) {
			return new FakeInventoryScreenValue(fakeInventoryScreen);
		}
		return screen == null ? NullValue.NULL : new ScreenValue<>(screen);
	}

	public static class ArucasScreenClass extends ArucasClassExtension {
		public ArucasScreenClass() {
			super("Screen");
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("getName", (context, function) -> StringValue.of(ScreenRemapper.getScreenName(this.getScreen(context, function).getClass()))),
				new MemberFunction("getTitle", this::getTitle),
				new MemberFunction("getOrderedSlotList", this::getOrderedSlotList),
				new MemberFunction("getOrderedSlot","index", this::getOrderedSlot)
			);
		}

		private Value<?> getTitle(Context context, MemberFunction function) throws CodeError {
			Screen screen = this.getScreen(context, function);
			String title = screen.getTitle().getString();
			if (screen instanceof CreativeInventoryScreen creativeInventoryScreen) {
				int tabIndex = creativeInventoryScreen.getSelectedTab();
				title = ItemGroup.GROUPS[tabIndex].getName();
			}
			return title == null ? NullValue.NULL : StringValue.of(title);
		}

		private Screen getScreen(Context context, MemberFunction function) throws CodeError {
			ScreenValue<?> screen = function.getParameterValueOfType(context, ScreenValue.class, 0);
			if (screen.value == null) {
				throw new RuntimeError("Screen was null", function.syntaxPosition, context);
			}
			return screen.value;
		}
		private Value<?> getOrderedSlotList(Context context, MemberFunction function) throws CodeError {
			final MinecraftClient client = ArucasMinecraftExtension.getClient();
			ScreenHandler abstractScreenHandler = client.currentScreen != null ? ((HandledScreen<?>) client.currentScreen).getScreenHandler() : null;
			if (abstractScreenHandler == null){
				final ClientPlayerEntity player = ArucasMinecraftExtension.getClient().player;
				if (player == null){
					throw new RuntimeError("Screen was null", function.syntaxPosition, context);
				}
				abstractScreenHandler = player.currentScreenHandler;
			}
			ArucasList valueList = abstractScreenHandler.slots.stream().sorted((slot1, slot2) -> {
				if (slot1.x == slot2.x && slot1.y == slot2.y) {
					return 0;
				}
				return slot1.y > slot2.y ? 1 : (slot1.x > slot2.x ? 1 : -1);
			}).map(a -> a.id).map(NumberValue::of).collect(Collectors.toCollection(ArucasList::new));
			return new ListValue(valueList);
		}
		private Value<?> getOrderedSlot(Context context, MemberFunction function) throws CodeError {
			final MinecraftClient client = ArucasMinecraftExtension.getClient();
			int numberValue1 = function.getParameterValueOfType(context, NumberValue.class, 1).value.intValue();
			ScreenHandler abstractScreenHandler = client.currentScreen != null ? ((HandledScreen<?>) client.currentScreen).getScreenHandler() : null;
			if (abstractScreenHandler == null){
				final ClientPlayerEntity player = ArucasMinecraftExtension.getClient().player;
				if (player == null){
					throw new RuntimeError("Screen was null", function.syntaxPosition, context);
				}
				abstractScreenHandler = player.currentScreenHandler;
			}
			List<Integer> valueList = abstractScreenHandler.slots.stream().sorted((slot1, slot2) -> {
				if (slot1.x == slot2.x && slot1.y == slot2.y) {
					return 0;
				}
				return slot1.y > slot2.y ? 1 : (slot1.x > slot2.x ? 1 : -1);
			}).map(a -> a.id).collect(Collectors.toList());
			return NumberValue.of(valueList.get(numberValue1));
		}
		@Override
		public Class<?> getValueClass() {
			return ScreenValue.class;
		}
	}
}
