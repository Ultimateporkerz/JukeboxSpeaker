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


    @Inject(method = "setTheItem", at = @At("RETURN"))
    private void setRecordItem(ItemStack pItem, CallbackInfo ci) {
        JukeboxBlockEntity self = (JukeboxBlockEntity)(Object) this;
        BlockPos pos = self.getBlockPos();
        boolean hasItem = !pItem.isEmpty();

        if (!self.getLevel().isClientSide && self.getLevel() instanceof ServerLevel serverLevel) {
            if (hasItem) {
                BetterMusicDiscs.jukeboxLOGGING("JukeboxBlockEntityMixin - Inserted item: " + pItem.getItem());
                SpeakerLinkUtil.activateSpeakersJukebox(serverLevel, pos, pItem);
            } else {
                BetterMusicDiscs.jukeboxLOGGING("JukeboxBlockEntityMixin - Item removed, stopping speakers");
                SpeakerLinkUtil.deactivateSpeakersJukebox(serverLevel, pos);
            }
        }
    }






}
