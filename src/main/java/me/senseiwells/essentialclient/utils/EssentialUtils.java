package me.senseiwells.essentialclient.utils;

import me.senseiwells.essentialclient.feature.CarpetClient;
import me.senseiwells.essentialclient.rule.carpet.CarpetClientRule;
import me.senseiwells.essentialclient.rule.carpet.IntegerCarpetRule;
import me.senseiwells.essentialclient.rule.carpet.StringCarpetRule;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.MinecraftVersion;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class EssentialUtils {
	private static final Path ESSENTIAL_CLIENT_PATH;
	private static final ModContainer ESSENTIAL_CONTAINER;
	private static final boolean DEV;

	public static final URL WIKI_URL;
	public static final URL SCRIPT_WIKI_URL;

	static {
		ESSENTIAL_CLIENT_PATH = FabricLoader.getInstance().getConfigDir().resolve("EssentialClient");
		DEV = FabricLoader.getInstance().isDevelopmentEnvironment();

		Optional<ModContainer> optional = FabricLoader.getInstance().getModContainer("essential-client");
		if (optional.isEmpty()) {
			throw new IllegalStateException("EssentialClient mod container was not found?!");
		}
		ESSENTIAL_CONTAINER = optional.get();

		try {
			WIKI_URL = new URL("https://github.com/senseiwells/EssentialClient/wiki");
			SCRIPT_WIKI_URL = new URL("https://github.com/senseiwells/EssentialClient/wiki/ClientScript");
			Files.createDirectories(ESSENTIAL_CLIENT_PATH);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void sendMessageToActionBar(String message) {
		MinecraftClient client = getClient();
		if (client.player != null) {
			client.execute(() -> client.player.sendMessage(Texts.literal(message), true));
		}
	}

	public static void sendMessage(String message) {
		MinecraftClient client = getClient();
		if (client.player != null) {
			client.execute(() -> client.player.sendMessage(Texts.literal(message), false));
		}
	}

	public static void sendMessage(Text text) {
		MinecraftClient client = getClient();
		if (client.player != null) {
			client.execute(() -> client.player.sendMessage(text, false));
		}
	}

	public static void sendChatMessage(String message) {
		MinecraftClient client = getClient();
		if (client.player != null) {
			client.execute(() -> client.player.sendChatMessage(message));
		}
	}

	public static Path getEssentialConfigFile() {
		return ESSENTIAL_CLIENT_PATH;
	}

	public static MinecraftClient getClient() {
		return MinecraftClient.getInstance();
	}

	public static ClientPlayerEntity getPlayer() {
		return getClient().player;
	}

	public static ClientWorld getWorld() {
		return getClient().world;
	}

	public static ClientPlayerInteractionManager getInteractionManager() {
		return getClient().interactionManager;
	}

	public static ClientPlayNetworkHandler getNetworkHandler() {
		return getClient().getNetworkHandler();
	}

	public static PlayerListEntry getPlayerListEntry() {
		return EssentialUtils.getNetworkHandler().getPlayerListEntry(EssentialUtils.getPlayer().getUuid());
	}

	public static boolean playerHasOp() {
		ClientPlayerEntity player = getPlayer();
		return player != null && player.hasPermissionLevel(2);
	}

	public static String getMinecraftVersion() {
		return MinecraftVersion.CURRENT.getName();
	}

	public static boolean isDev() {
		return DEV;
	}

	public static boolean isModInstalled(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	public static String getEssentialVersion() {
		return ESSENTIAL_CONTAINER.getMetadata().getVersion().getFriendlyString();
	}

	public static boolean canMineBlock(ClientPlayerEntity player, BlockPos pos) {
		double x = player.getX() - pos.getX() - 0.5;
		double y = player.getY() - pos.getY() + 1.0;
		double z = player.getZ() - pos.getZ() - 0.5;
		if (x * x + y * y + z * z > 36 || player.world.isOutOfHeightLimit(pos) || !player.world.getWorldBorder().contains(pos)) {
			return false;
		}
		if (player.isBlockBreakingRestricted(player.world, pos, getInteractionManager().getCurrentGameMode())) {
			return false;
		}
		BlockState state = player.world.getBlockState(pos);
		return !state.isAir() && !state.contains(FluidBlock.LEVEL) && state.getHardness(null, null) >= 0;
	}

	public static float getBlockBreakingSpeed(ItemStack itemStack, BlockState blockState, PlayerEntity player) {
		float multiplier = itemStack.getMiningSpeedMultiplier(blockState);
		if (multiplier > 1.0F) {
			int efficiencyLevel = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, itemStack);
			if (efficiencyLevel > 0) {
				multiplier += efficiencyLevel * efficiencyLevel + 1.0F;
			}
		}

		if (StatusEffectUtil.hasHaste(player)) {
			multiplier *= 1.0F + (StatusEffectUtil.getHasteAmplifier(player) + 1.0F) * 0.2F;
		}
		StatusEffectInstance effectInstance = player.getStatusEffect(StatusEffects.MINING_FATIGUE);
		if (effectInstance != null) {
			multiplier *= switch (effectInstance.getAmplifier()) {
				case 0 -> 0.3F;
				case 1 -> 0.09F;
				case 2 -> 0.027F;
				default -> 8.1e-4F;
			};
		}
		if (player.isSubmergedIn(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(player)) {
			multiplier /= 5.0F;
		}
		if (!player.isOnGround()) {
			multiplier /= 5.0F;
		}
		return multiplier;
	}

	public static int getMaxChatLength(int fallback) {
		if (CarpetClient.INSTANCE.isServerCarpet()) {
			CarpetClientRule<?> rule = CarpetClient.INSTANCE.getRule("maxChatLength");
			if (rule instanceof IntegerCarpetRule intRule) {
				int maxLength = intRule.getValue();
				if (maxLength > 0) {
					return maxLength;
				}
			}
			if (rule instanceof StringCarpetRule stringRule) {
				Integer maxLength = catchAsNull(() -> Integer.parseInt(stringRule.getValue()));
				if (maxLength != null && maxLength >= 0) {
					return maxLength;
				}
			}
		}
		return fallback;
	}

	public static <T> T throwAsRuntime(ThrowableSupplier<T> supplier) {
		try {
			return supplier.get();
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static <T> T catchAsNull(ThrowableSupplier<T> throwableSupplier) {
		try {
			return throwableSupplier.get();
		}
		catch (Throwable throwable) {
			return null;
		}
	}

	@FunctionalInterface
	public interface ThrowableRunnable {
		void run() throws Throwable;
	}

	@FunctionalInterface
	public interface ThrowableSupplier<T> {
		T get() throws Throwable;
	}
}
