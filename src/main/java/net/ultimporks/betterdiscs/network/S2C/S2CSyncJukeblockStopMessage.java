package net.ultimporks.betterdiscs.network.S2C;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.ultimporks.betterdiscs.client.JukeblockSoundEvents;

public class S2CSyncJukeblockStopMessage {
    private final BlockPos blockPos;
    private final boolean isStoppingAll;
    private final boolean isSpeaker;

    // Jukeblock Constructor
    public S2CSyncJukeblockStopMessage(BlockPos jukeblockPos) {
        this.blockPos = jukeblockPos;
        this.isStoppingAll = false;
        this.isSpeaker = false;
    }

    // Speaker Constructor
    public S2CSyncJukeblockStopMessage(BlockPos speakerPos, boolean isStoppingAll) {
        this.blockPos = speakerPos;
        this.isStoppingAll = isStoppingAll;
        this.isSpeaker = true;
    }

    public S2CSyncJukeblockStopMessage (FriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.isStoppingAll = buf.readBoolean();
        this.isSpeaker = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(isSpeaker);
        buf.writeBlockPos(blockPos);
        buf.writeBoolean(isStoppingAll);
    }

    public void handle(CustomPayloadEvent.Context context) {
        if (isSpeaker) {
            JukeblockSoundEvents.stopJukeblockSound(blockPos);
            context.setPacketHandled(true);
        } else {
            if (isStoppingAll) {
                JukeblockSoundEvents.stopAllSpeakerSounds();
                context.setPacketHandled(true);
            } else {
                JukeblockSoundEvents.stopSpeakerSound(blockPos);
                context.setPacketHandled(true);
            }
        }
    }
}
