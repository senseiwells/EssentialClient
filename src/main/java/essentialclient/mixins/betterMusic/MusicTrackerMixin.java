package essentialclient.mixins.betterMusic;

import essentialclient.feature.MusicSounds;
import essentialclient.gui.clientrule.ClientRules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.MusicType;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(MusicTracker.class)
public class MusicTrackerMixin {
    @Final
    @Shadow
    private Random random;

    @Shadow
    private SoundInstance current;

    @Final
    @Shadow
    private MinecraftClient client;

    @Shadow
    private int timeUntilNextSong;

    @Shadow
    public void play(MusicSound type) { }

    /**
     * @author Sensei
     * @reason Completely rewriting tick method
     */
    @Overwrite
    public void tick() {
        String type = ClientRules.MUSIC_TYPES.getString();
        MusicSound musicSound = switch (type) {
            case "Overworld" -> MusicType.GAME;
            case "Nether" -> MusicSounds.getRandomNetherMusic();
            case "Overwrld + Nethr" -> MusicSounds.getRandomOverworldAndNetherMusic();
            case "End" -> MusicType.END;
            case "Creative" -> MusicType.CREATIVE;
            case "Menu" -> MusicType.MENU;
            case "Credits" -> MusicType.CREDITS;
            case "Any" -> MusicSounds.getRandomAllMusic();
            default -> this.client.getMusicType();
        };
        if (this.current != null) {
            if (!musicSound.getSound().getId().equals(this.current.getId()) && musicSound.shouldReplaceCurrentMusic()) {
                this.client.getSoundManager().stop(this.current);
                this.timeUntilNextSong = MathHelper.nextInt(this.random, 0, musicSound.getMinDelay() / 2);
            }

            if (!this.client.getSoundManager().isPlaying(this.current)) {
                this.current = null;
                this.timeUntilNextSong = Math.min(this.timeUntilNextSong, MathHelper.nextInt(this.random, musicSound.getMinDelay(), ClientRules.MUSIC_INTERVAL.getInt()));
            }
        }
        this.timeUntilNextSong = Math.min(this.timeUntilNextSong, ClientRules.MUSIC_INTERVAL.getInt());
        if (this.current == null && this.timeUntilNextSong-- <= 1) {
            this.play(musicSound);
        }
    }
}
