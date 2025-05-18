package net.ultimporks.betterdiscs.mixins;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
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

        JukeboxBlockEntity jukebox = (JukeboxBlockEntity) (Object) this;
        ServerLevel serverLevel = jukebox.getLevel() instanceof ServerLevel s && !s.isClientSide ? s : null;

        if (serverLevel != null) {
            ItemStack record = getItem(0);
            if (!record.isEmpty()) {
                // Send the packet to the client
                SpeakerLinkUtil.activateSpeakersJukebox(serverLevel, jukebox.getBlockPos(), record);
            }
        }
    }

    @Inject(method = "stopPlaying", at = @At("RETURN"))
    private void onRecordStop(CallbackInfo ci) {
        BetterMusicDiscs.jukeboxLOGGING("(JukeboxBlockEntityMixin) - Master Jukebox has stopped playing!");

        JukeboxBlockEntity jukebox = (JukeboxBlockEntity) (Object) this;
        ServerLevel serverLevel = jukebox.getLevel() instanceof ServerLevel s && !s.isClientSide ? s : null;

        if (serverLevel != null) {
            // Send the packet to the client
            SpeakerLinkUtil.deactivateSpeakersJukebox(serverLevel, jukebox.getBlockPos());
        }
    }
}
