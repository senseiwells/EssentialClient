package me.senseiwells.essentialclient.feature;

import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.gui.RulesScreen;
import me.senseiwells.essentialclient.mixins.gameRuleSync.RuleInvoker;
import me.senseiwells.essentialclient.rule.game.GameRule;
import me.senseiwells.essentialclient.rule.game.VanillaGameRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.interfaces.IGameRule;
import me.senseiwells.essentialclient.utils.misc.Scheduler;
import me.senseiwells.essentialclient.utils.network.NetworkHandler;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

import java.util.function.BiFunction;
import java.util.function.Function;

public class GameRuleNetworkHandler extends NetworkHandler {
	public static final int VERSION = 1_0_1;

	private boolean canModifyRules = false;

	private final BiFunction<String, Integer, CustomPayload> helloFactory;
	private final BiFunction<String, String, CustomPayload> setFactory;

	public GameRuleNetworkHandler() {
		if (EssentialUtils.isModInstalled("essentialaddons")) {
			this.helloFactory = essentialaddons.feature.GameRuleNetworkHandler.GameRuleHelloPayload::new;
			this.setFactory = essentialaddons.feature.GameRuleNetworkHandler.SetGameRulePayload::new;
		} else {
			this.helloFactory = GameRuleHelloPayload::new;
			this.setFactory = SetGameRulePayload::new;
		}
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
	public void onDisconnect() {
		VanillaGameRules.getGameRules().forEach(GameRule::reset);
		this.canModifyRules = false;
		super.onDisconnect();
	}

	public void processGamerules(NbtCompound compound) {
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
		this.sendPayload(() -> this.setFactory.apply(gameRule.getName(), newValue));
	}

	@Override
	public void registerCustomPayloads() {
		if (!EssentialUtils.isModInstalled("essentialaddons")) {
			PayloadTypeRegistry.playC2S().register(GameRuleHelloPayload.ID, GameRuleHelloPayload.CODEC);
			PayloadTypeRegistry.playC2S().register(SetGameRulePayload.ID, SetGameRulePayload.CODEC);

			PayloadTypeRegistry.playS2C().register(GameRuleHelloPayload.ID, GameRuleHelloPayload.CODEC);
			PayloadTypeRegistry.playS2C().register(GameRulePermissionsPayload.ID, GameRulePermissionsPayload.CODEC);
			PayloadTypeRegistry.playS2C().register(GameRulesChangedPayload.ID, GameRulesChangedPayload.CODEC);

			ClientPlayNetworking.registerGlobalReceiver(GameRuleHelloPayload.ID, (payload, context) -> {
				this.onHello(context.player().networkHandler, payload);
			});
			ClientPlayNetworking.registerGlobalReceiver(GameRulePermissionsPayload.ID, (payload, context) -> {
				this.canModifyRules = payload.canUpdateGamerules;
				this.refreshScreen();
			});
			ClientPlayNetworking.registerGlobalReceiver(GameRulesChangedPayload.ID, (payload, context) -> {
				this.processGamerules(payload.compound);
			});
		} else {
			Scheduler.schedule(0, () -> {
				ClientPlayNetworking.registerGlobalReceiver(essentialaddons.feature.GameRuleNetworkHandler.GameRuleHelloPayload.ID, (payload, context) -> {
					this.onHello(context.player().networkHandler, new GameRuleHelloPayload(payload.brand(), payload.version()));
				});
				ClientPlayNetworking.registerGlobalReceiver(essentialaddons.feature.GameRuleNetworkHandler.GameRulePermissionsPayload.ID, (payload, context) -> {
					this.canModifyRules = payload.canUpdateGamerules();
					this.refreshScreen();
				});
				ClientPlayNetworking.registerGlobalReceiver(essentialaddons.feature.GameRuleNetworkHandler.GameRulesChangedPayload.ID, (payload, context) -> {
					this.processGamerules(payload.compound());
				});
			});
		}
	}

	@Override
	protected CustomPayload createHelloPayload(String brand, int version) {
		return this.helloFactory.apply(brand, version);
	}

	private void refreshScreen() {
		if (EssentialUtils.getClient().currentScreen instanceof RulesScreen rulesScreen && rulesScreen.getTitle() == Texts.GAME_RULE_SCREEN) {
			EssentialUtils.getClient().execute(() -> rulesScreen.refreshRules(rulesScreen.getSearchBoxText()));
		}
	}

	private static class GameRuleHelloPayload extends HelloPayload {
		public static final Id<GameRuleHelloPayload> ID = new CustomPayload.Id<>(Identifier.of("essential:game_rule_hello"));
		public static final PacketCodec<PacketByteBuf, GameRuleHelloPayload> CODEC = PacketCodec.of(
			HelloPayload::write,
			GameRuleHelloPayload::new
		);

		public GameRuleHelloPayload(String brand, int version) {
			super(brand, version);
		}

		public GameRuleHelloPayload(PacketByteBuf buf) {
			super(buf);
		}

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}

	private record SetGameRulePayload(String name, String value) implements CustomPayload {
		public static final Id<SetGameRulePayload> ID = new CustomPayload.Id<>(Identifier.of("essential:set_game_rule"));
		public static final PacketCodec<PacketByteBuf, SetGameRulePayload> CODEC = PacketCodec.of(
			(payload, buf) -> buf.writeString(payload.name).writeString(payload.value),
			(buf) -> new SetGameRulePayload(buf.readString(), buf.readString())
		);

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}

	private record GameRulesChangedPayload(NbtCompound compound) implements CustomPayload {
		public static final Id<GameRulesChangedPayload> ID = new CustomPayload.Id<>(Identifier.of("essential:game_rules_changed"));
		public static final PacketCodec<PacketByteBuf, GameRulesChangedPayload> CODEC = PacketCodec.of(
			(payload, buf) -> buf.writeNbt(payload.compound),
			(buf) -> new GameRulesChangedPayload(buf.readNbt())
		);

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}

	private record GameRulePermissionsPayload(boolean canUpdateGamerules) implements CustomPayload {
		public static final Id<GameRulePermissionsPayload> ID = new CustomPayload.Id<>(Identifier.of("essential:game_rule_permissions"));
		public static final PacketCodec<PacketByteBuf, GameRulePermissionsPayload> CODEC = PacketCodec.of(
			(payload, buf) -> buf.writeBoolean(payload.canUpdateGamerules),
			(buf) -> new GameRulePermissionsPayload(buf.readBoolean())
		);

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}
}
