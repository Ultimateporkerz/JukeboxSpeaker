package net.ultimporks.betterdiscs.network.C2S;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.ultimporks.betterdiscs.block.entity.JukeblockBlockEntity;
import net.ultimporks.betterdiscs.block.entity.SpeakerBlockEntity;

import java.util.function.Supplier;

public class C2SSyncVolumeMessage {
    private final int volume;
    private final BlockPos blockPos;

    public C2SSyncVolumeMessage(int volume, BlockPos blockPos) {
        this.volume = volume;
        this.blockPos = blockPos;
    }

    public C2SSyncVolumeMessage(FriendlyByteBuf buf) {
        this.volume = buf.readInt();
        this.blockPos = buf.readBlockPos();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(volume);
        buf.writeBlockPos(blockPos);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            Player player = context.get().getSender();
            if (player != null) {
                BlockEntity blockEntity = player.level().getExistingBlockEntity(blockPos);
                if (blockEntity instanceof SpeakerBlockEntity speakerBlock) {
                    speakerBlock.setVolume(volume);
                } else if (blockEntity instanceof JukeblockBlockEntity jukeblockBlock) {
                    jukeblockBlock.setVolume(volume);
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
