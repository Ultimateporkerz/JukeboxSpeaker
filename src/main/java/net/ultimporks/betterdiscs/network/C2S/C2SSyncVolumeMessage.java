package net.ultimporks.betterdiscs.network.C2S;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.ultimporks.betterdiscs.block.entity.JukeblockBlockEntity;
import net.ultimporks.betterdiscs.block.entity.SpeakerBlockEntity;

public class C2SSyncVolumeMessage extends PlayMessage<C2SSyncVolumeMessage> {
    private int volume;
    private BlockPos blockPos;

    public C2SSyncVolumeMessage() {}

    public C2SSyncVolumeMessage(int volume, BlockPos blockPos) {
        this.volume = volume;
        this.blockPos = blockPos;
    }

    @Override
    public void encode(C2SSyncVolumeMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.volume);
        buf.writeBlockPos(message.blockPos);
    }

    @Override
    public C2SSyncVolumeMessage decode(FriendlyByteBuf buf) {
        return new C2SSyncVolumeMessage(buf.readInt(), buf.readBlockPos());
    }

    @Override
    public void handle(C2SSyncVolumeMessage packet, MessageContext messageContext) {
        Player player = messageContext.getPlayer();
        if (player != null) {
            BlockEntity blockEntity = player.level().getExistingBlockEntity(packet.blockPos);
            if (blockEntity instanceof SpeakerBlockEntity speakerBlock) {
                speakerBlock.setVolume(packet.volume);
            } else if (blockEntity instanceof JukeblockBlockEntity jukeblockBlock) {
                jukeblockBlock.setVolume(packet.volume);
            }
        }
        messageContext.setHandled(true);
    }
}
