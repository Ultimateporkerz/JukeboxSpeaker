package net.ultimporks.betterdiscs.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.WritableLevelData;
import net.ultimporks.betterdiscs.BetterMusicDiscs;
import net.ultimporks.betterdiscs.block.SpeakerBlock;
import net.ultimporks.betterdiscs.util.SpeakerLinkUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class LevelMixin {

    @Shadow @Final protected WritableLevelData levelData;

    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("HEAD"))
    private void onBlockChanged(BlockPos pos, BlockState newState, int flags, int recursionLimit, CallbackInfoReturnable<Boolean> cir) {
        // Cast 'this' to Level to access its methods
        Level level = (Level) (Object) this;

        // Get the OLD block state before the change
        BlockState oldState = level.getBlockState(pos);

        // Check if this is a removal/replacement (not placement in empty air)
        if (!oldState.isAir() && oldState != newState && newState.isAir() && !level.isClientSide) {
            Block oldBlock = oldState.getBlock();

            // Noteblock
            if (oldBlock instanceof NoteBlock) {
                if (SpeakerLinkUtil.unlinkAllSpeakersNoteblock((ServerLevel) level, pos)) {
                    BetterMusicDiscs.noteblockLOGGING("(LevelMixin) - Unlinked all linked speakers from broken Noteblock!");
                    return;
                }
            }
            // Jukebox
            if (oldBlock instanceof JukeboxBlock) {
                if (SpeakerLinkUtil.unlinkAllSpeakersJukebox((ServerLevel) level, pos)) {
                    BetterMusicDiscs.jukeboxLOGGING("(LevelMixin) - Unlinked all linked speakers from broken Jukebox!");
                }
            }
            // Jukeblock
        //    if (oldBlock instanceof JukeblockBlock) {
        //        if (SpeakerLinkUtil.unlinkAllSpeakersJukeblock((ServerLevel) level, pos)) {
        //            BetterMusicDiscs.jukeblockLOGGING("(LevelMixin) - Unlinked all linked speakers from broken Jukeblock!");
        //        }
        //    }

            // Speakers
            if (oldBlock instanceof SpeakerBlock) {
                // Unlink Jukeblock
                if (SpeakerLinkUtil.isSpeakerLinked((ServerLevel) level, pos).equals("Jukebox")) {
                    SpeakerLinkUtil.unlinkSpeakerJukebox((ServerLevel) level, pos);
                }
                // Unlink Noteblock
                if (SpeakerLinkUtil.isSpeakerLinked((ServerLevel) level, pos).equals("Noteblock")) {
                    SpeakerLinkUtil.unlinkSpeakerNoteblock((ServerLevel) level, pos);
                }
        //        // Unlink Jukeblock
        //        if (SpeakerLinkUtil.isSpeakerLinked((ServerLevel) level, pos).equals("Jukeblock")) {
        //            SpeakerLinkUtil.unlinkSpeakerJukeblock((ServerLevel) level, pos);
        //        }
            }
        }
    }
}
