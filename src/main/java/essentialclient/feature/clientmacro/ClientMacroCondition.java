package essentialclient.feature.clientmacro;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

//Good luck reading this - Sensei
public class ClientMacroCondition {

    private final MinecraftClient client;
    private boolean inverted;
    private int actionLevel;
    private int operatorLevel;
    private final String[] currentActions;
    public boolean isTrue;
    public final boolean isElse;
    private final ClientPlayerEntity playerEntity;

    public static List<ClientMacroCondition> ifs = new LinkedList<>();
    public static ClientMacroCondition lastIf;

    public ClientMacroCondition(MinecraftClient client, @NotNull ClientPlayerEntity playerEntity, String[] actions, int actionLvl, boolean isElse) {
        this.client = client;
        this.inverted = false;
        this.actionLevel = actionLvl;
        this.currentActions = actions;
        this.isTrue = false;
        this.playerEntity = playerEntity;
        this.isElse = isElse;
    }

    public void readAction() {
        this.checkInvert();
        this.addToList();
        this.operatorLevel = this.actionLevel + 1;
        boolean bool;
        switch (this.currentActions[this.actionLevel]) {
            case "looking_at_block":
                bool = this.checkEqualOperator() && this.checkLookingAtBlock(this.playerEntity);
                break;
            case "held_item":
                bool = this.checkEqualOperator() && this.checkHeldItem(this.playerEntity);
                break;
            case "health":
                bool = this.compareNumber((int) this.playerEntity.getHealth(), Integer.parseInt(this.currentActions[this.operatorLevel + 1]));
                break;
            case "inventory_is_full":
                bool = this.playerEntity.inventory.getEmptySlot() != -1;
                break;
            default:
                return;
        }
        this.isTrue = this.inverted != bool;
    }

    public static void tryElse(MinecraftClient client, String[] actions) {
        assert client.player != null;
        if (lastIf == null || lastIf.isElse || lastIf.isTrue) {
            ifs.add(null);
            return;
        }
        switch (actions[1]) {
            case "if":
                new ClientMacroCondition(client, client.player, actions , 2, false).readAction();
                break;
            case "{":
                new ClientMacroCondition(client, client.player, actions , 0, true).addToList();
                break;
        }
    }

    public void checkInvert() {
        if (this.currentActions[this.actionLevel].equals("not")) {
            this.actionLevel++;
            this.inverted = true;
        }
    }

    private void addToList() {
        ifs.add(this);
    }

    public static ClientMacroCondition getCurrentIf() {
        if (ifs.isEmpty())
            return null;
        return ifs.get(ifs.size() - 1);
    }

    public static void removeCurrentIf() {
        if (ifs.isEmpty())
            return;
        lastIf = ifs.remove(ifs.size() - 1);
    }

    //This should only be called when stopping macro
    protected static void removeAllIf() {
        ifs.clear();
    }

    private boolean checkHeldItem(ClientPlayerEntity playerEntity) {
        Item item = Registry.ITEM.get(new Identifier(this.currentActions[this.operatorLevel + 1]));
        return playerEntity.inventory.getMainHandStack().getItem() == item;
    }

    private boolean checkLookingAtBlock(ClientPlayerEntity playerEntity) {
        Block block = Registry.BLOCK.get(new Identifier(this.currentActions[this.operatorLevel + 1]));
        HitResult result = playerEntity.raycast(20D, 0.0F, true);
        if (result.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult)result).getBlockPos();
            return block == playerEntity.world.getBlockState(blockPos).getBlock();
        }
        return false;
    }

    private boolean compareNumber(int x, int y) {
        boolean bool;
        switch (this.currentActions[this.operatorLevel]) {
            case "==":
                bool = (x == y);
                break;
            case "<":
                bool = (x < y);
                break;
            case "<=":
                bool = (x <= y);
                break;
            case ">":
                bool = (x > y);
                break;
            case ">=":
                bool = (x >= y);
                break;
            default:
                return false;
        }
        return this.inverted != bool;
    }

    private boolean checkEqualOperator() {
        return this.currentActions[this.operatorLevel].equals("==");
    }
}
