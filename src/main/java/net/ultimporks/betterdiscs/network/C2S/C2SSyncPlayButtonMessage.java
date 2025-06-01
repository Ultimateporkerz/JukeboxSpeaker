package net.ultimporks.betterdiscs.network.C2S;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;
import java.util.Objects;

public class C2SSyncPlayButtonMessage {
    private final BlockPos jukeblockPos;

    public C2SSyncPlayButtonMessage(BlockPos jukeblockPos) {
        this.jukeblockPos = jukeblockPos;
    }

    public C2SSyncPlayButtonMessage (FriendlyByteBuf buf) {
        this.jukeblockPos = buf.readBlockPos();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(jukeblockPos);
    }

    public void handle(CustomPayloadEvent.Context context) {
        Level level = Objects.requireNonNull(context.getSender()).level();
        BlockEntity blockEntity = level.getExistingBlockEntity(jukeblockPos);
    //    if (blockEntity instanceof JukeblockBlockEntity jukeblockBlockEntity) {
            // Activate BlockEntity
    //        jukeblockBlockEntity.startPlaying();
            context.setPacketHandled(true);
    //    }
    }
}
