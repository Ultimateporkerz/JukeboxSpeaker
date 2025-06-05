package net.ultimporks.betterdiscs.network.S2C;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.ultimporks.betterdiscs.Reference;

public record S2CSyncNoteblockSpeakersMessage(BlockPos speakerPos, String instrumentName, int note, float volume) implements CustomPacketPayload {
    public static final Type<S2CSyncNoteblockSpeakersMessage> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "sync_noteblock_speaker_message"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSyncNoteblockSpeakersMessage> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, S2CSyncNoteblockSpeakersMessage::speakerPos,
                    ByteBufCodecs.STRING_UTF8, S2CSyncNoteblockSpeakersMessage::instrumentName,
                    ByteBufCodecs.INT, S2CSyncNoteblockSpeakersMessage::note,
                    ByteBufCodecs.FLOAT, S2CSyncNoteblockSpeakersMessage::volume,
                    S2CSyncNoteblockSpeakersMessage::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
