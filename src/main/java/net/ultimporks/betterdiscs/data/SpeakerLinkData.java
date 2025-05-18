package net.ultimporks.betterdiscs.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;
import net.ultimporks.betterdiscs.BetterMusicDiscs;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SpeakerLinkData extends SavedData {
    private final Map<BlockPos, Set<BlockPos>> linkedSpeakersJukebox = new HashMap<>();
    private final Map<BlockPos, Set<BlockPos>> linkedSpeakersNoteblock = new HashMap<>();
    private final Map<BlockPos, Set<BlockPos>> linkedSpeakersJukeblock = new HashMap<>();

    // The NBT_KEY data is saved to
    private static final String NBT_KEY = "LinkedSpeakers";

    @Override
    public @NotNull CompoundTag save(CompoundTag tag, HolderLookup.Provider pRegistries) {
        CompoundTag dataTag = new CompoundTag();
        saveSpeakerMap(dataTag, "jukebox", linkedSpeakersJukebox);
        saveSpeakerMap(dataTag, "noteblock", linkedSpeakersNoteblock);
        saveSpeakerMap(dataTag, "jukeblock", linkedSpeakersJukeblock);
        tag.put(NBT_KEY, dataTag);
        return tag;
    }

    public static SpeakerLinkData load(CompoundTag tag) {
        SpeakerLinkData data = new SpeakerLinkData();

        if (tag == null || !tag.contains(NBT_KEY, Tag.TAG_COMPOUND)) {
            BetterMusicDiscs.speakerLOGGING("(SpeakerLinkData) - Invalid or missing NBT data!");
            return data;
        }

        CompoundTag dataTag = tag.getCompound(NBT_KEY);
        loadSpeakerMap(dataTag, "jukebox", data.linkedSpeakersJukebox);
        loadSpeakerMap(dataTag, "noteblock", data.linkedSpeakersNoteblock);
        loadSpeakerMap(dataTag, "jukeblock", data.linkedSpeakersJukeblock);

        return data;
    }

    // Jukebox Side

    public Map<BlockPos, Set<BlockPos>> getLinkedSpeakersJukebox() {
        return new HashMap<>(linkedSpeakersJukebox);
    }
    public BlockPos getJukeboxFromSpeaker(BlockPos speakerPos) {
        for (Map.Entry<BlockPos, Set<BlockPos>> entry : linkedSpeakersJukebox.entrySet()) {
            BlockPos jukeboxPos = entry.getKey();
            Set<BlockPos> speakers = entry.getValue();
            if (speakers.contains(speakerPos)) {
                return jukeboxPos;
            }
        }
        BetterMusicDiscs.speakerLOGGING("(SpeakerLinkData) - No Jukebox found for Speaker!");
        return null;
    }
    public void addJukeboxLink(BlockPos jukeboxPos, BlockPos speakerPos) {
        if (jukeboxPos == null || speakerPos == null) {
            BetterMusicDiscs.speakerLOGGING("(SpeakerLinkData) - Tried to link Speaker to Jukebox with NULL positions!");
            return;
        }
        linkedSpeakersJukebox.computeIfAbsent(jukeboxPos, k -> ConcurrentHashMap.newKeySet()).add(speakerPos);
        setDirty();
    }
    public void removeJukeboxLink(BlockPos jukeboxPos, BlockPos speakerPos) {
        if (linkedSpeakersJukebox.containsKey(jukeboxPos)) {
            linkedSpeakersJukebox.get(jukeboxPos).remove(speakerPos);
            BetterMusicDiscs.speakerLOGGING("(SpeakerLinkData) - Removed link from SpeakerMap");
            setDirty();
            // Remove the Jukebox Pos if no more links exist.
            if (linkedSpeakersJukebox.get(jukeboxPos).isEmpty()) {
                linkedSpeakersJukebox.remove(jukeboxPos);
                BetterMusicDiscs.speakerLOGGING("(SpeakerLinkData) - No more links to Jukebox exist, removing Jukebox linkKey from SpeakerMap...");
                setDirty();
            }
        }
    }
    public void removeAllJukeboxLinks(BlockPos jukeboxPos) {
        if (linkedSpeakersJukebox.containsKey(jukeboxPos)) {
            BetterMusicDiscs.speakerLOGGING("(SpeakerLinkUtil) - Removed all speaker links linked to Jukebox " + jukeboxPos.toShortString());
            linkedSpeakersJukebox.remove(jukeboxPos);
            setDirty();
        }
    }


    // Noteblock Side

    public Map<BlockPos, Set<BlockPos>> getLinkedSpeakersNoteblock() {
        return new HashMap<>(linkedSpeakersNoteblock);
    }
    public BlockPos getNoteblockFromSpeaker(BlockPos speakerPos) {
        for (Map.Entry<BlockPos, Set<BlockPos>> entry : linkedSpeakersNoteblock.entrySet()) {
            BlockPos jukeboxPos = entry.getKey();
            Set<BlockPos> speakers = entry.getValue();
            if (speakers.contains(speakerPos)) {
                return jukeboxPos;
            }
        }
        BetterMusicDiscs.speakerLOGGING("(SpeakerLinkData) - No Jukebox found for Speaker!");
        return null;
    }
    public void addNoteblockLink(BlockPos noteblockPos, BlockPos speakerPos) {
        if (noteblockPos == null || speakerPos == null) {
            BetterMusicDiscs.speakerLOGGING("(SpeakerLinkData) - Tried to link Speaker to Noteblock with NULL positions!");
            return;
        }
        linkedSpeakersNoteblock.computeIfAbsent(noteblockPos, k -> ConcurrentHashMap.newKeySet()).add(speakerPos);
        setDirty();
    }
    public void removeNoteblockLink(BlockPos speakerPos) {
        BlockPos noteblockPos = getNoteblockFromSpeaker(speakerPos);
        if (linkedSpeakersNoteblock.containsKey(noteblockPos)) {
            linkedSpeakersNoteblock.get(noteblockPos).remove(speakerPos);
            BetterMusicDiscs.speakerLOGGING("(SpeakerLinkData) - Removed link from SpeakerMap");
            setDirty();
            // Remove the Jukebox Pos if no more links exist.
            if (linkedSpeakersNoteblock.get(noteblockPos).isEmpty()) {
                linkedSpeakersNoteblock.remove(noteblockPos);
                BetterMusicDiscs.speakerLOGGING("(SpeakerLinkData) - No more links to Noteblock exist, removing Noteblock linkKey from SpeakerMap...");
                setDirty();
            }
        }
    }
    public void removeAllNoteblockLinks(BlockPos noteblockPos) {
        if (linkedSpeakersNoteblock.containsKey(noteblockPos)) {
            BetterMusicDiscs.speakerLOGGING("(SpeakerLinkUtil) - Removed all speaker links linked to Noteblock " + noteblockPos.toShortString());
            linkedSpeakersNoteblock.remove(noteblockPos);
            setDirty();
        }
    }


    // Jukeblock Side

    public Map<BlockPos, Set<BlockPos>> getLinkedSpeakersJukeblock() {
        return new HashMap<>(linkedSpeakersJukeblock);
    }
    public BlockPos getJukeblockFromSpeaker(BlockPos speakerPos) {
        for (Map.Entry<BlockPos, Set<BlockPos>> entry : linkedSpeakersJukeblock.entrySet()) {
            BlockPos jukeboxPos = entry.getKey();
            Set<BlockPos> speakers = entry.getValue();
            if (speakers.contains(speakerPos)) {
                return jukeboxPos;
            }
        }
        BetterMusicDiscs.speakerLOGGING("(SpeakerLinkData) - No Jukeblock found for Speaker!");
        return null;
    }
    public void addJukeblockLink(BlockPos jukeblockPos, BlockPos speakerPos) {
        if (jukeblockPos == null || speakerPos == null) {
            BetterMusicDiscs.speakerLOGGING("(SpeakerLinkData) - Tried to link Speaker to Jukeblock with NULL positions!");
            return;
        }
        linkedSpeakersJukeblock.computeIfAbsent(jukeblockPos, k -> ConcurrentHashMap.newKeySet()).add(speakerPos);
        setDirty();
    }
    public void removeJukeblockLink(BlockPos jukeblockPos, BlockPos speakerPos) {
        if (linkedSpeakersJukeblock.containsKey(jukeblockPos)) {
            linkedSpeakersJukeblock.get(jukeblockPos).remove(speakerPos);
            BetterMusicDiscs.speakerLOGGING("(SpeakerLinkData) - Removed link from SpeakerMap");
            setDirty();
            // Remove the Jukebox Pos if no more links exist.
            if (linkedSpeakersJukeblock.get(jukeblockPos).isEmpty()) {
                linkedSpeakersJukeblock.remove(jukeblockPos);
                BetterMusicDiscs.speakerLOGGING("(SpeakerLinkData) - No more links to Jukeblock exist, removing Jukeblock linkKey from SpeakerMap...");
                setDirty();
            }
        }
    }
    public void removeAllJukeblockLinks(BlockPos jukeblockPos) {
        if (linkedSpeakersJukeblock.containsKey(jukeblockPos)) {
            BetterMusicDiscs.speakerLOGGING("(SpeakerLinkUtil) - Removed all speaker links linked to Jukeblock " + jukeblockPos.toShortString());
            linkedSpeakersJukeblock.remove(jukeblockPos);
            setDirty();
        }
    }


    // CLASS HELPERS

    // Save / Load helpers
    private static String posToString(BlockPos pos) {
        return pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }
    // Updated stringToPos method
    private static BlockPos stringToPos(String key) {
        String[] parts = key.split(",");
        if (parts.length != 3) {
            BetterMusicDiscs.speakerLOGGING("(SpeakerLinkUtil) - Invalid BlockPos key format: " + key);
            return null; // Return null and skip invalid entries
        }
        try {
            return new BlockPos(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2])
            );
        } catch (NumberFormatException e) {
            BetterMusicDiscs.speakerLOGGING("(SpeakerLinkUtil) - Failed to parse BlockPos from key: {} " + key + " " + e);
            return null;
        }
    }
    // Helper method to save maps
    private static void saveSpeakerMap(CompoundTag dataTag, String key, Map<BlockPos, Set<BlockPos>> speakerMap) {
        CompoundTag mapTag = new CompoundTag();
        speakerMap.forEach((masterPos, speakers) -> {
            ListTag speakerList = new ListTag();
            speakers.forEach(speakerPos -> speakerList.add(NbtUtils.writeBlockPos(speakerPos)));
            mapTag.put(posToString(masterPos), speakerList);
        });
        dataTag.put(key, mapTag);
    }
    // Helper method to load maps
    private static void loadSpeakerMap(CompoundTag dataTag, String key, Map<BlockPos, Set<BlockPos>> speakerMap) {
        if (!dataTag.contains(key, Tag.TAG_COMPOUND)) return;

        CompoundTag mapTag = dataTag.getCompound(key);
        for (String masterKey : mapTag.getAllKeys()) {
            BlockPos masterPos = stringToPos(masterKey);
            if (masterPos == null) {
                BetterMusicDiscs.speakerLOGGING("(SpeakerLinkData) - Invalid master block position in NBT: " + masterKey);
                continue;
            }

            ListTag speakerList = mapTag.getList(masterKey, Tag.TAG_COMPOUND);
            Set<BlockPos> speakers = new HashSet<>();

            for (int i = 0; i < speakerList.size(); i++) {
                BlockPos speakerPos = readBlockPosSafe(speakerList.getCompound(i));

                if (!speakerPos.equals(masterPos)) {
                    speakers.add(speakerPos);
                } else {
                    BetterMusicDiscs.speakerLOGGING("(SpeakerLinkData) - Speaker cannot be linked to itself: " + masterPos);
                }
            }
            if (!speakers.isEmpty()) {
                speakerMap.put(masterPos, speakers);
            }
        }
    }

    private static BlockPos readBlockPosSafe(CompoundTag tag) {
        try {
            int x = tag.getInt("X");
            int y = tag.getInt("Y");
            int z = tag.getInt("Z");
            return new BlockPos(x, y, z);
        } catch (Exception e) {
            BetterMusicDiscs.speakerLOGGING("Failed to read BlockPos from tag: " + tag);
            return null;
        }
    }


}
