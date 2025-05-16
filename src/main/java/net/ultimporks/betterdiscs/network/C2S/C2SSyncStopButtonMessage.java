package net.ultimporks.betterdiscs.network.C2S;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.ultimporks.betterdiscs.block.entity.JukeblockBlockEntity;
import net.ultimporks.betterdiscs.util.SpeakerLinkUtil;

public class C2SSyncStopButtonMessage extends PlayMessage<C2SSyncStopButtonMessage> {
    private BlockPos jukeblockPos;

    public C2SSyncStopButtonMessage() {}


    public C2SSyncStopButtonMessage(BlockPos jukeblockPos) {
        this.jukeblockPos = jukeblockPos;
    }

    @Override
    public void encode(C2SSyncStopButtonMessage message, FriendlyByteBuf buf) {
        buf.writeBlockPos(message.jukeblockPos);
    }

    @Override
    public C2SSyncStopButtonMessage decode(FriendlyByteBuf buf) {
        return new C2SSyncStopButtonMessage(buf.readBlockPos());
    }

    @Override
    public void handle(C2SSyncStopButtonMessage message, MessageContext context) {
        Level level = context.getPlayer().level();
        BlockEntity blockEntity = level.getExistingBlockEntity(message.jukeblockPos);
        if (blockEntity instanceof JukeblockBlockEntity jukeblockBlockEntity) {
            // Stop the BlockEntity from playing
            jukeblockBlockEntity.setStopped();
            context.setHandled(true);
        }
    }
}
