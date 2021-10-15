package essentialclient.utils.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.render.ChatColour;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class CommandHelper {

    // I know this is bad way of doing it but don't want to refactor ClientRules, most likely will not add more commands, if do then will refactor
    public static Set<String> clientCommands = new HashSet<>();

    public static Set<String> functionCommand = new HashSet<>();

    public static DecimalFormat decimalFormat = new DecimalFormat("0.00");

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

    public boolean tryRunFunctionCommand(String message) {
        message = message.replace("/", "");
        List<String> arguments = Arrays.stream(message.split(" ")).toList();
        String command = arguments.remove(0);
        if (functionCommand.contains(command)) {
            return true;
        }
        return false;
    }
}
