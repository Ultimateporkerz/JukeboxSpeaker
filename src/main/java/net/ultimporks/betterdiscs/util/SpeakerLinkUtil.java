package net.ultimporks.betterdiscs.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.ultimporks.betterdiscs.BetterMusicDiscs;
import net.ultimporks.betterdiscs.block.entity.SpeakerBlockEntity;
import net.ultimporks.betterdiscs.data.SpeakerLinkData;
import net.ultimporks.betterdiscs.init.ModBlocks;
import net.ultimporks.betterdiscs.init.ModMessages;
import net.ultimporks.betterdiscs.network.S2C.*;

import java.util.*;

public class SpeakerLinkUtil {
    private static final Map<ServerLevel, SpeakerLinkData> DATA_STORE = new HashMap<>();


    // JUKEBLOCK SIDE

    // Activates Jukeblock
    /*
    public static void activateJukeblock(ServerLevel level, BlockPos jukeblockPos, ItemStack currentDisc) {
        if (level.getExistingBlockEntity(jukeblockPos) instanceof JukeblockBlockEntity jukeblockBlockEntity && !level.isClientSide) {
            if (!currentDisc.isEmpty()) {
                int volume = jukeblockBlockEntity.getVolume();
                float scaledVolume = volume / 100.0F;
                S2CSyncJukeblockPlayMessage jukeblockPlayMessage = new S2CSyncJukeblockPlayMessage(jukeblockPos, currentDisc, scaledVolume);
                ModMessages.sendToAllPlayers(jukeblockPlayMessage);
            }
        }
    }
    // Deactivates Jukeblock
    public static void deactivateJukeblock(ServerLevel level, BlockPos jukeblockPos) {
        if (level.getExistingBlockEntity(jukeblockPos) instanceof JukeblockBlockEntity jukeblockBlockEntity) {
            jukeblockBlockEntity.setStopped();
            S2CSyncJukeblockStopMessage jukeblockStopMessage = new S2CSyncJukeblockStopMessage(jukeblockPos);
            ModMessages.sendToAllPlayers(jukeblockStopMessage);
        }
    }
    // Activates all speakers linked to Master's Block Pos
    public static void activateSpeakersJukeblock(ServerLevel level, BlockPos masterBlockPos, ItemStack currentDisc) {
        getLinkedSpeakersJukeblock(level, masterBlockPos).forEach(speakerPos -> {
            if (level.getExistingBlockEntity(speakerPos) instanceof SpeakerBlockEntity speakerBlock) {
                BlockEntity jukeblockEntity = level.getExistingBlockEntity(masterBlockPos);
                if (jukeblockEntity instanceof JukeblockBlockEntity) {
                    if (!currentDisc.isEmpty()) {
                        speakerBlock.setActive(true, currentDisc);
                        int volume = speakerBlock.getVolume();
                        float scaledVolume = volume / 100.0F;
                        S2CSyncJukeblockPlayMessage speakerPlayMessage = new S2CSyncJukeblockPlayMessage(speakerPos, currentDisc, scaledVolume, true);
                        ModMessages.sendToAllPlayers(speakerPlayMessage);
                    }
                }
            }
        });
    }
    // Deactivates ALL speakers linked to Master's Block Pos
    public static void deactivateSpeakersJukeblock(ServerLevel level, BlockPos masterBlockPos) {
        getLinkedSpeakersJukeblock(level, masterBlockPos).forEach(speakerPos -> {
            if (level.getExistingBlockEntity(speakerPos) instanceof SpeakerBlockEntity speakerBlock) {
                speakerBlock.setActive(false, ItemStack.EMPTY);
                S2CSyncJukeblockStopMessage speakerStopMessage = new S2CSyncJukeblockStopMessage(speakerPos, true);
                ModMessages.sendToAllPlayers(speakerStopMessage);
            }
        });
    }
    // Deactivates a SINGLE speaker linked to Master's Block Pos
    public static void deactivateSpeakerJukeblock(ServerLevel level, BlockPos speakerPos) {
        if (level.getBlockEntity(speakerPos) instanceof SpeakerBlockEntity speakerBlock) {
            speakerBlock.setActive(false, ItemStack.EMPTY);
            S2CSyncJukeblockStopMessage speakerStopMessage = new S2CSyncJukeblockStopMessage(speakerPos, false);
            ModMessages.sendToAllPlayers(speakerStopMessage);
        }
    }
    // Link speaker to jukeblock
    public static boolean linkSpeakerJukeblock(ServerLevel level, BlockPos masterBlockPos, BlockPos speakerPos) {
        SpeakerLinkData data = getData(level);
        if (data == null) return false;

        if (isValidPosition(level, masterBlockPos) && isValidPosition(level, speakerPos)) {
            data.addJukeblockLink(masterBlockPos, speakerPos);
            return true;
        }
        return false;
    }
    // Unlink speaker from jukeblock
    public static boolean unlinkSpeakerJukeblock(ServerLevel level, BlockPos speakerPos) {
        SpeakerLinkData data = getData(level);
        if (data == null) return false;

        deactivateSpeakerJukeblock(level, speakerPos);
        BlockEntity jukeblock = getLinkedJukeblock(level, speakerPos);
        if (jukeblock == null) {
            BetterMusicDiscs.speakerLOGGING("(SpeakerLinkUtil) - Tried to unlink speaker; but jukebox is NULL");

            data.getLinkedSpeakersJukeblock().forEach((masterPos, speakers) -> {
                if (speakers.remove(speakerPos)) {
                    BetterMusicDiscs.speakerLOGGING("(SpeakerLinkUtil) - Removed speakerPos " + speakerPos + " from masterPos " + masterPos);
                }
            });

            BetterMusicDiscs.speakerLOGGING("(SpeakerLinkUtil) - Cleaning up links, removing empty ones");
            data.getLinkedSpeakersJukeblock().entrySet().removeIf(entry -> entry.getValue().isEmpty());
            return true;
        }
        BlockPos jukeblockBlockPos = jukeblock.getBlockPos();
        data.removeJukeblockLink(jukeblockBlockPos, speakerPos);
        return true;
    }
    // Unlink ALL speakers from jukeblock
    public static boolean unlinkAllSpeakersJukeblock(ServerLevel level, BlockPos jukeblockPos) {
        SpeakerLinkData data = getData(level);
        if (data == null) return false;

        deactivateJukeblock(level, jukeblockPos);
        deactivateSpeakersJukeblock(level, jukeblockPos);
        data.removeAllJukeblockLinks(jukeblockPos);
        return true;
    }



    // JUKEBLOCK HELPER METHODS

    // Gets all the linked speakers from a jukeblock
    public static List<BlockPos> getLinkedSpeakersJukeblock(ServerLevel level, BlockPos jukeblockPos) {
        SpeakerLinkData data = getData(level);
        return data != null ?
                new ArrayList<>(data.getLinkedSpeakersJukeblock().getOrDefault(jukeblockPos, Collections.emptySet())) :
                Collections.emptyList();
    }
    // Returns the jukeblock block entity from a speakerPos
    public static BlockEntity getLinkedJukeblock(ServerLevel level, BlockPos speakerPos) {
        SpeakerLinkData data = getData(level);
        // Make sure Data is there
        if (data == null) {
            BetterMusicDiscs.jukeblockLOGGING("(SpeakerLinkUtil) - Data is NULL - returning NULL");
            return null;
        }

        // Iterate through speaker links to find "master" jukebox
        for (Map.Entry<BlockPos, Set<BlockPos>> entry : data.getLinkedSpeakersJukeblock().entrySet()) {
            if (entry.getValue().contains(speakerPos)) {
                BlockEntity blockEntity = level.getBlockEntity(entry.getKey());
                if (blockEntity instanceof JukeblockBlockEntity jukeblockBlockEntity) {
                    return jukeblockBlockEntity;
                }
            }
        }
        return null;
    }
    */

