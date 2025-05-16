package net.ultimporks.betterdiscs.network.C2S;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.ultimporks.betterdiscs.block.entity.JukeblockBlockEntity;
import net.ultimporks.betterdiscs.block.entity.SpeakerBlockEntity;

public class C2SSyncParticleMessage extends PlayMessage<C2SSyncParticleMessage> {
    private boolean particlesEnabled;
    private BlockPos blockPos;

    public C2SSyncParticleMessage() {}

    public C2SSyncParticleMessage(boolean particlesEnabled, BlockPos blockPos) {
        this.particlesEnabled = particlesEnabled;
        this.blockPos = blockPos;
    }

    @Override
    public void encode(C2SSyncParticleMessage message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.particlesEnabled);
        buf.writeBlockPos(message.blockPos);
    }

    @Override
    public C2SSyncParticleMessage decode(FriendlyByteBuf buf) {
        return new C2SSyncParticleMessage(buf.readBoolean(), buf.readBlockPos());
    }

    @Override
    public void handle(C2SSyncParticleMessage packet, MessageContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            BlockEntity blockEntity = player.level().getExistingBlockEntity(packet.blockPos);
            if (blockEntity instanceof SpeakerBlockEntity speakerBlock) {
                speakerBlock.setParticlesEnabled(packet.particlesEnabled);
            }

            if (blockEntity instanceof JukeblockBlockEntity jukeblockBlockEntity) {
                jukeblockBlockEntity.setParticlesEnabled(packet.particlesEnabled);
            }
        }
        context.setHandled(true);
    }
}
