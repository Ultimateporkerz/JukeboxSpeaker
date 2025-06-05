package net.ultimporks.betterdiscs.network.C2S;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import net.ultimporks.betterdiscs.Reference;

public record C2SSyncVolumeMessage(BlockPos blockPos, int volume) implements CustomPacketPayload {
    public static final Type<C2SSyncVolumeMessage> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "sync_volume_message"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SSyncVolumeMessage> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, C2SSyncVolumeMessage::blockPos,
                    ByteBufCodecs.INT, C2SSyncVolumeMessage::volume,
                    C2SSyncVolumeMessage::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
