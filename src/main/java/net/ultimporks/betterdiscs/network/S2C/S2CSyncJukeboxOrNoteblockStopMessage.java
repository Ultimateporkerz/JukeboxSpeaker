package net.ultimporks.betterdiscs.network.S2C;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.ultimporks.betterdiscs.client.SpeakerSoundEvent;

import java.util.function.Supplier;

public class S2CSyncJukeboxOrNoteblockStopMessage {
    private final BlockPos speakerPos;
    private final boolean isStoppingAll;

    public S2CSyncJukeboxOrNoteblockStopMessage(BlockPos speakerPos, boolean isStoppingAll) {
        this.speakerPos = speakerPos;
        this.isStoppingAll = isStoppingAll;
    }

    public S2CSyncJukeboxOrNoteblockStopMessage(FriendlyByteBuf buf) {
        this.speakerPos = buf.readBlockPos();
        this.isStoppingAll = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(speakerPos);
        buf.writeBoolean(isStoppingAll);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            if (isStoppingAll) {
                SpeakerSoundEvent.stopAllSounds();
            } else {
                SpeakerSoundEvent.stopSound(speakerPos);
            }
        });
        ctx.setPacketHandled(true);
    }
}
