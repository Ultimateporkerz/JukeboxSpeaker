package net.ultimporks.betterdiscs.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class TuningToolTagHelper {

    // Speaker Tag methods
    public static void saveSpeakerPosToTag(ItemStack tuningTool, BlockPos clickedPos) {
        int x = clickedPos.getX();
        int y = clickedPos.getY();
        int z = clickedPos.getZ();

        CompoundTag speakerData = tuningTool.getOrCreateTag();

        speakerData.putInt("SpeakerPosX", x);
        speakerData.putInt("SpeakerPosY", y);
        speakerData.putInt("SpeakerPosZ", z);

        tuningTool.setTag(speakerData);
    }
    public static boolean hasSpeakerTags(ItemStack tuningTool) {
        CompoundTag speakerData = tuningTool.getOrCreateTag();
        return speakerData.contains("SpeakerPosX") && speakerData.contains("SpeakerPosY") && speakerData.contains("SpeakerPosZ");
    }
    public static BlockPos getSpeakerPosFromTag(ItemStack tuningTool) {
        CompoundTag speakerData = tuningTool.getOrCreateTag();

        int x = speakerData.getInt("SpeakerPosX");
        int y = speakerData.getInt("SpeakerPosY");
        int z = speakerData.getInt("SpeakerPosZ");
        return new BlockPos(x, y, z);
    }
    public static boolean removeSpeakerTags(ItemStack tuningTool) {
        CompoundTag speakerData = tuningTool.getOrCreateTag();
        speakerData.remove("SpeakerPosX");
        speakerData.remove("SpeakerPosY");
        speakerData.remove("SpeakerPosZ");
        return true;
    }

    // Jukeblock Tag methods
    public static void saveJukeblockPosToTag(ItemStack tuningTool, BlockPos clickedPos) {
        int x = clickedPos.getX();
        int y = clickedPos.getY();
        int z = clickedPos.getZ();

        CompoundTag jukeblockData = tuningTool.getOrCreateTag();

        jukeblockData.putInt("jukeblockPosX", x);
        jukeblockData.putInt("jukeblockPosY", y);
        jukeblockData.putInt("jukeblockPosZ", z);

        tuningTool.setTag(jukeblockData);
    }
    public static boolean hasJukeblockTags(ItemStack tuningTool) {
        CompoundTag jukeblockData = tuningTool.getOrCreateTag();
        return jukeblockData.contains("jukeblockPosX") && jukeblockData.contains("jukeblockPosY") && jukeblockData.contains("jukeblockPosZ");
    }
    public static BlockPos getJukeblockPosFromTag(ItemStack tuningTool) {
        CompoundTag jukeblockData = tuningTool.getOrCreateTag();

        int x = jukeblockData.getInt("jukeblockPosX");
        int y = jukeblockData.getInt("jukeblockPosY");
        int z = jukeblockData.getInt("jukeblockPosZ");
        return new BlockPos(x, y, z);
    }
    public static boolean removeJukeblockTags(ItemStack tuningTool) {
        CompoundTag jukeblockData = tuningTool.getOrCreateTag();
        jukeblockData.remove("jukeblockPosX");
        jukeblockData.remove("jukeblockPosY");
        jukeblockData.remove("jukeblockPosZ");
        return true;
    }

    // Jukebox Tag methods
    public static void saveJukeboxPosToTag(ItemStack tuningTool, BlockPos clickedPos) {
        int x = clickedPos.getX();
        int y = clickedPos.getY();
        int z = clickedPos.getZ();

        CompoundTag jukeboxData = tuningTool.getOrCreateTag();

        jukeboxData.putInt("jukeboxPosX", x);
        jukeboxData.putInt("jukeboxPosY", y);
        jukeboxData.putInt("jukeboxPosZ", z);

        tuningTool.setTag(jukeboxData);
    }
    public static boolean hasJukeboxTags(ItemStack tuningTool) {
        CompoundTag jukeboxData = tuningTool.getOrCreateTag();
        return jukeboxData.contains("jukeboxPosX") && jukeboxData.contains("jukeboxPosY") && jukeboxData.contains("jukeboxPosZ");
    }
    public static BlockPos getJukeboxPosFromTag(ItemStack tuningTool) {
        CompoundTag jukeboxData = tuningTool.getOrCreateTag();

        int x = jukeboxData.getInt("jukeboxPosX");
        int y = jukeboxData.getInt("jukeboxPosY");
        int z = jukeboxData.getInt("jukeboxPosZ");
        return new BlockPos(x, y, z);
    }
    public static boolean removeJukeboxTags(ItemStack tuningTool) {
        CompoundTag jukeboxData = tuningTool.getOrCreateTag();
        jukeboxData.remove("jukeboxPosX");
        jukeboxData.remove("jukeboxPosY");
        jukeboxData.remove("jukeboxPosZ");
        return true;
    }

    // NoteBlock Tag methods
    public static void saveNoteBlockPosToTag(ItemStack tuningTool, BlockPos clickedPos) {
        int x = clickedPos.getX();
        int y = clickedPos.getY();
        int z = clickedPos.getZ();

        CompoundTag noteblockData = tuningTool.getOrCreateTag();

        noteblockData.putInt("NoteBlockPosX", x);
        noteblockData.putInt("NoteBlockPosY", y);
        noteblockData.putInt("NoteBlockPosZ", z);
    }
    public static boolean hasNoteBlockTags(ItemStack tuningTool) {
        CompoundTag noteblockData = tuningTool.getOrCreateTag();
        return noteblockData.contains("NoteBlockPosX") && noteblockData.contains("NoteBlockPosY") && noteblockData.contains("NoteBlockPosZ");
    }
    public static BlockPos getNoteBlockPosFromTag(ItemStack tuningTool) {
        CompoundTag noteblockData = tuningTool.getOrCreateTag();

        int x = noteblockData.getInt("NoteBlockPosX");
        int y = noteblockData.getInt("NoteBlockPosY");
        int z = noteblockData.getInt("NoteBlockPosZ");
        return new BlockPos(x, y, z);
    }
    public static boolean removeNoteBlockTags(ItemStack tuningTool) {
        CompoundTag noteblockData = tuningTool.getOrCreateTag();
        noteblockData.remove("NoteBlockPosX");
        noteblockData.remove("NoteBlockPosY");
        noteblockData.remove("NoteBlockPosZ");
        return true;
    }

    // Remove all tags from Tuning Tool
    public static boolean removeAllTags(ItemStack tuningTool) {
        return removeJukeboxTags(tuningTool) && removeNoteBlockTags(tuningTool) && removeSpeakerTags(tuningTool) && removeJukeblockTags(tuningTool);
    }
}
