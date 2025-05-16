package net.ultimporks.betterdiscs.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.BlockHitResult;
import net.ultimporks.betterdiscs.BetterMusicDiscs;
import net.ultimporks.betterdiscs.item.TuningTool;
import net.ultimporks.betterdiscs.util.SpeakerLinkUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoteBlock.class)
public class NoteBlockMixin {

    @Shadow @Final public static EnumProperty<NoteBlockInstrument> INSTRUMENT;

    @Shadow @Final public static IntegerProperty NOTE;

    @Inject(method = "playNote", at = @At("RETURN"))
    private void playNote(Entity pEntity, BlockState pState, Level pLevel, BlockPos pPos, CallbackInfo ci) {
        NoteBlockInstrument instrument = pState.getValue(INSTRUMENT);
        String instrumentName = instrument.getSerializedName();
        int note = pState.getValue(NOTE);
        SpeakerLinkUtil.activateSpeakersNoteblock(pLevel, pPos, note, instrumentName);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit, CallbackInfoReturnable<InteractionResult> cir) {
        if (pPlayer.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof TuningTool) {
            cir.setReturnValue(InteractionResult.FAIL);
            cir.cancel();
        }
    }





}
