package essentialclient.feature.clientmacro;

import essentialclient.utils.inventory.InventoryUtils;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.screen.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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
                bool = this.checkEqualOperator() && this.checkLookingAtBlock();
                break;
            case "looking_at_entity":
                bool = this.checkEqualOperator() && this.checkLookingAtEntity();
                break;
            case "held_item":
                bool = this.checkEqualOperator() && this.checkHeldItem();
                break;
            case "health":
                bool = this.compareNumber((int) this.playerEntity.getHealth(), Integer.parseInt(this.currentActions[this.operatorLevel + 1]));
                break;
            case "is_trade_disabled":
                try {
                    bool = InventoryUtils.checkTradeDisabled(this.client, this.currentActions.length > 2 ? Integer.parseInt(this.currentActions[operatorLevel]) : 0);
                }
                catch (NumberFormatException nfe) {
                    bool = InventoryUtils.checkTradeDisabled(client, Registry.ITEM.get(new Identifier(this.currentActions[operatorLevel])));
                }
                break;
            case "villager_has_trade_for":
                bool = InventoryUtils.checkHasTrade(this.client, Registry.ITEM.get(new Identifier(this.currentActions[operatorLevel])));
                break;
            case "inventory_is_full":
                bool = this.playerEntity.inventory.getEmptySlot() != -1;
                break;
            case "in_inventory_gui":
                bool = this.checkInventoryScreen();
                break;
            default:
                return;
        }
        this.isTrue = this.inverted != bool;
        if (Arrays.asList(this.currentActions).contains("->")) {
            removeCurrentIf();
            String fullActions = ClientMacroHelper.concatStringArray(this.currentActions, ArrayUtils.indexOf(this.currentActions, "->") + 1);
            if (fullActions != null && this.isTrue)
                ClientMacro.action(fullActions, client);
        }
    }

    public static void tryElse(MinecraftClient client, String[] actions) {
        assert client.player != null;
        if (lastIf == null || lastIf.isElse || lastIf.isTrue) {
            if (!Arrays.asList(actions).contains("->"))
                ifs.add(new ClientMacroCondition(client, client.player, actions , 0, false));
            return;
        }
        switch (actions[1]) {
            case "if":
                new ClientMacroCondition(client, client.player, actions , 2, false).readAction();
                break;
            case "->":
                String fullActions = ClientMacroHelper.concatStringArray(actions, 2);
                if (fullActions != null)
                    ClientMacro.action(fullActions, client);
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

    private boolean checkHeldItem() {
        Item item = Registry.ITEM.get(new Identifier(this.currentActions[this.operatorLevel + 1]));
        return this.playerEntity.inventory.getMainHandStack().getItem() == item;
    }

    private boolean checkLookingAtBlock() {
        Block block = Registry.BLOCK.get(new Identifier(this.currentActions[this.operatorLevel + 1]));
        HitResult result = this.playerEntity.raycast(20D, 0.0F, true);
        if (result.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult) result).getBlockPos();
            return block == this.playerEntity.world.getBlockState(blockPos).getBlock();
        }
        return false;
    }

    private boolean checkLookingAtEntity() {
        EntityType<?> wantedEntityType = Registry.ENTITY_TYPE.get(new Identifier(this.currentActions[this.operatorLevel + 1]));
        return this.client.targetedEntity != null && this.client.targetedEntity.getType() == wantedEntityType;
    }

    private boolean checkInventoryScreen() {
        ScreenHandler screenHandler = this.playerEntity.currentScreenHandler;
        return  screenHandler instanceof GenericContainerScreenHandler ||
                screenHandler instanceof MerchantScreenHandler ||
                screenHandler instanceof HopperScreenHandler ||
                screenHandler instanceof FurnaceScreenHandler ||
                client.currentScreen instanceof InventoryScreen;
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

    protected static boolean checkForConditionals(String fullAction, String[] actions) {
        if (actions[0].equals("}")) {
            ClientMacroCondition.removeCurrentIf();
            return true;
        }
        if (ClientMacroCondition.getCurrentIf() != null && !ClientMacroCondition.getCurrentIf().isTrue && !ClientMacroCondition.getCurrentIf().isElse) {
            if (fullAction.contains("{"))
                ClientMacroCondition.ifs.add(ClientMacroCondition.getCurrentIf());
            return true;
        }
        return false;
    }
}
