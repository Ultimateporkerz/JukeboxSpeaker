package net.ultimporks.betterdiscs.network.S2C;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.ultimporks.betterdiscs.Reference;

public record S2CSyncJukeboxSpeakersMessage(ItemStack currentDisc, BlockPos blockPos, float volume) implements CustomPacketPayload {
    public static final Type<S2CSyncJukeboxSpeakersMessage> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "sync_jukebox_speakers_message"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSyncJukeboxSpeakersMessage> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStack.STREAM_CODEC, S2CSyncJukeboxSpeakersMessage::currentDisc,
                    BlockPos.STREAM_CODEC, S2CSyncJukeboxSpeakersMessage::blockPos,
                    ByteBufCodecs.FLOAT, S2CSyncJukeboxSpeakersMessage::volume,
                    S2CSyncJukeboxSpeakersMessage::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}