    // JUKEBOX SIDE

    // Activates all speakers linked to Master's Block Pos
    public static void activateSpeakersJukebox(ServerLevel level, BlockPos masterBlockPos, ItemStack currentDisc) {
        getLinkedSpeakersJukebox(level, masterBlockPos).forEach(speakerPos -> {
            if (level.getExistingBlockEntity(speakerPos) instanceof SpeakerBlockEntity speakerBlock) {
                if (!currentDisc.isEmpty()) {
                    speakerBlock.setActive(true, currentDisc);
                    int volume = speakerBlock.getVolume();
                    float scaledVolume = volume / 100.0F;
                    S2CSyncJukeboxSpeakersMessage message = new S2CSyncJukeboxSpeakersMessage(speakerPos, currentDisc, scaledVolume);
                    ModMessages.sendToAllPlayers(message);
                }
            }
        });
    }
    // Deactivates ALL speakers linked to Master's Block Pos
    public static void deactivateSpeakersJukebox(ServerLevel level, BlockPos masterBlockPos) {
        getLinkedSpeakersJukebox(level, masterBlockPos).forEach(speakerPos -> {
            if (level.getExistingBlockEntity(speakerPos) instanceof SpeakerBlockEntity speakerBlock) {
                speakerBlock.setActive(false, ItemStack.EMPTY);
                S2CSyncJukeboxOrNoteblockStopMessage message = new S2CSyncJukeboxOrNoteblockStopMessage(speakerPos, true);
                ModMessages.sendToAllPlayers(message);
            }
        });
    }
    // Deactivates a SINGLE speaker linked to Master's Block Pos
    public static void deactivateSpeakerJukebox(ServerLevel level, BlockPos speakerPos) {
        if (level.getBlockEntity(speakerPos) instanceof SpeakerBlockEntity speakerBlock) {
            speakerBlock.setActive(false, ItemStack.EMPTY);
            S2CSyncJukeboxOrNoteblockStopMessage message = new S2CSyncJukeboxOrNoteblockStopMessage(speakerPos, false);
            ModMessages.sendToAllPlayers(message);
        }
    }
    // Link speaker to jukebox
    public static boolean linkSpeakerJukebox(ServerLevel level, BlockPos masterBlockPos, BlockPos speakerPos) {
        SpeakerLinkData data = getData(level);
        if (data == null) {
            BetterMusicDiscs.jukeboxLOGGING("(SpeakerLinkUtil) - Data is NULL");
            return false;
        }

        if (isValidPosition(level, masterBlockPos) && isValidPosition(level, speakerPos)) {
            data.addJukeboxLink(masterBlockPos, speakerPos);
            return true;
        }
        return false;
    }
    // Unlink speaker from jukebox
    public static boolean unlinkSpeakerJukebox(ServerLevel level, BlockPos speakerPos) {
        SpeakerLinkData data = getData(level);
        if (data == null) return false;

        BlockEntity jukebox = getLinkedJukebox(level, speakerPos);

        if (jukebox instanceof JukeboxBlockEntity jukeboxEntity) {
            // If the linked jukebox is valid, remove the link
            BlockPos jukeboxPos = jukeboxEntity.getBlockPos();
            data.removeJukeboxLink(jukeboxPos, speakerPos);
            deactivateSpeakerJukebox(level, speakerPos);
            BetterMusicDiscs.jukeboxLOGGING("(SpeakerLinkUtil) - Unlinked speaker " + speakerPos + " from jukebox " + jukeboxPos);
            return true;
        }
        return false;
    }
    // Unlink ALL speakers from jukebox
    public static boolean unlinkAllSpeakersJukebox(ServerLevel level, BlockPos jukeboxPos) {
        SpeakerLinkData data = getData(level);
        if (data == null) return false;

        deactivateSpeakersJukebox(level, jukeboxPos);
        data.removeAllJukeboxLinks(jukeboxPos);
        return true;
    }

