package essentialclient.utils.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.command.CommandSource;

import java.text.DecimalFormat;
import java.util.concurrent.CompletableFuture;

public class CommandHelper {
    public static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public static CompletableFuture<Suggestions> suggestLocation(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder, String type) {
        switch (type) {
            case "x":
                return CommandSource.suggestMatching(new String[]{String.valueOf(decimalFormat.format(context.getSource().getPlayer().getX()))}, builder);
            case "y":
                return CommandSource.suggestMatching(new String[]{String.valueOf(decimalFormat.format(context.getSource().getPlayer().getY()))}, builder);
            case "z":
                return CommandSource.suggestMatching(new String[]{String.valueOf(decimalFormat.format(context.getSource().getPlayer().getZ()))}, builder);
            case "yaw":
                return CommandSource.suggestMatching(new String[]{String.valueOf(decimalFormat.format(context.getSource().getPlayer().yaw))}, builder);
            case "pitch":
                return CommandSource.suggestMatching(new String[]{String.valueOf(decimalFormat.format(context.getSource().getPlayer().pitch))}, builder);
            case "dimension":
                return CommandSource.suggestMatching(new String[]{"minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"}, builder);
        }
        return null;
    }

    public static LiteralArgumentBuilder<FabricClientCommandSource> setLiteral(LiteralArgumentBuilder<FabricClientCommandSource> builder, String literal) {
        return ClientCommandManager.literal(literal).requires(builder.getRequirement()).forward(builder.getRedirect(), builder.getRedirectModifier(), builder.isFork()).executes(builder.getCommand());
    }
}
