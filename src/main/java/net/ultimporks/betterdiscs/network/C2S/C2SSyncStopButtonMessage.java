package net.ultimporks.betterdiscs.network.C2S;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.ultimporks.betterdiscs.block.entity.JukeblockBlockEntity;

import java.util.Objects;

public class C2SSyncStopButtonMessage {
    private BlockPos jukeblockPos;

    public C2SSyncStopButtonMessage(BlockPos jukeblockPos) {
        this.jukeblockPos = jukeblockPos;
    }

    public C2SSyncStopButtonMessage (FriendlyByteBuf buf) {
        this.jukeblockPos = buf.readBlockPos();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(jukeblockPos);
    }

    public void handle(CustomPayloadEvent.Context context) {
        Level level = Objects.requireNonNull(context.getSender()).level();
        BlockEntity blockEntity = level.getExistingBlockEntity(jukeblockPos);
        if (blockEntity instanceof JukeblockBlockEntity jukeblockBlockEntity) {
            // Stop the BlockEntity from playing
            jukeblockBlockEntity.setStopped();
        }
    }
}
