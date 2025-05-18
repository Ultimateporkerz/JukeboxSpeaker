package net.ultimporks.betterdiscs.network.C2S;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.ultimporks.betterdiscs.block.entity.JukeblockBlockEntity;

import java.util.Objects;
import java.util.function.Supplier;

public class C2SSyncPlayButtonMessage {
    private final BlockPos jukeblockPos;

    public C2SSyncPlayButtonMessage(BlockPos jukeblockPos) {
        this.jukeblockPos = jukeblockPos;
    }

    public C2SSyncPlayButtonMessage(FriendlyByteBuf buf) {
        this.jukeblockPos = buf.readBlockPos();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(jukeblockPos);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            Level level = Objects.requireNonNull(context.get().getSender()).level();
            BlockEntity blockEntity = level.getExistingBlockEntity(jukeblockPos);
            if (blockEntity instanceof JukeblockBlockEntity jukeblockBlockEntity) {
                // Activate BlockEntity
                jukeblockBlockEntity.startPlaying();
            }
        });
        ctx.setPacketHandled(true);
    }
}
