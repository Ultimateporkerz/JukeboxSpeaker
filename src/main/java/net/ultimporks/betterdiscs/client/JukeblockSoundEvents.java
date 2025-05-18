package net.ultimporks.betterdiscs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.ultimporks.betterdiscs.BetterMusicDiscs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class JukeblockSoundEvents {
    private static final Minecraft minecraft = Minecraft.getInstance();

    private static final Map<BlockPos, SoundInfo> ACTIVE_SOUNDS_JUKEBLOCK = new HashMap<>();
    private static final Map<BlockPos, SoundInfo> ACTIVE_SOUNDS_JUKEBLOCK_SPEAKERS = new HashMap<>();

    // Jukeblock Block

    public static void playJukeblock(BlockPos jukeBlockPos, ItemStack currentDisc, float volume) {
        Level level = minecraft.level;
        if (level == null || currentDisc.isEmpty()) return;

        RegistryAccess registryAccess = level.registryAccess();

        JukeboxSong.fromStack(registryAccess, currentDisc).ifPresent(songHolder -> {
            JukeboxSong song = songHolder.value();

            SoundEvent soundEvent = song.soundEvent().value();
            int durationTicks = song.lengthInTicks();

            SoundInstance soundInstance = new SimpleSoundInstance(
                    soundEvent,
                    SoundSource.RECORDS,
                    volume,
                    1.0F,
                    SoundInstance.createUnseededRandom(),
                    jukeBlockPos
            );

            SoundInfo firstSoundInfo = new SoundInfo(soundInstance, soundEvent, 0, durationTicks);

            minecraft.execute(() -> {
                minecraft.getSoundManager().play(soundInstance);
                ACTIVE_SOUNDS_JUKEBLOCK.put(jukeBlockPos, firstSoundInfo);
                BetterMusicDiscs.jukeblockLOGGING("(JukeblockSoundEvent) - Playing song: " + song.description().getString());
            });
        });
    }

    public static void stopJukeblockSound(BlockPos jukeblockPos) {
        minecraft.execute(() -> {
            SoundInfo soundInfo = ACTIVE_SOUNDS_JUKEBLOCK.get(jukeblockPos);
            if (soundInfo != null) {
                minecraft.getSoundManager().stop(soundInfo.soundInstance);
                ACTIVE_SOUNDS_JUKEBLOCK.remove(jukeblockPos);
                BetterMusicDiscs.jukeblockLOGGING("(JukeblockSoundEvent) - Jukeblock is stopping! " + jukeblockPos);
            }
        });
    }

    // Speakers

    public static void playJukeblockSpeakers(BlockPos speakerPos, ItemStack currentDisc, float volume) {
        Level level = minecraft.level;
        if (level == null || currentDisc.isEmpty()) return;

        RegistryAccess registryAccess = level.registryAccess();

        JukeboxSong.fromStack(registryAccess, currentDisc).ifPresent(songHolder -> {
            JukeboxSong song = songHolder.value();

            SoundEvent soundEvent = song.soundEvent().value();
            int durationTicks = song.lengthInTicks();

            SoundInstance soundInstance = new SimpleSoundInstance(
                    soundEvent,
                    SoundSource.RECORDS,
                    volume,
                    1.0F,
                    SoundInstance.createUnseededRandom(),
                    speakerPos
            );

            SoundInfo firstSoundInfo = new SoundInfo(soundInstance, soundEvent, 0, durationTicks);

            Minecraft.getInstance().execute(() -> {
                minecraft.getSoundManager().play(soundInstance);
                ACTIVE_SOUNDS_JUKEBLOCK_SPEAKERS.put(speakerPos, firstSoundInfo);
                BetterMusicDiscs.jukeblockLOGGING("(JukeblockSoundEvent) - Speaker is starting! Song: " + song.description().getString() + ", Volume: " + volume);
            });
        });
    }

    public static void stopSpeakerSound(BlockPos speakerPos) {
        minecraft.execute(() -> {
            SoundInfo soundInfo = ACTIVE_SOUNDS_JUKEBLOCK_SPEAKERS.get(speakerPos);
            if (soundInfo != null) {
                minecraft.getSoundManager().stop(soundInfo.soundInstance);
                ACTIVE_SOUNDS_JUKEBLOCK_SPEAKERS.remove(speakerPos);
                BetterMusicDiscs.jukeblockLOGGING("(JukeblockSoundEvent) - Jukeblock Speaker is stopping! " + speakerPos);
            }
        });
    }
    public static void stopAllSpeakerSounds() {
        List<BlockPos> speakerKeys = new ArrayList<>(ACTIVE_SOUNDS_JUKEBLOCK_SPEAKERS.keySet());
        for (BlockPos speakerPos : speakerKeys) {
            stopSpeakerSound(speakerPos);
        }
    }






}