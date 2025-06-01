package net.ultimporks.betterdiscs.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.ultimporks.betterdiscs.BetterMusicDiscs;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SpeakerLinkData extends SavedData {
    private final Map<BlockPos, Set<BlockPos>> linkedSpeakersJukebox = new HashMap<>();
    private final Map<BlockPos, Set<BlockPos>> linkedSpeakersNoteblock = new HashMap<>();
    private final Map<BlockPos, Set<BlockPos>> linkedSpeakersJukeblock = new HashMap<>();

    // The NBT_KEY data is saved to
    private static final String NBT_KEY = "LinkedSpeakers";

    // Save, Load, and Get Methods

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        // Helper to serialize a BlockPos into a CompoundTag
        BiFunction<BlockPos, CompoundTag, CompoundTag> serializeBlockPos = (pos, compound) -> {
            compound.putInt("x", pos.getX());
            compound.putInt("y", pos.getY());
            compound.putInt("z", pos.getZ());
            return compound;
        };

        // Serialize a Map<BlockPos, Set<BlockPos>> into a ListTag
        java.util.function.Function<Map<BlockPos, Set<BlockPos>>, ListTag> serializeMap = (map) -> {
            ListTag list = new ListTag();
            for (Map.Entry<BlockPos, Set<BlockPos>> entry : map.entrySet()) {
                CompoundTag entryTag = new CompoundTag();

                // Serialize key BlockPos
                CompoundTag keyTag = new CompoundTag();
                serializeBlockPos.apply(entry.getKey(), keyTag);
                entryTag.put("key", keyTag);

                // Serialize Set<BlockPos> as ListTag
                ListTag valueList = new ListTag();
                for (BlockPos pos : entry.getValue()) {
                    CompoundTag posTag = new CompoundTag();
                    serializeBlockPos.apply(pos, posTag);
                    valueList.add(posTag);
                }
                entryTag.put("value", valueList);

                list.add(entryTag);
            }
            return list;
        };

        tag.put("linkedSpeakersJukebox", serializeMap.apply(linkedSpeakersJukebox));
        tag.put("linkedSpeakersNoteblock", serializeMap.apply(linkedSpeakersNoteblock));
        tag.put("linkedSpeakersJukeblock", serializeMap.apply(linkedSpeakersJukeblock));

        return tag;
    }
    public static SpeakerLinkData load(CompoundTag tag, HolderLookup.Provider registries) {
        SpeakerLinkData data = new SpeakerLinkData();

        // Helper to deserialize a BlockPos from a CompoundTag
        Function<CompoundTag, BlockPos> deserializeBlockPos = (compound) -> {
            return new BlockPos(
                    compound.getInt("x"),
                    compound.getInt("y"),
                    compound.getInt("z")
            );
        };

        // Deserialize a ListTag into a Map<BlockPos, Set<BlockPos>>
        Function<ListTag, Map<BlockPos, Set<BlockPos>>> deserializeMap = (list) -> {
            Map<BlockPos, Set<BlockPos>> map = new HashMap<>();
            for (Tag entryTag : list) {
                if (entryTag instanceof CompoundTag compoundEntry) {
                    // Deserialize key BlockPos
                    CompoundTag keyTag = compoundEntry.getCompound("key");
                    BlockPos key = deserializeBlockPos.apply(keyTag);

                    // Deserialize Set<BlockPos> from ListTag
                    ListTag valueList = compoundEntry.getList("value", Tag.TAG_COMPOUND);
                    Set<BlockPos> value = new HashSet<>();
                    for (Tag posTag : valueList) {
                        if (posTag instanceof CompoundTag compoundPos) {
                            value.add(deserializeBlockPos.apply(compoundPos));
                        }
                    }

                    map.put(key, value);
                }
            }
            return map;
        };

        if (tag.contains("linkedSpeakersJukebox", Tag.TAG_LIST)) {
            data.linkedSpeakersJukebox.putAll(deserializeMap.apply(tag.getList("linkedSpeakersJukebox", Tag.TAG_COMPOUND)));
        }
        if (tag.contains("linkedSpeakersNoteblock", Tag.TAG_LIST)) {
            data.linkedSpeakersNoteblock.putAll(deserializeMap.apply(tag.getList("linkedSpeakersNoteblock", Tag.TAG_COMPOUND)));
        }
        if (tag.contains("linkedSpeakersJukeblock", Tag.TAG_LIST)) {
            data.linkedSpeakersJukeblock.putAll(deserializeMap.apply(tag.getList("linkedSpeakersJukeblock", Tag.TAG_COMPOUND)));
        }

        return data;
    }
    public static SpeakerLinkData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(
                        SpeakerLinkData::new,
                        SpeakerLinkData::load,
                        DataFixTypes.LEVEL
                ),
                NBT_KEY
        );
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
}
