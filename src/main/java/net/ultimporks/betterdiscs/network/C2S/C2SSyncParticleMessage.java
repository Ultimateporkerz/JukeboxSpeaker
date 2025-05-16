package net.ultimporks.betterdiscs.network.C2S;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.ultimporks.betterdiscs.block.entity.JukeblockBlockEntity;
import net.ultimporks.betterdiscs.block.entity.SpeakerBlockEntity;

public class C2SSyncParticleMessage {
    private boolean particlesEnabled;
    private BlockPos blockPos;

    public C2SSyncParticleMessage(boolean particlesEnabled, BlockPos blockPos) {
        this.particlesEnabled = particlesEnabled;
        this.blockPos = blockPos;
    }

    public C2SSyncParticleMessage (FriendlyByteBuf buf) {
        this.particlesEnabled = buf.readBoolean();
        this.blockPos = buf.readBlockPos();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(particlesEnabled);
        buf.writeBlockPos(blockPos);
    }

    public void handle(CustomPayloadEvent.Context context) {
        Player player = context.getSender();
        if (player != null) {
            BlockEntity blockEntity = player.level().getExistingBlockEntity(blockPos);
            if (blockEntity instanceof SpeakerBlockEntity speakerBlock) {
                speakerBlock.setParticlesEnabled(particlesEnabled);
            }

            if (blockEntity instanceof JukeblockBlockEntity jukeblockBlockEntity) {
                jukeblockBlockEntity.setParticlesEnabled(particlesEnabled);
            }
        }
    }
}
