package net.ultimporks.betterdiscs.network.S2C;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.ultimporks.betterdiscs.Reference;

public record S2CSyncJukeboxOrNoteblockStopMessage(BlockPos speakerPos, boolean isStoppingAll) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<S2CSyncJukeboxOrNoteblockStopMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "sync_jukebox_or_noteblock_stop_message"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSyncJukeboxOrNoteblockStopMessage> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, S2CSyncJukeboxOrNoteblockStopMessage::speakerPos,
                    ByteBufCodecs.BOOL, S2CSyncJukeboxOrNoteblockStopMessage::isStoppingAll,
                    S2CSyncJukeboxOrNoteblockStopMessage::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