    // JUKEBOX HELPER METHODS

    // Gets all the linked speakers from a jukebox
    public static List<BlockPos> getLinkedSpeakersJukebox(ServerLevel level, BlockPos jukeboxPos) {
        SpeakerLinkData data = getData(level);
        return data != null ?
                new ArrayList<>(data.getLinkedSpeakersJukebox().getOrDefault(jukeboxPos, Collections.emptySet())) :
                Collections.emptyList();
    }
    // Returns the jukebox block entity from a speakerPos
    public static BlockEntity getLinkedJukebox(ServerLevel level, BlockPos speakerPos) {
        SpeakerLinkData data = getData(level);
        // Make sure Data is there
        if (data == null) {
            BetterMusicDiscs.jukeboxLOGGING("(SpeakerLinkUtil) - Data is NULL - returning NULL");
            return null;
        }

        // Iterate through speaker links to find "master" jukebox
        for (Map.Entry<BlockPos, Set<BlockPos>> entry : data.getLinkedSpeakersJukebox().entrySet()) {
            if (entry.getValue().contains(speakerPos)) {
                BlockEntity blockEntity = level.getBlockEntity(entry.getKey());
                if (blockEntity instanceof JukeboxBlockEntity jukeboxBlockEntity) {
                    return jukeboxBlockEntity;
                }
            }
        }
        return null;
    }


