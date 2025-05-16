package net.ultimporks.betterdiscs.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.ultimporks.betterdiscs.BetterMusicDiscs;
import net.ultimporks.betterdiscs.util.SpeakerLinkUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JukeboxBlockEntity.class)
public abstract class JukeboxBlockEntityMixin {

    @Shadow public abstract ItemStack getItem(int pSlot);

    @Inject(method = "startPlaying", at = @At("RETURN"))
    private void startPlaying(CallbackInfo ci) {
        BetterMusicDiscs.jukeboxLOGGING("(JukeboxBlockEntityMixin) - Master Jukebox has started playing!");

        // Activate Linked speakers
        BlockPos jukeboxPos = ((JukeboxBlockEntity) (Object) this).getBlockPos();
        BlockEntity jukeboxEntity = ((JukeboxBlockEntity) (Object) this);
        if (jukeboxEntity.getLevel() instanceof ServerLevel serverLevel && !serverLevel.isClientSide) {
            SpeakerLinkUtil.activateSpeakersJukebox(serverLevel, jukeboxPos, getItem(0));
        }
    }

    @Inject(method = "stopPlaying", at = @At("RETURN"))
    private void onRecordStop(CallbackInfo ci) {
        BetterMusicDiscs.jukeboxLOGGING("(JukeboxBlockEntityMixin) - Master Jukebox has stopped playing!");

        // Deactivate linked speakers
        BlockPos jukeboxPos = ((JukeboxBlockEntity) (Object) this).getBlockPos();
        BlockEntity jukeboxEntity = ((JukeboxBlockEntity) (Object) this);
        if (jukeboxEntity.getLevel() instanceof ServerLevel serverLevel && !serverLevel.isClientSide) {
            SpeakerLinkUtil.deactivateSpeakersJukebox(serverLevel, jukeboxPos);
        }
    }

}
