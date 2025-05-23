package net.ultimporks.betterdiscs.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.ultimporks.betterdiscs.BetterMusicDiscs;
import net.ultimporks.betterdiscs.block.entity.SpeakerBlockEntity;
import net.ultimporks.betterdiscs.util.SpeakerLinkUtil;
import net.ultimporks.betterdiscs.util.TuningToolTagHelper;
import org.jetbrains.annotations.NotNull;

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
        ServerLevel serverLevel = (ServerLevel) pContext.getLevel();
        assert player != null;
        ItemStack tuningTool = player.getMainHandItem();
        // Handle Speaker Interaction
        if (blockEntity instanceof SpeakerBlockEntity) {
            BetterMusicDiscs.generalLOGGING("Clicked block is SpeakerBlockEntity");
            if (handleSpeakerEntity(player, blockPos, serverLevel, tuningTool)) {
                return InteractionResult.SUCCESS_NO_ITEM_USED;
            }
        }
        // Handle Jukeblock Interaction
    //    if (blockEntity instanceof JukeblockBlockEntity) {
    //        if (handleJukeblockEntity(player, blockPos, serverLevel, tuningTool)) {
    //            return InteractionResult.SUCCESS_NO_ITEM_USED;
    //        }
    //    }
        // Handle Jukebox Interaction
        if (blockEntity instanceof JukeboxBlockEntity) {
            BetterMusicDiscs.generalLOGGING("Clicked block is JukeboxBlockEntity");
            if (handleJukeboxEntity(player, blockPos, serverLevel, tuningTool)) {
                return InteractionResult.SUCCESS_NO_ITEM_USED;
            }
        }
        BlockState state = serverLevel.getBlockState(blockPos);
        // Handle NoteBlock Interaction
        if (state.getBlock() instanceof NoteBlock) {
            if (handleNoteBlock(player, blockPos, serverLevel, tuningTool)) {
                return InteractionResult.SUCCESS_NO_ITEM_USED;
            }
        }
        // Handle Tool Reset
        if (player.isCrouching() && !(blockEntity instanceof SpeakerBlockEntity) && !(blockEntity instanceof JukeboxBlockEntity) && !(state.getBlock() instanceof NoteBlock) /* && !(blockEntity instanceof JukeblockBlockEntity) */) {
            if (TuningToolTagHelper.hasJukeboxTags(tuningTool) || TuningToolTagHelper.hasNoteBlockTags(tuningTool) || TuningToolTagHelper.hasSpeakerTags(tuningTool) /* || TuningToolTagHelper.hasJukeblockTags(tuningTool) */) {
            if (TuningToolTagHelper.removeAllTags(tuningTool)) {
                player.sendSystemMessage(Component.literal("Tuning Tool has been reset!").withStyle(ChatFormatting.GREEN));
                    return InteractionResult.SUCCESS_NO_ITEM_USED;
                }
            }
        }
        return super.useOn(pContext);
    }

    // SpeakerEntity Handler
    private boolean handleSpeakerEntity(Player player, BlockPos speakerPos, ServerLevel level, ItemStack tuningTool) {
        // Prevent Speaker being linked to another Speaker
        if (TuningToolTagHelper.hasSpeakerTags(tuningTool)) {
            player.sendSystemMessage(Component.literal("Removing saved Speaker tags before linking Speaker, please try again.").withStyle(ChatFormatting.YELLOW));
            TuningToolTagHelper.removeSpeakerTags(tuningTool);
            return false;
        }
        // Unlink Mode if player is holding shift
        if (player.isShiftKeyDown()) {
            // Return early if no links are found
            if (SpeakerLinkUtil.isSpeakerLinked(level, speakerPos).equals("NULL")) {
                player.sendSystemMessage(Component.literal("Speaker is not linked to a Jukebox/NoteBlock/Jukeblock!").withStyle(ChatFormatting.YELLOW));
                return false;
            }

            if (SpeakerLinkUtil.isSpeakerLinked(level, speakerPos).equals("Jukebox")) {
                if (SpeakerLinkUtil.unlinkSpeakerJukebox(level, speakerPos)) {
                    player.sendSystemMessage(Component.literal("Speaker unlinked from Jukebox!").withStyle(ChatFormatting.GREEN));
                    return true;
                }
            }

            if (SpeakerLinkUtil.isSpeakerLinked(level, speakerPos).equals("Noteblock")) {
                if (SpeakerLinkUtil.unlinkSpeakerNoteblock(level, speakerPos)) {
                    player.sendSystemMessage(Component.literal("Speaker unlinked from Noteblock!").withStyle(ChatFormatting.GREEN));
                    return true;
                }
            }

        //    if (SpeakerLinkUtil.isSpeakerLinked(level, speakerPos).equals("Jukeblock")) {
        //        if (SpeakerLinkUtil.unlinkSpeakerJukeblock(level, speakerPos)) {
        //            player.sendSystemMessage(Component.literal("Speaker unlinked from Jukeblock!").withStyle(ChatFormatting.GREEN));
        //            return true;
        //        }
        //    }
        }
        // Link mode if player is NOT holding shift
        if (!player.isShiftKeyDown()) {
            // Make sure speaker is not linked before linking & return early if it is.
            if (!SpeakerLinkUtil.isSpeakerLinked(level, speakerPos).equals("NULL")) {
                player.sendSystemMessage(Component.literal("Speaker is already linked to a Jukebox, NoteBlock or Jukeblock!").withStyle(ChatFormatting.RED));
                TuningToolTagHelper.removeJukeboxTags(tuningTool);
                TuningToolTagHelper.removeNoteBlockTags(tuningTool);
        //        TuningToolTagHelper.removeJukeblockTags(tuningTool);
                return false;
            }

            boolean hasJukeboxTags = TuningToolTagHelper.hasJukeboxTags(tuningTool);
            boolean hasNoteblockTags = TuningToolTagHelper.hasNoteBlockTags(tuningTool);
        //    boolean hasJukeblockTags = TuningToolTagHelper.hasJukeblockTags(tuningTool);
            boolean hasSpeakerTags = TuningToolTagHelper.hasSpeakerTags(tuningTool);

            BetterMusicDiscs.generalLOGGING("Tag Statuses: " +
                    "Jukebox: " + hasJukeboxTags +
                    " Noteblock: " + hasNoteblockTags +
           //         " Jukeblock: " + hasJukeblockTags +
                    " Speaker: " + hasSpeakerTags);

            // Check player tags for (Jukebox / NoteBlock / Jukeblock)
            if (TuningToolTagHelper.hasJukeboxTags(tuningTool) && !TuningToolTagHelper.hasNoteBlockTags(tuningTool) /* && !TuningToolTagHelper.hasJukeblockTags(tuningTool) */) {
                BlockPos jukeboxPos = TuningToolTagHelper.getJukeboxPosFromTag(tuningTool);
                if (SpeakerLinkUtil.linkSpeakerJukebox(level, jukeboxPos, speakerPos)) {
                    player.sendSystemMessage(Component.literal("Link Completed!").withStyle(ChatFormatting.GREEN));
                    // Remove Jukebox tags after linking
                    TuningToolTagHelper.removeJukeboxTags(tuningTool);
                    return true;
                }
            }

            // Check player tags for (NoteBlock / Jukebox / Jukeblock)
            if (TuningToolTagHelper.hasNoteBlockTags(tuningTool) && !TuningToolTagHelper.hasJukeboxTags(tuningTool) /* && !TuningToolTagHelper.hasJukeblockTags(tuningTool) */) {
                BlockPos noteblockPos = TuningToolTagHelper.getNoteBlockPosFromTag(tuningTool);
                if (SpeakerLinkUtil.linkSpeakerNoteblock(level, noteblockPos, speakerPos)) {
                    player.sendSystemMessage(Component.literal("Link Completed!").withStyle(ChatFormatting.GREEN));
                    // Remove NoteBlock tags after linking
                    TuningToolTagHelper.removeNoteBlockTags(tuningTool);
                    return true;
                }
            }

            // Check player tags for (Jukeblock / Jukebox / Noteblock)
        //    if (TuningToolTagHelper.hasJukeblockTags(tuningTool) && !TuningToolTagHelper.hasJukeboxTags(tuningTool) && !TuningToolTagHelper.hasNoteBlockTags(tuningTool)) {
        //        BlockPos jukeblockPos = TuningToolTagHelper.getJukeblockPosFromTag(tuningTool);
        //        if (SpeakerLinkUtil.linkSpeakerJukeblock(level, jukeblockPos, speakerPos)) {
        //            player.sendSystemMessage(Component.literal("Link Completed!").withStyle(ChatFormatting.GREEN));
        //            TuningToolTagHelper.removeJukeblockTags(tuningTool);
        //            return true;
        //        }
        //    }

            // If player didn't have Jukebox, NoteBlock, or Jukeblock tags saved, save the Speaker pos to Tag
            if (!TuningToolTagHelper.hasJukeboxTags(tuningTool) || !TuningToolTagHelper.hasNoteBlockTags(tuningTool) /* || !TuningToolTagHelper.hasJukeblockTags(tuningTool) */) {
                player.sendSystemMessage(Component.literal("Selected Speaker: " + speakerPos.toShortString()).withStyle(ChatFormatting.YELLOW));
                TuningToolTagHelper.saveSpeakerPosToTag(tuningTool, speakerPos);
                return true;
            }
        }
        return false;
    }

    /*
    // Custom jukeblockEntity Handler
    private boolean handleJukeblockEntity(Player player, BlockPos jukeblockPos, ServerLevel level, ItemStack tuningTool) {
        // Prevent player from linking Jukebox to Jukeblock
        if (TuningToolTagHelper.hasJukeboxTags(tuningTool)) {
            player.sendSystemMessage(Component.literal("Removing Jukebox tags before linking Jukeblock, please try again.").withStyle(ChatFormatting.YELLOW));
            TuningToolTagHelper.removeJukeboxTags(tuningTool);
            return false;
        }
        // Prevent player from linking Noteblock to Jukeblock
        if (TuningToolTagHelper.hasNoteBlockTags(tuningTool)) {
            player.sendSystemMessage(Component.literal("Removing Noteblock tags before linking Jukeblock, please try again.").withStyle(ChatFormatting.YELLOW));
            TuningToolTagHelper.removeNoteBlockTags(tuningTool);
            return false;
        }

        // Check if speakerPos is saved
        if (TuningToolTagHelper.hasSpeakerTags(tuningTool)) {
            // If saved, get BlockPos and link to Jukeblock
            BlockPos speakerPos = TuningToolTagHelper.getSpeakerPosFromTag(tuningTool);
            if (SpeakerLinkUtil.linkSpeakerJukeblock(level, jukeblockPos, speakerPos)) {
                player.sendSystemMessage(Component.literal("Link Completed!").withStyle(ChatFormatting.GREEN));
                // Remove the saved speaker tags after linking
                TuningToolTagHelper.removeSpeakerTags(tuningTool);
                return true;
            }
        } else {
            player.sendSystemMessage(Component.literal("Selected Jukeblock: " + jukeblockPos.toShortString()).withStyle(ChatFormatting.YELLOW));
            TuningToolTagHelper.saveJukeblockPosToTag(tuningTool, jukeblockPos);
            return true;
        }
        return false;
    }
     */

    // JukeboxEntity Handler
    private boolean handleJukeboxEntity(Player player, BlockPos jukeboxPos, ServerLevel level, ItemStack tuningTool) {
        // Prevent player from linking NoteBlock to Jukebox.
        if (TuningToolTagHelper.hasNoteBlockTags(tuningTool)) {
            player.sendSystemMessage(Component.literal("Removing NoteBlock tags before linking Jukebox, please try again.").withStyle(ChatFormatting.YELLOW));
            TuningToolTagHelper.removeNoteBlockTags(tuningTool);
            return false;
        }
        /*
        // Prevent player from linking Jukeblock to Jukebox
        if (TuningToolTagHelper.hasJukeblockTags(tuningTool)) {
            player.sendSystemMessage(Component.literal("Removing Jukeblock tags before linking Jukebox, please try again.").withStyle(ChatFormatting.YELLOW));
        }
         */

        // Check if speakerPos is saved
        if (TuningToolTagHelper.hasSpeakerTags(tuningTool)) {
            BetterMusicDiscs.generalLOGGING("Tuning Tool has the Speaker tags!");
            // If saved, get BlockPos and link to Jukebox
            BlockPos speakerPos = TuningToolTagHelper.getSpeakerPosFromTag(tuningTool);
            BetterMusicDiscs.generalLOGGING("Speaker Pos: " + speakerPos);
            if (SpeakerLinkUtil.linkSpeakerJukebox(level, jukeboxPos, speakerPos)) {
                player.sendSystemMessage(Component.literal("Link Completed!").withStyle(ChatFormatting.GREEN));
                // Remove the saved tags after linking
                TuningToolTagHelper.removeSpeakerTags(tuningTool);
                return true;
            }
        } else {
            player.sendSystemMessage(Component.literal("Selected Jukebox: " + jukeboxPos.toShortString()).withStyle(ChatFormatting.YELLOW));
            TuningToolTagHelper.saveJukeboxPosToTag(tuningTool, jukeboxPos);
            return true;
        }
        return false;
    }

    // NoteBlock Handler
    private boolean handleNoteBlock(Player player, BlockPos noteblockPos, ServerLevel level, ItemStack tuningTool) {
        // Prevent player from linking Jukebox to NoteBlock
        if (TuningToolTagHelper.hasJukeboxTags(tuningTool)) {
            player.sendSystemMessage(Component.literal("Removing Jukebox tags before linking NoteBlock, please try again.").withStyle(ChatFormatting.YELLOW));
            TuningToolTagHelper.removeJukeboxTags(tuningTool);
            return false;
        }
        /*
        // Prevent player from linking Jukeblock to Noteblock
        if (TuningToolTagHelper.hasJukeblockTags(tuningTool)) {
            player.sendSystemMessage(Component.literal("Removing Jukeblock tags before linking Jukeblock, please try again.").withStyle(ChatFormatting.YELLOW));
            TuningToolTagHelper.removeJukeblockTags(tuningTool);
            return false;
        }
         */

        // Check if player has a SpeakerPos saved, if so link Speaker to NoteBlock
        if (TuningToolTagHelper.hasSpeakerTags(tuningTool)) {
            // If saved, get BlockPos and link to NoteBlock
            BlockPos speakerPos = TuningToolTagHelper.getSpeakerPosFromTag(tuningTool);
            if (SpeakerLinkUtil.linkSpeakerNoteblock(level, noteblockPos, speakerPos)) {
                player.sendSystemMessage(Component.literal("Link Completed!").withStyle(ChatFormatting.GREEN));
                // Remove the saved tags after linking
                TuningToolTagHelper.removeSpeakerTags(tuningTool);
                return true;
            }
        } else {
            player.sendSystemMessage(Component.literal("Selected NoteBlock: " + noteblockPos.toShortString()).withStyle(ChatFormatting.YELLOW));
            TuningToolTagHelper.saveNoteBlockPosToTag(tuningTool, noteblockPos);
            return true;
        }
        return false;
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return TuningToolTagHelper.hasSpeakerTags(pStack) || TuningToolTagHelper.hasJukeboxTags(pStack) || TuningToolTagHelper.hasNoteBlockTags(pStack) /* || TuningToolTagHelper.hasJukeblockTags(pStack) */;
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(Component.translatable("item.betterdiscs.description.tuning_tool"));
        pTooltipComponents.add(Component.literal("Crouch right-click an un-linkable block to reset tool"));
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
    }
}