    // NOTEBLOCK SIDE

    // Activates all speakers linked to Noteblock
    public static void activateSpeakersNoteblock(ServerLevel level, BlockPos noteblockPos, int note, String instrumentName) {
        getLinkedSpeakersNoteblock(level, noteblockPos).forEach(speakerPos -> {
            if (level.getExistingBlockEntity(speakerPos) instanceof SpeakerBlockEntity speaker) {
                int volume = speaker.getVolume();
                float scaledVolume = volume / 100.0F;

                BetterMusicDiscs.noteblockLOGGING("Noteblock has been activated.");

                BlockState state = speaker.getBlockState();
                if (state.is(ModBlocks.CEILING_SPEAKER.get())) {
                    speaker.spawnParticlesForNoteblockCeiling();

                } else if (!state.is(ModBlocks.CEILING_SPEAKER.get())) {
                    speaker.spawnParticlesForNoteBlock();
                }
                S2CSyncNoteblockSpeakersMessage message = new S2CSyncNoteblockSpeakersMessage(speakerPos, instrumentName, note, scaledVolume);
                ModMessages.sendToAllPlayers(message);
            }
        });
    }
    // Link a Speaker to Noteblock
    public static boolean linkSpeakerNoteblock(ServerLevel level, BlockPos noteblockPos, BlockPos speakerPos) {
        SpeakerLinkData data = getData(level);
        if (data == null) return false;

        if (isValidPosition(level, noteblockPos) && isValidPosition(level, speakerPos)) {
            data.addNoteblockLink(noteblockPos, speakerPos);
            return true;
        }
        return false;
    }
    // Unlink a Speaker from Noteblock
    public static boolean unlinkSpeakerNoteblock(ServerLevel level, BlockPos speakerPos) {
        SpeakerLinkData data = getData(level);
        if (data == null) return false;

        Block noteBlock = getLinkedNoteBlock(level, speakerPos);

        if (noteBlock instanceof NoteBlock) {
            // If the linked jukebox is valid, remove the link
            data.removeNoteblockLink(speakerPos);
            deactivateSpeakerJukebox(level, speakerPos);
            BetterMusicDiscs.speakerLOGGING("(SpeakerLinkUtil) - Unlinked speaker " + speakerPos + " from noteblock");
            return true;
        }
        return false;
    }
    // Unlink ALL Speakers from Noteblock
    public static boolean unlinkAllSpeakersNoteblock(ServerLevel level, BlockPos noteblockPos) {
        SpeakerLinkData data = getData(level);
        if (data == null) {
            return false;
        }
        data.removeAllNoteblockLinks(noteblockPos);
        return true;
    }

