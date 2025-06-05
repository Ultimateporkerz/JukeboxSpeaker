package net.ultimporks.betterdiscs.network.C2S;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.ultimporks.betterdiscs.Reference;

public record C2SSyncParticleMessage(BlockPos speakerPos, boolean particlesEnabled) implements CustomPacketPayload {
    public static final Type<C2SSyncParticleMessage> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "sync_particle_message"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SSyncParticleMessage> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, C2SSyncParticleMessage::speakerPos,
                    ByteBufCodecs.BOOL, C2SSyncParticleMessage::particlesEnabled,
                    C2SSyncParticleMessage::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}