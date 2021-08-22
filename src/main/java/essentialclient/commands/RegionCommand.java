package essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import essentialclient.gui.clientruleformat.BooleanClientRule;
import essentialclient.utils.command.CommandHelper;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.LiteralText;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public class RegionCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("region").requires((p) -> BooleanClientRule.clientBooleanRulesMap.get("commandRegion").value)
                .then(literal("get")
                        .then(argument("x", DoubleArgumentType.doubleArg())
                                .suggests((context, builder) -> CommandHelper.suggestLocation(context, builder, "x"))
                                .then(argument("z", DoubleArgumentType.doubleArg())
                                        .suggests((context, builder) -> CommandHelper.suggestLocation(context, builder, "z"))
                                        .executes(context -> {
                                            ClientPlayerEntity playerEntity = context.getSource().getPlayer();
                                            playerEntity.sendMessage(new LiteralText("§6Those coordinates are in region: §a" + (int) Math.floor(context.getArgument("x", Double.class)/512) + "." + (int) Math.floor(context.getArgument("z", Double.class)/512)), false);
                                            return 0;
                                        })
                                )
                        )
                        .executes(context -> {
                            ClientPlayerEntity playerEntity = context.getSource().getPlayer();
                            playerEntity.sendMessage(new LiteralText("§6You are in region: §a" + (int) Math.floor(playerEntity.getX()/512) + "." + (int) Math.floor(playerEntity.getZ()/512)), false);
                            return 0;
                        })
                )
        );
    }
}