    // NOTEBLOCK HELPER METHODS

    // Gets all linked speakers from a noteblock
    public static List<BlockPos> getLinkedSpeakersNoteblock(ServerLevel level, BlockPos noteblockPos) {
        SpeakerLinkData data = getData(level);
        return data != null ?
                new ArrayList<>(data.getLinkedSpeakersNoteblock().getOrDefault(noteblockPos, Collections.emptySet())) :
                Collections.emptyList();
    }
    // Returns the noteblock block from a SpeakerPos
    public static Block getLinkedNoteBlock(ServerLevel level, BlockPos speakerPos) {
        SpeakerLinkData data = getData(level);

        if (data == null) {
            BetterMusicDiscs.speakerLOGGING("(SpeakerLinkUtil) - Data is NULL - returning NULL");
            return null;
        }

        for (Map.Entry<BlockPos, Set<BlockPos>> entry : data.getLinkedSpeakersNoteblock().entrySet()) {
            if (entry.getValue().contains(speakerPos)) {
                BlockState state = level.getBlockState(entry.getKey());
                if (state.getBlock() instanceof NoteBlock noteBlock) {
                    return noteBlock;
                }
            }
        }
        return null;
    }


    // CLASS HELPER METHODS
    private static SpeakerLinkData getData(ServerLevel level) {
        return SpeakerLinkData.get(level);
    }
    private static boolean isValidPosition(ServerLevel level, BlockPos pos) {
        return level.isLoaded(pos);
    }
    public static String isSpeakerLinked(ServerLevel level, BlockPos speakerPos) {
        // Check if the level is a ServerLevel before casting
        if (!(level instanceof ServerLevel serverLevel)) {
            BetterMusicDiscs.speakerLOGGING("(SpeakerLinkUtil) - Level is not a ServerLevel!");
            return "NULL";  // Or any appropriate fallback
        }

        SpeakerLinkData data = getData(serverLevel);

        // Log the SpeakerLinkData to check its contents
        BetterMusicDiscs.speakerLOGGING("(SpeakerLinkUtil) - SpeakerLinkData: " + data);

        // Log the speaker position being checked
        BetterMusicDiscs.speakerLOGGING("(SpeakerLinkUtil) - Checking speaker at: " + speakerPos);

        // Log the contents of the maps
        BetterMusicDiscs.speakerLOGGING("(SpeakerLinkUtil) - Jukebox Links: " + data.getLinkedSpeakersJukebox());
        BetterMusicDiscs.speakerLOGGING("(SpeakerLinkUtil) - Noteblock Links: " + data.getLinkedSpeakersNoteblock());
        BetterMusicDiscs.speakerLOGGING("(SpeakerLinkUtil) - Jukeblock Links: " + data.getLinkedSpeakersJukeblock());

        // Check if speakerPos is contained in the Set<BlockPos> for each map
        if (data.getLinkedSpeakersJukebox().values().stream().anyMatch(set -> set.contains(speakerPos))) {
            BetterMusicDiscs.speakerLOGGING("(SpeakerLinkUtil) - Speaker is linked to a Jukebox!");
            return "Jukebox";
        }
        if (data.getLinkedSpeakersNoteblock().values().stream().anyMatch(set -> set.contains(speakerPos))) {
            BetterMusicDiscs.speakerLOGGING("(SpeakerLinkUtil) - Speaker is linked to a Noteblock!");
            return "Noteblock";
        }
        if (data.getLinkedSpeakersJukeblock().values().stream().anyMatch(set -> set.contains(speakerPos))) {
            BetterMusicDiscs.speakerLOGGING("(SpeakerLinkUtil) - Speaker is linked to a Jukeblock!");
            return "Jukeblock";
        }

        BetterMusicDiscs.speakerLOGGING("(SpeakerLinkUtil) - Speaker is not linked!");
        return "NULL";
    }
}