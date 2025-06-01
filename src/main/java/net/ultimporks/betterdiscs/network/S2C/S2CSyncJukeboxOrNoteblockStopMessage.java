package net.ultimporks.betterdiscs.network.S2C;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.ultimporks.betterdiscs.client.SpeakerSoundEvent;

public class S2CSyncJukeboxOrNoteblockStopMessage {
    private final BlockPos speakerPos;
    private final boolean isStoppingAll;

    public S2CSyncJukeboxOrNoteblockStopMessage(BlockPos speakerPos, boolean isStoppingAll) {
        this.speakerPos = speakerPos;
        this.isStoppingAll = isStoppingAll;
    }

    public S2CSyncJukeboxOrNoteblockStopMessage (FriendlyByteBuf buf) {
        this.speakerPos = buf.readBlockPos();
        this.isStoppingAll = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(speakerPos);
        buf.writeBoolean(isStoppingAll);
    }

    public void handle(CustomPayloadEvent.Context context) {
        if (isStoppingAll) {
            SpeakerSoundEvent.stopAllSounds();
            context.setPacketHandled(true);
        } else {
            SpeakerSoundEvent.stopSound(speakerPos);
            context.setPacketHandled(true);
        }
    }
}
