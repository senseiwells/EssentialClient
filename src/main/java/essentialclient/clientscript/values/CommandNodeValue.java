package essentialclient.clientscript.values;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

public class CommandNodeValue extends Value<CommandNode<ServerCommandSource>> {
	public CommandNodeValue(CommandNode<ServerCommandSource> value) {
		super(value);
	}

	@Override
	public CommandNodeValue copy(Context context) throws CodeError {
		return this;
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return "<Command - " + this.value.getName() + ">";
	}

	@Override
	public int getHashCode(Context context) throws CodeError {
		return this.value.hashCode();
	}

	@Override
	public boolean isEquals(Context context, Value<?> other) throws CodeError {
		return this == other;
	}

	public static class CommandNodeClass extends ArucasClassExtension {
		public CommandNodeClass() {
			super("CommandNode");
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				new BuiltInFunction("literal", List.of("name", "function"), this::literal),
				new BuiltInFunction("argument", List.of("name", "type", "suggests", "function"), this::argument)
			);
		}

		private Value<?> literal(Context context, BuiltInFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 1);
			LiteralCommandNode<ServerCommandSource> literalCommandNode = CommandManager.literal(stringValue.value).executes(c -> {
				c.getArgument()
				context.getThreadHandler().runAsyncFunctionInContext(context, ctx -> {
					functionValue.call(ctx, List.of());
				});
				return 1;
			}).build();
			return new CommandNodeValue();
		}

		private Value<?> argument(Context context, BuiltInFunction function) throws CodeError {

		}

		@Override
		public Class<?> getValueClass() {
			return CommandNodeValue.class;
		}
	}
}
