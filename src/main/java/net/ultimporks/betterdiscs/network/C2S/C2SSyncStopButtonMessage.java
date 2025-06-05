package net.ultimporks.betterdiscs.network.C2S;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.ultimporks.betterdiscs.Reference;

public record C2SSyncStopButtonMessage(BlockPos jukeblockPos) implements CustomPacketPayload {
    public static final Type<C2SSyncStopButtonMessage> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "sync_stop_button_message"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SSyncStopButtonMessage> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, C2SSyncStopButtonMessage::jukeblockPos,
                    C2SSyncStopButtonMessage::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}