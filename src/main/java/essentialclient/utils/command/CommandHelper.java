package essentialclient.utils.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import essentialclient.feature.clientscript.MinecraftEventFunction;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.render.ChatColour;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CommandHelper {

    public static final Set<String> clientCommands = new HashSet<>();
    public static final Set<String> functionCommand = new HashSet<>();
    public static final Set<LiteralCommandNode<ServerCommandSource>> functionCommands = new HashSet<>();
    public static final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    public static boolean needUpdate = false;

    public static CompletableFuture<Suggestions> suggestLocation(SuggestionsBuilder builder, String type) {
        return switch (type) {
            case "x" -> CommandSource.suggestMatching(new String[]{String.valueOf(decimalFormat.format(getPlayer().getX()))}, builder);
            case "y" -> CommandSource.suggestMatching(new String[]{String.valueOf(decimalFormat.format(getPlayer().getY()))}, builder);
            case "z" -> CommandSource.suggestMatching(new String[]{String.valueOf(decimalFormat.format(getPlayer().getZ()))}, builder);
            case "yaw" -> CommandSource.suggestMatching(new String[]{String.valueOf(decimalFormat.format(getPlayer().yaw))}, builder);
            case "pitch" -> CommandSource.suggestMatching(new String[]{String.valueOf(decimalFormat.format(getPlayer().pitch))}, builder);
            case "dimension" -> CommandSource.suggestMatching(new String[]{"minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"}, builder);
            default -> null;
        };
    }

    public static boolean isClientCommand(String command) {
        return clientCommands.contains(command);
    }

    public static void clearCommands() {
        clientCommands.clear();
    }

    public static ClientPlayerEntity getPlayer() {
        return MinecraftClient.getInstance().player;
    }

    public static void executeCommand(StringReader reader, String command) {
        ClientPlayerEntity player = getPlayer();
        try {
            player.networkHandler.getCommandDispatcher().execute(reader, new FakeCommandSource(player));
        } catch (CommandException e) {
            EssentialUtils.sendMessage(ChatColour.RED + e.getTextMessage());
        } catch (CommandSyntaxException e) {
            EssentialUtils.sendMessage(ChatColour.RED + e.getMessage());
            if (e.getInput() != null && e.getCursor() >= 0) {
                int cursor = Math.min(e.getCursor(), e.getInput().length());
                MutableText text = new LiteralText("").formatted(Formatting.GRAY)
                        .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)));
                if (cursor > 10)
                    text.append("...");

                text.append(e.getInput().substring(Math.max(0, cursor - 10), cursor));
                if (cursor < e.getInput().length()) {
                    text.append(new LiteralText(e.getInput().substring(cursor)).formatted(Formatting.RED, Formatting.UNDERLINE));
                }

                text.append(new TranslatableText("command.context.here").formatted(Formatting.RED, Formatting.ITALIC));
                getPlayer().sendMessage(text, false);
            }
        } catch (Exception e) {
            LiteralText error = new LiteralText(e.getMessage() == null ? e.getClass().getName() : e.getMessage());
            getPlayer().sendMessage(new TranslatableText("command.failed")
                    .styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, error))), false);
            e.printStackTrace();
        }
    }

    public static boolean tryRunFunctionCommand(String message) {
        message = message.replace("/", "");
        List<Value<?>> arguments = new ArrayList<>();
        for (String argument : message.split(" "))
            arguments.add(new StringValue(argument));
        StringValue command = (StringValue) arguments.remove(0);
        if (functionCommand.contains(command.value)) {
            List<Value<?>> parameters = List.of(command, new ListValue(arguments));
            MinecraftEventFunction.ON_COMMAND.tryRunFunction(parameters);
            return true;
        }
        return false;
    }
}
