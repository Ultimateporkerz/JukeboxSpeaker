package net.ultimporks.betterdiscs.network.S2C;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.ultimporks.betterdiscs.Reference;

public record S2CSyncJukeblockStopMessage(BlockPos jukeblockPos, boolean isStoppingAll, boolean isSpeaker) implements CustomPacketPayload {
    public static final Type<S2CSyncJukeblockStopMessage> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "sync_jukeblock_stop_message"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSyncJukeblockStopMessage> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, S2CSyncJukeblockStopMessage::jukeblockPos,
                    ByteBufCodecs.BOOL, S2CSyncJukeblockStopMessage::isStoppingAll,
                    ByteBufCodecs.BOOL, S2CSyncJukeblockStopMessage::isSpeaker,
                    S2CSyncJukeblockStopMessage::new
            );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}