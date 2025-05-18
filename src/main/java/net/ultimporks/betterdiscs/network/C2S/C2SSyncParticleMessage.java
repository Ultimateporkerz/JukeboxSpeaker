package net.ultimporks.betterdiscs.network.C2S;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.ultimporks.betterdiscs.block.entity.JukeblockBlockEntity;
import net.ultimporks.betterdiscs.block.entity.SpeakerBlockEntity;

import java.util.function.Supplier;

public class C2SSyncParticleMessage {
    private final boolean particlesEnabled;
    private final BlockPos blockPos;

    public C2SSyncParticleMessage(boolean particlesEnabled, BlockPos blockPos) {
        this.particlesEnabled = particlesEnabled;
        this.blockPos = blockPos;
    }

    public C2SSyncParticleMessage(FriendlyByteBuf buf) {
        this.particlesEnabled = buf.readBoolean();
        this.blockPos = buf.readBlockPos();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(particlesEnabled);
        buf.writeBlockPos(blockPos);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            Player player = context.get().getSender();
            if (player != null) {
                BlockEntity blockEntity = player.level().getExistingBlockEntity(blockPos);
                if (blockEntity instanceof SpeakerBlockEntity speakerBlock) {
                    speakerBlock.setParticlesEnabled(particlesEnabled);
                }
                if (blockEntity instanceof JukeblockBlockEntity jukeblockBlockEntity) {
                    jukeblockBlockEntity.setParticlesEnabled(particlesEnabled);
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
