package net.ultimporks.betterdiscs.network.C2S;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import net.ultimporks.betterdiscs.Reference;

public record C2SVolumeMessage(BlockPos blockPos, int volume) implements CustomPacketPayload {
    public static final Type<C2SVolumeMessage> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "sync_volume_message"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SVolumeMessage> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, C2SVolumeMessage::blockPos,
                    ByteBufCodecs.INT, C2SVolumeMessage::volume,
                    C2SVolumeMessage::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
