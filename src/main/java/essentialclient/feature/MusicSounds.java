package essentialclient.feature;

import com.mojang.datafixers.kinds.IdF;
import net.minecraft.client.sound.MusicType;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvents;

import java.util.Random;

public class MusicSounds {

    private static final MusicSound[] NETHERMUSICTYPE = new MusicSound[] {
            MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_BASALT_DELTAS),
            MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_NETHER_WASTES),
            MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_CRIMSON_FOREST),
            MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_WARPED_FOREST),
            MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_SOUL_SAND_VALLEY)
    };

    private static final MusicSound[] OVERWORLDANDNETHERTYPE = new MusicSound[] {
            NETHERMUSICTYPE[new Random().nextInt(NETHERMUSICTYPE.length)],
            MusicType.GAME
    };

    private static final MusicSound[] ALLTYPE = new MusicSound[] {
            MusicType.GAME,
            MusicType.GAME,
            MusicType.GAME,
            MusicType.CREATIVE,
            MusicType.CREATIVE,
            MusicType.UNDERWATER,
            NETHERMUSICTYPE[new Random().nextInt(NETHERMUSICTYPE.length)],
            new MusicSound(SoundEvents.MUSIC_END, 6000, 24000, false),
            new MusicSound(SoundEvents.MUSIC_CREDITS, 0, 0, false),
            new MusicSound(SoundEvents.MUSIC_MENU, 20, 600, false)
    };

    public static MusicSound getRandomNetherMusic() {
        return NETHERMUSICTYPE[new Random().nextInt(NETHERMUSICTYPE.length)];
    }

    public static MusicSound getRandomOverworldAndNetherMusic() {
        return OVERWORLDANDNETHERTYPE[new Random().nextInt(OVERWORLDANDNETHERTYPE.length)];
    }

    public static MusicSound getRandomAllMusic() {
        return ALLTYPE[new Random().nextInt(ALLTYPE.length)];
    }
}
