package net.ultimporks.betterdiscs.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.ultimporks.betterdiscs.BetterMusicDiscs;
import net.ultimporks.betterdiscs.block.entity.JukeblockBlockEntity;
import net.ultimporks.betterdiscs.block.entity.SpeakerBlockEntity;
import net.ultimporks.betterdiscs.util.SpeakerLinkUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TuningTool extends Item {
    public TuningTool(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel().isClientSide) {
            return InteractionResult.FAIL;
        }
        BlockPos blockPos = pContext.getClickedPos();
        BlockEntity blockEntity = pContext.getLevel().getBlockEntity(blockPos);
        Player player = pContext.getPlayer();
        Level level = pContext.getLevel();
        assert player != null;
        ItemStack tuningTool = player.getMainHandItem();
        // Handle Speaker Interaction
        if (blockEntity instanceof SpeakerBlockEntity) {
            if (handleSpeakerEntity(player, blockPos, level, tuningTool)) {
                return InteractionResult.SUCCESS;
            }
        }
        // Handle Jukeblock Interaction
        if (blockEntity instanceof JukeblockBlockEntity) {
            if (handleJukeblockEntity(player, blockPos, level, tuningTool)) {
                return InteractionResult.SUCCESS;
            }
        }

        // Handle Jukebox Interaction
        if (blockEntity instanceof JukeboxBlockEntity) {
            if (handleJukeboxEntity(player, blockPos, level, tuningTool)) {
                return InteractionResult.SUCCESS;
            }
        }
        BlockState state = level.getBlockState(blockPos);
        // Handle NoteBlock Interaction
        if (state.getBlock() instanceof NoteBlock) {
            if (handleNoteBlock(player, blockPos, level, tuningTool)) {
                return InteractionResult.SUCCESS;
            }
        }
        // Handle Tool Reset
        if (player.isCrouching() && !(blockEntity instanceof SpeakerBlockEntity) && !(blockEntity instanceof JukeboxBlockEntity) && !(state.getBlock() instanceof NoteBlock)) {
            if (hasJukeboxTags(tuningTool) || hasNoteBlockTags(tuningTool) || hasSpeakerTags(tuningTool)) {
            if (removeAllTags(tuningTool)) {
                    player.sendSystemMessage(Component.literal("Tuning Tool has been reset!").withStyle(ChatFormatting.GREEN));
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.useOn(pContext);
    }

    // SpeakerEntity Handler
    private boolean handleSpeakerEntity(Player player, BlockPos speakerPos, Level level, ItemStack tuningTool) {
        if (level.isClientSide) return false;

        // Unlink Mode if player is holding shift
        if (player.isShiftKeyDown()) {
            // Return early if no links are found
            if (SpeakerLinkUtil.isSpeakerLinked(level, speakerPos).equals("NULL")) {
                player.sendSystemMessage(Component.literal("Speaker is not linked to a Jukebox/NoteBlock/Jukeblock!").withStyle(ChatFormatting.YELLOW));
                return false;
            }

            if (SpeakerLinkUtil.isSpeakerLinked(level, speakerPos).equals("Jukebox")) {
                if (SpeakerLinkUtil.unlinkSpeakerJukeblock(level, speakerPos)) {
                    player.sendSystemMessage(Component.literal("Speaker unlinked from Jukebox!").withStyle(ChatFormatting.GREEN));
                    return true;
                }
            }

            if (SpeakerLinkUtil.isSpeakerLinked(level, speakerPos).equals("Noteblock")) {
                if (SpeakerLinkUtil.unlinkSpeakerJukeblock(level, speakerPos)) {
                    player.sendSystemMessage(Component.literal("Speaker unlinked from Noteblock!").withStyle(ChatFormatting.GREEN));
                    return true;
                }
            }

            if (SpeakerLinkUtil.isSpeakerLinked(level, speakerPos).equals("Jukeblock")) {
                if (SpeakerLinkUtil.unlinkSpeakerJukeblock(level, speakerPos)) {
                    player.sendSystemMessage(Component.literal("Speaker unlinked from Jukeblock!").withStyle(ChatFormatting.GREEN));
                    return true;
                }
            }
        }
        // Link mode if player is NOT holding shift
        if (!player.isShiftKeyDown()) {
            // Make sure speaker is not linked before linking & return early if it is.
            if (!SpeakerLinkUtil.isSpeakerLinked(level, speakerPos).equals("NULL")) {
                player.sendSystemMessage(Component.literal("Speaker is already linked to a Jukebox, NoteBlock or Jukeblock!").withStyle(ChatFormatting.RED));
                removeJukeboxTags(tuningTool);
                removeNoteBlockTags(tuningTool);
                removeJukeblockTags(tuningTool);
                return false;
            }

            // Check player tags for (Jukebox / NoteBlock / Jukeblock)
            if (hasJukeboxTags(tuningTool) && !hasNoteBlockTags(tuningTool) && !hasJukeblockTags(tuningTool)) {
                BlockPos jukeboxPos = getJukeboxPosFromTag(tuningTool);
                if (SpeakerLinkUtil.linkSpeakerJukebox(level, jukeboxPos, speakerPos)) {
                    player.sendSystemMessage(Component.literal("Link Completed!").withStyle(ChatFormatting.GREEN));
                    // Remove Jukebox tags after linking
                    removeJukeboxTags(tuningTool);
                    return true;
                }
            }

            // Check player tags for (NoteBlock / Jukebox / Jukeblock)
            if (hasNoteBlockTags(tuningTool) && !hasJukeboxTags(tuningTool) && !hasJukeblockTags(tuningTool)) {
                BlockPos noteblockPos = getNoteBlockPosFromTag(tuningTool);
                if (SpeakerLinkUtil.linkSpeakerNoteblock(level, noteblockPos, speakerPos)) {
                    player.sendSystemMessage(Component.literal("Link Completed!").withStyle(ChatFormatting.GREEN));
                    // Remove NoteBlock tags after linking
                    removeNoteBlockTags(tuningTool);
                    return true;
                }
            }

            // Check player tags for (Jukeblock / Jukebox / Noteblock)
            if (hasJukeblockTags(tuningTool) && !hasJukeboxTags(tuningTool) && !hasNoteBlockTags(tuningTool)) {
                BlockPos jukeblockPos = getJukeblockPosFromTag(tuningTool);
                if (SpeakerLinkUtil.linkSpeakerJukeblock(level, jukeblockPos, speakerPos)) {
                    player.sendSystemMessage(Component.literal("Link Completed!").withStyle(ChatFormatting.GREEN));
                    removeJukeblockTags(tuningTool);
                    return true;
                }
            }

            // If player didn't have Jukebox, NoteBlock, or Jukeblock tags saved, save the Speaker pos to Tag
            if (!hasJukeboxTags(tuningTool) || !hasNoteBlockTags(tuningTool) || !hasJukeblockTags(tuningTool)) {
                player.sendSystemMessage(Component.literal("Selected Speaker: " + speakerPos.toShortString()).withStyle(ChatFormatting.YELLOW));
                saveSpeakerPosToTag(tuningTool, speakerPos);
                return true;
            }
        }

        return false;
    }

    // Custom jukeblockEntity Handler
    private boolean handleJukeblockEntity(Player player, BlockPos jukeblockPos, Level level, ItemStack tuningTool) {
        if (level.isClientSide) return false;

        // Prevent player from linking Jukebox to Custom Jukebox
        if (hasJukeboxTags(tuningTool)) {
            player.sendSystemMessage(Component.literal("Removing Jukebox tags before linking Jukeblock, please try again.").withStyle(ChatFormatting.YELLOW));
            removeJukeboxTags(tuningTool);
            return false;
        }

        // Check if speakerPos is saved
        if (hasSpeakerTags(tuningTool)) {
            // If saved, get BlockPos and link to Jukeblock
            BlockPos speakerPos = getSpeakerPosFromTag(tuningTool);
            if (SpeakerLinkUtil.linkSpeakerJukeblock(level, jukeblockPos, speakerPos)) {
                player.sendSystemMessage(Component.literal("Link Completed!").withStyle(ChatFormatting.GREEN));
                // Remove the saved speaker tags after linking
                removeSpeakerTags(tuningTool);
                return true;
            }
        }
        // If speakerPos is not saved, save the jukeblock pos
        if (!hasSpeakerTags(tuningTool)) {
            player.sendSystemMessage(Component.literal("Selected Jukeblock: " + jukeblockPos.toShortString()).withStyle(ChatFormatting.YELLOW));
            saveJukeblockPosToTag(tuningTool, jukeblockPos);
            return true;
        }
        return false;
    }

    // JukeboxEntity Handler
    private boolean handleJukeboxEntity(Player player, BlockPos jukeboxPos, Level level, ItemStack tuningTool) {
        if (level.isClientSide) return false;

        // Prevent player from linking NoteBlock to Jukebox.
        if (hasNoteBlockTags(tuningTool)) {
            player.sendSystemMessage(Component.literal("Removing NoteBlock tags before linking Jukebox, please try again.").withStyle(ChatFormatting.YELLOW));
            removeNoteBlockTags(tuningTool);
            return false;
        }
        // Check if speakerPos is saved
        if (hasSpeakerTags(tuningTool)) {
            // If saved, get BlockPos and link to Jukebox
            BlockPos speakerPos = getSpeakerPosFromTag(tuningTool);
            if (SpeakerLinkUtil.linkSpeakerJukebox(level, jukeboxPos, speakerPos)) {
                player.sendSystemMessage(Component.literal("Link Completed!").withStyle(ChatFormatting.GREEN));
                // Remove the saved tags after linking
                removeSpeakerTags(tuningTool);
                return true;
            }
        }
        // If speaker pos is not saved, save JukeboxPos
        if (!hasSpeakerTags(tuningTool)) {
            player.sendSystemMessage(Component.literal("Selected Jukebox: " + jukeboxPos.toShortString()).withStyle(ChatFormatting.YELLOW));
            saveJukeboxPosToTag(tuningTool, jukeboxPos);
            return true;
        }
        return false;
    }

    // NoteBlock Handler
    private boolean handleNoteBlock(Player player, BlockPos noteblockPos, Level level, ItemStack tuningTool) {
        if (level.isClientSide) return false;

        // Prevent player from linking Jukebox to NoteBlock
        if (hasJukeboxTags(tuningTool)) {
            player.sendSystemMessage(Component.literal("Removing Jukebox tags before linking NoteBlock, please try again.").withStyle(ChatFormatting.YELLOW));
            removeJukeboxTags(tuningTool);
            return false;
        }
        // Check if player has a SpeakerPos saved, if so link Speaker to NoteBlock
        if (hasSpeakerTags(tuningTool)) {
            // If saved, get BlockPos and link to NoteBlock
            BlockPos speakerPos = getSpeakerPosFromTag(tuningTool);
            if (SpeakerLinkUtil.linkSpeakerNoteblock(level, noteblockPos, speakerPos)) {
                player.sendSystemMessage(Component.literal("Link Completed!").withStyle(ChatFormatting.GREEN));
                // Remove the saved tags after linking
                removeSpeakerTags(tuningTool);
                return true;
            }
        }

        // If speaker pos is not saved, save NoteBlockPos
        if (!hasSpeakerTags(tuningTool)) {
            player.sendSystemMessage(Component.literal("Selected NoteBlock: " + noteblockPos.toShortString()).withStyle(ChatFormatting.YELLOW));
            saveNoteBlockPosToTag(tuningTool, noteblockPos);
            return true;
        }
        return false;
    }

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
    private boolean removeAllTags(ItemStack tuningTool) {
        return removeJukeboxTags(tuningTool) && removeNoteBlockTags(tuningTool) && removeSpeakerTags(tuningTool);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return hasSpeakerTags(pStack) || hasJukeboxTags(pStack) || hasNoteBlockTags(pStack);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("item.betterdiscs.description.tuning_tool"));
        pTooltipComponents.contains(Component.literal("Crouch right-click an un-linkable block to reset tool"));
    }
}
