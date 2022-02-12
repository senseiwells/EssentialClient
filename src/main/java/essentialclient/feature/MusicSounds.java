package essentialclient.feature;

import net.minecraft.client.sound.MusicType;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvents;

import java.util.Random;

public class MusicSounds {

	private static final MusicSound[] NETHER_MUSICTYPE = {
		MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_BASALT_DELTAS),
		MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_NETHER_WASTES),
		MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_CRIMSON_FOREST),
		MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_WARPED_FOREST),
		MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_SOUL_SAND_VALLEY)
	};

	private static final MusicSound[] OVERWORLD_AND_NETHERTYPE = {
		NETHER_MUSICTYPE[new Random().nextInt(NETHER_MUSICTYPE.length)],
		MusicType.GAME
	};

	private static final MusicSound[] ALL_TYPE = {
		MusicType.GAME,
		MusicType.GAME,
		MusicType.GAME,
		MusicType.CREATIVE,
		MusicType.CREATIVE,
		MusicType.UNDERWATER,
		NETHER_MUSICTYPE[new Random().nextInt(NETHER_MUSICTYPE.length)],
		new MusicSound(SoundEvents.MUSIC_END, 6000, 24000, false),
		new MusicSound(SoundEvents.MUSIC_CREDITS, 0, 0, false),
		new MusicSound(SoundEvents.MUSIC_MENU, 20, 600, false)
	};

	public static MusicSound getRandomNetherMusic() {
		return NETHER_MUSICTYPE[new Random().nextInt(NETHER_MUSICTYPE.length)];
	}

	public static MusicSound getRandomOverworldAndNetherMusic() {
		return OVERWORLD_AND_NETHERTYPE[new Random().nextInt(OVERWORLD_AND_NETHERTYPE.length)];
	}

	public static MusicSound getRandomAllMusic() {
		return ALL_TYPE[new Random().nextInt(ALL_TYPE.length)];
	}
}
