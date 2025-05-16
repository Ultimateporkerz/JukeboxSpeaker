package net.ultimporks.betterdiscs.network.S2C;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.ultimporks.betterdiscs.client.JukeblockSoundEvents;

import java.util.Objects;

public class S2CSyncJukeblockStopMessage extends PlayMessage<S2CSyncJukeblockStopMessage> {
    private BlockPos blockPos;
    private boolean isStoppingAll;
    private boolean isSpeaker;

    public S2CSyncJukeblockStopMessage() {}

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

    @Override
    public void encode(S2CSyncJukeblockStopMessage message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.isSpeaker);
        buf.writeBlockPos(message.blockPos);
        buf.writeBoolean(message.isStoppingAll);
    }

    @Override
    public S2CSyncJukeblockStopMessage decode(FriendlyByteBuf buf) {
        boolean isSpeaker = buf.readBoolean();
        BlockPos blockPos = buf.readBlockPos();
        boolean isStoppingAll = buf.readBoolean();
        return new S2CSyncJukeblockStopMessage(blockPos, isSpeaker, isStoppingAll);
    }

    private S2CSyncJukeblockStopMessage(BlockPos blockPos, boolean isSpeaker, boolean isStoppingAll) {
        this.blockPos = blockPos;
        this.isStoppingAll = isStoppingAll;
        this.isSpeaker = isSpeaker;
    }

    @Override
    public void handle(S2CSyncJukeblockStopMessage message, MessageContext context) {
        if (!Objects.requireNonNull(context.getDirection()).isClient()) {
            return;
        }

        if (!message.isSpeaker) {
            JukeblockSoundEvents.stopJukeblockSound(message.blockPos);
        } else {
            if (message.isStoppingAll) {
                JukeblockSoundEvents.stopAllSpeakerSounds();
            } else {
                JukeblockSoundEvents.stopSpeakerSound(message.blockPos);
            }
        }
        context.setHandled(true);
    }
}
