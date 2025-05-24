package net.ultimporks.betterdiscs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.ultimporks.betterdiscs.BetterMusicDiscs;

import java.util.HashMap;
import java.util.Map;


@OnlyIn(Dist.CLIENT)
public class SpeakerSoundEvent {
    public static Minecraft minecraft = Minecraft.getInstance();

    private static final Map<BlockPos, SoundInfo> ACTIVE_SOUNDS_JUKEBOX = new HashMap<>();

    // Jukebox Sounds
    public static void playSound(ItemStack currentDisc, BlockPos speakerPos, float volume) {
        if (currentDisc.isEmpty()) return;
        Level level = minecraft.level;
        if (level == null) return;

        RegistryAccess registryAccess = level.registryAccess();

        JukeboxSong.fromStack(registryAccess, currentDisc).ifPresent(songHolder -> {
            JukeboxSong song = songHolder.value();

            SoundEvent soundEvent = song.soundEvent().get();

            Vec3 speakerPosVec = new Vec3(speakerPos.getX(), speakerPos.getY(), speakerPos.getZ());

            SoundInstance musicInstance = SimpleSoundInstance.forJukeboxSong(
                    soundEvent,
                    speakerPosVec
            );

            SoundInfo firstSoundInfo = new SoundInfo(
                    musicInstance,
                    soundEvent,
                    0,
                    song.lengthInTicks());

            minecraft.execute(() -> {
                minecraft.getSoundManager().play(musicInstance);
                ACTIVE_SOUNDS_JUKEBOX.put(speakerPos, firstSoundInfo);
                BetterMusicDiscs.speakerLOGGING("(SpeakerSoundEvent) - Music is starting! Volume: " + volume);
            });
        });
    }
    public static void stopSound(BlockPos speakerPos) {
        minecraft.execute(() -> {
            SoundInfo soundInfo = ACTIVE_SOUNDS_JUKEBOX.get(speakerPos);
            if (soundInfo != null) {
                minecraft.getSoundManager().stop(soundInfo.soundInstance);
                ACTIVE_SOUNDS_JUKEBOX.remove(speakerPos);
                BetterMusicDiscs.speakerLOGGING("(SpeakerSoundEvent) - Music is stopping!");
            }
        });
    }
    public static void stopAllSounds() {
        minecraft.execute(() -> {
            ACTIVE_SOUNDS_JUKEBOX.forEach((speakerPos, soundInstance) -> minecraft.execute(() -> {
                SoundInfo soundInfo = ACTIVE_SOUNDS_JUKEBOX.get(speakerPos);
                minecraft.getSoundManager().stop(soundInfo.soundInstance);
                ACTIVE_SOUNDS_JUKEBOX.remove(speakerPos);
                BetterMusicDiscs.speakerLOGGING("(SpeakerSoundEvent) - Music is stopping for all speakers.");
            }));
        });

    }

    // NoteBlock sounds
    public static void playNoteBlock(BlockPos speakerPos, String instrumentName, int note, float volume) {
        if (instrumentName == null) return;
        NoteBlockInstrument instrument = NoteBlockInstrument.valueOf(instrumentName.toUpperCase());
        SoundEvent soundEvent  = getSoundEventForInstrument(instrument);

        assert soundEvent != null;

        float pitch = (float) Math.pow(2.0, (note - 12) / 12.0);

        SoundInstance noteInstance = new SimpleSoundInstance(
                soundEvent,
                SoundSource.BLOCKS,
                volume,
                pitch,
                SoundInstance.createUnseededRandom(),
                speakerPos
        );
        minecraft.execute(() -> {
            minecraft.getSoundManager().play(noteInstance);
            BetterMusicDiscs.speakerLOGGING("(SpeakerSoundEvent) - Note is starting! Volume: " + volume);
        });
    }
    private static SoundEvent getSoundEventForInstrument(NoteBlockInstrument instrument) {
        return switch (instrument) {
            case HARP -> SoundEvents.NOTE_BLOCK_HARP.value();
            case BASEDRUM -> SoundEvents.NOTE_BLOCK_BASEDRUM.value();
            case SNARE -> SoundEvents.NOTE_BLOCK_SNARE.value();
            case HAT -> SoundEvents.NOTE_BLOCK_HAT.value();
            case BASS -> SoundEvents.NOTE_BLOCK_BASS.value();
            case FLUTE -> SoundEvents.NOTE_BLOCK_FLUTE.value();
            case BELL -> SoundEvents.NOTE_BLOCK_BELL.value();
            case GUITAR -> SoundEvents.NOTE_BLOCK_GUITAR.value();
            case CHIME -> SoundEvents.NOTE_BLOCK_CHIME.value();
            case XYLOPHONE -> SoundEvents.NOTE_BLOCK_XYLOPHONE.value();
            case IRON_XYLOPHONE -> SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE.value();
            case COW_BELL -> SoundEvents.NOTE_BLOCK_COW_BELL.value();
            case DIDGERIDOO -> SoundEvents.NOTE_BLOCK_DIDGERIDOO.value();
            case BIT -> SoundEvents.NOTE_BLOCK_BIT.value();
            case BANJO -> SoundEvents.NOTE_BLOCK_BANJO.value();
            case PLING -> SoundEvents.NOTE_BLOCK_PLING.value();
            default -> null;
        };
    }

}


