package me.senseiwells.essentialclient.feature;

import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.gui.RulesScreen;
import me.senseiwells.essentialclient.mixins.gameRuleSync.RuleInvoker;
import me.senseiwells.essentialclient.rule.game.GameRule;
import me.senseiwells.essentialclient.rule.game.VanillaGameRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.interfaces.IGameRule;
import me.senseiwells.essentialclient.utils.network.NetworkHandler;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

public class GameRuleNetworkHandler extends NetworkHandler {
	public static final int VERSION = 1_0_0;

	private boolean canModifyRules = false;

	public GameRuleNetworkHandler() {
		super(new Identifier("essentialclient", "gamerule"));
	}

	public boolean canModifyRules() {
		return this.canModifyRules || EssentialUtils.getClient().isInSingleplayer();
	}

	@Override
	public int getVersion() {
		return VERSION;
	}

	@Override
	protected void onHelloSuccess() {
		EssentialClient.LOGGER.info("Game Rule data is available");
	}

	@Override
	protected void onHelloFail() {
		EssentialClient.LOGGER.info("Could not sync Game Rule data");
	}

	@Override
	public void processData(PacketByteBuf packetByteBuf, ClientPlayNetworkHandler networkHandler) {
		if (packetByteBuf.readableBytes() == 0) {
			return;
		}
		this.processRawData(packetByteBuf.readNbt());
	}

	@Override
	protected void customData(int varInt, PacketByteBuf packetByteBuf, ClientPlayNetworkHandler networkHandler) {
		if (varInt == 15) {
			this.canModifyRules = packetByteBuf.readBoolean();
			this.refreshScreen();
		}
	}

	@Override
	public void onDisconnect() {
		VanillaGameRules.getGameRules().forEach(GameRule::reset);
		this.canModifyRules = false;
		super.onDisconnect();
	}

	public void processRawData(NbtCompound compound) {
		if (compound == null) {
			EssentialClient.LOGGER.warn("Received bad Game Rule packet");
			return;
		}
		for (String ruleKey : compound.getKeys()) {
			String ruleValue = compound.getString(ruleKey);
			GameRule<?> gameRule = VanillaGameRules.ruleFromString(ruleKey);
			if (gameRule == null) {
				EssentialClient.LOGGER.warn("Game Rule '{}' could not be synced", ruleKey);
				continue;
			}
			gameRule.setFromServer(ruleValue);
		}
		this.refreshScreen();
	}

	public void sendServerRuleChange(GameRule<?> gameRule, String newValue) {
		MinecraftClient client = EssentialUtils.getClient();
		if (client.getServer() != null) {
			if (gameRule.getKey() != null) {
				GameRules.Rule<?> rule = client.getServer().getGameRules().get(gameRule.getKey());
				((RuleInvoker) rule).deserialize(newValue);
				((IGameRule) rule).essentialclient$ruleChanged(gameRule.getName(), client.getServer());
				gameRule.setFromServer(newValue);
				this.refreshScreen();
			}
			return;
		}
		this.sendDataPacket(buf -> buf.writeString(gameRule.getName()).writeString(newValue));
	}

	private void refreshScreen() {
		if (EssentialUtils.getClient().currentScreen instanceof RulesScreen rulesScreen && rulesScreen.getTitle() == Texts.GAME_RULE_SCREEN) {
			EssentialUtils.getClient().execute(() -> rulesScreen.refreshRules(rulesScreen.getSearchBoxText()));
		}
	}
}
