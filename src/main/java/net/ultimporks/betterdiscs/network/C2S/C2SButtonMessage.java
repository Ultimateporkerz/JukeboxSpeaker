package net.ultimporks.betterdiscs.network.C2S;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.ultimporks.betterdiscs.block.entity.JukeblockBlockEntity;

public class C2SButtonMessage {
    private final BlockPos jukeblockPos;
    private final String buttonName;

    public C2SButtonMessage(BlockPos jukeblockPos, String buttonName) {
        this.jukeblockPos = jukeblockPos;
        this.buttonName = buttonName;
    }

    public C2SButtonMessage(FriendlyByteBuf buf) {
        this.jukeblockPos = buf.readBlockPos();
        this.buttonName = buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(jukeblockPos);
        buf.writeUtf(buttonName);
    }

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            BlockPos jukeblockPos = this.jukeblockPos;
            Level level = context.getSender().level();
            BlockEntity blockEntity = level.getBlockEntity(jukeblockPos);
            String buttonName = this.buttonName;

            if (blockEntity instanceof JukeblockBlockEntity jukeblockBlock) {
                if (buttonName.equals("PlayButton")) {
                    jukeblockBlock.startPlaying(false, false);
                }

                if (buttonName.equals("StopButton")) {
                    jukeblockBlock.stopPlaying(false);
                }

                if (buttonName.equals("NextButton")) {
                    jukeblockBlock.nextTrack();
                }

                if (buttonName.equals("RewindButton")) {
                    jukeblockBlock.previousTrack();
                }
            }
        });
    }
}
