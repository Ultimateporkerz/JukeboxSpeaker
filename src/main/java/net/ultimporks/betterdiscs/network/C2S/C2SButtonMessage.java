package net.ultimporks.betterdiscs.network.C2S;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.ultimporks.betterdiscs.Reference;

public record C2SButtonMessage(BlockPos jukeblockPos, String buttonName) implements CustomPacketPayload {
    public static final Type<C2SButtonMessage> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "sync_button_message"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SButtonMessage> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, C2SButtonMessage::jukeblockPos,
                    ByteBufCodecs.STRING_UTF8, C2SButtonMessage::buttonName,
                    C2SButtonMessage::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}