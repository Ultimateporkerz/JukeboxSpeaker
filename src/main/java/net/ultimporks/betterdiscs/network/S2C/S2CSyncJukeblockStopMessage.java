package net.ultimporks.betterdiscs.network.S2C;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.ultimporks.betterdiscs.client.JukeblockSoundEvents;

import java.util.function.Supplier;

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

    public S2CSyncJukeblockStopMessage(FriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.isStoppingAll = buf.readBoolean();
        this.isSpeaker = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeBoolean(isStoppingAll);
        buf.writeBoolean(isSpeaker);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            if (!isSpeaker) {
                JukeblockSoundEvents.stopJukeblockSound(blockPos);
            } else {
                if (isStoppingAll) {
                    JukeblockSoundEvents.stopAllSpeakerSounds();
                } else {
                    JukeblockSoundEvents.stopSpeakerSound(blockPos);
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
