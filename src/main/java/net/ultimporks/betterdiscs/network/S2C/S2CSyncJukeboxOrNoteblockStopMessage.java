package net.ultimporks.betterdiscs.network.S2C;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.ultimporks.betterdiscs.client.SpeakerSoundEvent;

import java.util.Objects;

public class S2CSyncJukeboxOrNoteblockStopMessage extends PlayMessage<S2CSyncJukeboxOrNoteblockStopMessage> {
    private BlockPos speakerPos;
    private boolean isStoppingAll;

    public S2CSyncJukeboxOrNoteblockStopMessage() {}

    public S2CSyncJukeboxOrNoteblockStopMessage(BlockPos speakerPos, boolean isStoppingAll) {
        this.speakerPos = speakerPos;
        this.isStoppingAll = isStoppingAll;
    }

    @Override
    public void encode(S2CSyncJukeboxOrNoteblockStopMessage message, FriendlyByteBuf buf) {
        buf.writeBlockPos(message.speakerPos);
        buf.writeBoolean(message.isStoppingAll);
    }

    @Override
    public S2CSyncJukeboxOrNoteblockStopMessage decode(FriendlyByteBuf buf) {
        BlockPos speakerPos = buf.readBlockPos();
        boolean isStoppingAll = buf.readBoolean();
        return new S2CSyncJukeboxOrNoteblockStopMessage(speakerPos, isStoppingAll);
    }

    @Override
    public void handle(S2CSyncJukeboxOrNoteblockStopMessage message, MessageContext ctx) {
        if (Objects.requireNonNull(ctx.getDirection()).isClient()) {
                if (message.isStoppingAll) {
                    SpeakerSoundEvent.stopAllSounds();
                } else {
                    SpeakerSoundEvent.stopSound(message.speakerPos);
                }
            }
        ctx.setHandled(true);
    }
}
