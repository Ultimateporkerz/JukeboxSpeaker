package net.ultimporks.betterdiscs.network.C2S;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.ultimporks.betterdiscs.Reference;

public record C2SParticleMessage(BlockPos speakerPos, boolean particlesEnabled) implements CustomPacketPayload {
    public static final Type<C2SParticleMessage> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "sync_particle_message"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SParticleMessage> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, C2SParticleMessage::speakerPos,
                    ByteBufCodecs.BOOL, C2SParticleMessage::particlesEnabled,
                    C2SParticleMessage::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}