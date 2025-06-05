package net.ultimporks.betterdiscs.network.S2C;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.ultimporks.betterdiscs.Reference;

public record S2CSyncJukeblockPlayMessage(BlockPos jukeblockOrSpeakerPos, ItemStack currentDisc, float volume, boolean speakers) implements CustomPacketPayload {
    public static final Type<S2CSyncJukeblockPlayMessage> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "sync_jukeblock_play_message"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CSyncJukeblockPlayMessage> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, S2CSyncJukeblockPlayMessage::jukeblockOrSpeakerPos,
                    ItemStack.STREAM_CODEC, S2CSyncJukeblockPlayMessage::currentDisc,
                    ByteBufCodecs.FLOAT, S2CSyncJukeblockPlayMessage::volume,
                    ByteBufCodecs.BOOL, S2CSyncJukeblockPlayMessage::speakers,
                    S2CSyncJukeblockPlayMessage::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
