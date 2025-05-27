package net.ultimporks.betterdiscs.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.ultimporks.betterdiscs.init.ModDataComponents;

public class TuningToolTagHelper {

    // Speaker Tag methods
    public static void saveSpeakerPosToTag(ItemStack tuningTool, BlockPos clickedPos) {
        tuningTool.set(ModDataComponents.SPEAKER_POS.get(), clickedPos);
    }
    public static boolean hasSpeakerTags(ItemStack tuningTool) {
        BlockPos savedBlockPos = tuningTool.get(ModDataComponents.SPEAKER_POS.get());
        return savedBlockPos != null;
    }
    public static BlockPos getSpeakerPosFromTag(ItemStack tuningTool) {
        BlockPos speakerPos = tuningTool.get(ModDataComponents.SPEAKER_POS.get());
        return new BlockPos(speakerPos);
    }
    public static boolean removeSpeakerTags(ItemStack tuningTool) {
        tuningTool.remove(ModDataComponents.SPEAKER_POS.get());
        return true;
    }
    /*
    // Jukeblock Tag methods
    public static void saveJukeblockPosToTag(ItemStack tuningTool, BlockPos clickedPos) {
        tuningTool.set(ModDataComponents.JUKEBLOCK_POS.get(), clickedPos);
    }
    public static boolean hasJukeblockTags(ItemStack tuningTool) {
        BlockPos savedBlockPos = tuningTool.get(ModDataComponents.JUKEBLOCK_POS.get());
        return savedBlockPos != null;
    }
    public static BlockPos getJukeblockPosFromTag(ItemStack tuningTool) {
        BlockPos jukeblockPos = tuningTool.get(ModDataComponents.JUKEBLOCK_POS.get());
        return new BlockPos(jukeblockPos);
    }
    public static boolean removeJukeblockTags(ItemStack tuningTool) {
        tuningTool.remove(ModDataComponents.JUKEBLOCK_POS.get());
        return true;
    }
    */
    // Jukebox Tag methods
    public static void saveJukeboxPosToTag(ItemStack tuningTool, BlockPos clickedPos) {
        tuningTool.set(ModDataComponents.JUKEBOX_POS.get(), clickedPos);
    }
    public static boolean hasJukeboxTags(ItemStack tuningTool) {
        BlockPos savedBlockPos = tuningTool.get(ModDataComponents.JUKEBOX_POS.get());
        return savedBlockPos != null;
    }
    public static BlockPos getJukeboxPosFromTag(ItemStack tuningTool) {
        BlockPos jukeboxPos = tuningTool.get(ModDataComponents.JUKEBOX_POS.get());
        return new BlockPos(jukeboxPos);
    }
    public static boolean removeJukeboxTags(ItemStack tuningTool) {
        tuningTool.remove(ModDataComponents.JUKEBOX_POS.get());
        return true;
    }

    // NoteBlock Tag methods
    public static void saveNoteBlockPosToTag(ItemStack tuningTool, BlockPos clickedPos) {
        tuningTool.set(ModDataComponents.NOTEBLOCK_POS.get(), clickedPos);
    }
    public static boolean hasNoteBlockTags(ItemStack tuningTool) {
        BlockPos savedBlockPos = tuningTool.get(ModDataComponents.NOTEBLOCK_POS.get());
        return savedBlockPos != null;
    }
    public static BlockPos getNoteBlockPosFromTag(ItemStack tuningTool) {
        BlockPos noteblockPos = tuningTool.get(ModDataComponents.NOTEBLOCK_POS.get());
        return new BlockPos(noteblockPos);
    }
    public static boolean removeNoteBlockTags(ItemStack tuningTool) {
        tuningTool.remove(ModDataComponents.NOTEBLOCK_POS.get());
        return true;
    }

    // Remove all tags from Tuning Tool
    public static boolean removeAllTags(ItemStack tuningTool) {
        return removeJukeboxTags(tuningTool) && removeNoteBlockTags(tuningTool) && removeSpeakerTags(tuningTool);
    }
}
