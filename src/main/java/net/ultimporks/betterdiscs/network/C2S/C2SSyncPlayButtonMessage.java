package net.ultimporks.betterdiscs.network.C2S;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.ultimporks.betterdiscs.block.entity.JukeblockBlockEntity;

public class C2SSyncPlayButtonMessage extends PlayMessage<C2SSyncPlayButtonMessage> {
    private BlockPos jukeblockPos;

    public C2SSyncPlayButtonMessage() {}

    public C2SSyncPlayButtonMessage(BlockPos jukeblockPos) {
        this.jukeblockPos = jukeblockPos;
    }

    @Override
    public void encode(C2SSyncPlayButtonMessage message, FriendlyByteBuf buf) {
        buf.writeBlockPos(message.jukeblockPos);
    }

    @Override
    public C2SSyncPlayButtonMessage decode(FriendlyByteBuf buf) {
        return new C2SSyncPlayButtonMessage(buf.readBlockPos());
    }

    @Override
    public void handle(C2SSyncPlayButtonMessage message, MessageContext context) {
        Level level = context.getPlayer().level();
        BlockEntity blockEntity = level.getExistingBlockEntity(message.jukeblockPos);
        if (blockEntity instanceof JukeblockBlockEntity jukeblockBlockEntity) {
            // Activate BlockEntity
            jukeblockBlockEntity.startPlaying();
            context.setHandled(true);
        }
    }
}
