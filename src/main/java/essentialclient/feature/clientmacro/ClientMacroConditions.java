package essentialclient.feature.clientmacro;

import essentialclient.utils.EssentialUtils;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class ClientMacroConditions {

    private static boolean inverted = false;
    private static int actionLevel;
    private static int operatorLevel;
    private static String[] currentActions;
    public static boolean inIf = false;
    public static boolean isIfTrue = false;

    public static void conditional(MinecraftClient client, String[] actions, String fullAction) {
        inIf = true;
        ClientPlayerEntity player = client.player;
        assert player != null;
        currentActions = actions;
        actionLevel = 1;
        try {
            if (actions[actionLevel].equals("not")) {
                actionLevel++;
                inverted = true;
            }
            checkConditions(client, player);
        }
        catch (Exception e) {
            EssentialUtils.sendMessage("§cYou have an invalid macro file!");
            EssentialUtils.sendMessage("§cPlease check that you have the macro configured properly");
            EssentialUtils.sendMessage("§cThe line: " + fullAction + " most likely caused the issue");
            ClientMacroHelper.stopMacro(client);
            ClientMacro.enabled = false;
        }
        finally {
            inverted = false;
        }
    }

    private static void checkConditions(MinecraftClient client, ClientPlayerEntity playerEntity) {
        operatorLevel = actionLevel + 1;
        switch (currentActions[actionLevel]) {
            case "looking_at_block":
                isIfTrue = checkEqualOperator() && (inverted != checkLookingAtBlock(playerEntity));
                break;
            case "health":
                isIfTrue = compareNumber((int) playerEntity.getHealth(), Integer.parseInt(currentActions[operatorLevel + 1]));
                break;
            case "inventory_is_full":
                isIfTrue = inverted == (playerEntity.inventory.getEmptySlot() != -1);
                break;
        }
    }

    private static boolean checkLookingAtBlock(ClientPlayerEntity playerEntity) {
        Block block = Registry.BLOCK.get(new Identifier(currentActions[operatorLevel + 1]));
        HitResult result = playerEntity.raycast(20D, 0.0F, true);
        if (result.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult)result).getBlockPos();
            return block == playerEntity.world.getBlockState(blockPos).getBlock();
        }
        return false;
    }

    private static boolean compareNumber(int x, int y) {
        switch (currentActions[operatorLevel]) {
            case "==":
                return inverted != (x == y);
            case "<":
                return inverted != (x < y);
            case "<=":
                return inverted != (x <= y);
            case ">":
                return inverted != (x > y);
            case ">=":
                return inverted != (x >= y);
        }
        return false;
    }

    private static boolean checkEqualOperator() {
        return currentActions[operatorLevel].equals("==");
    }
}
