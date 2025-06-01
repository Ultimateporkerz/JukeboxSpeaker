package net.ultimporks.betterdiscs.network.C2S;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.ultimporks.betterdiscs.block.entity.SpeakerBlockEntity;

public class C2SSyncVolumeMessage {
    private final int volume;
    private final BlockPos blockPos;

    public C2SSyncVolumeMessage(int volume, BlockPos blockPos) {
        this.volume = volume;
        this.blockPos = blockPos;
    }

    public C2SSyncVolumeMessage (FriendlyByteBuf buf) {
        this.volume = buf.readInt();
        this.blockPos = buf.readBlockPos();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(volume);
        buf.writeBlockPos(blockPos);
    }

    public void handle(CustomPayloadEvent.Context context) {
        Player player = context.getSender();
        if (player != null) {
            BlockEntity blockEntity = player.level().getExistingBlockEntity(blockPos);
            if (blockEntity instanceof SpeakerBlockEntity speakerBlock) {
                speakerBlock.setVolume(volume);
                context.setPacketHandled(true);
        //    } else if (blockEntity instanceof JukeblockBlockEntity jukeblockBlock) {
        //        jukeblockBlock.setVolume(volume);
        //        context.setPacketHandled(true);
            }
        }
    }
}
