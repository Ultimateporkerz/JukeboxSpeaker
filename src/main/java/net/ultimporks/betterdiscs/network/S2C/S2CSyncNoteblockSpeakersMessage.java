package net.ultimporks.betterdiscs.network.S2C;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.ultimporks.betterdiscs.client.SpeakerSoundEvent;

import java.util.Objects;

public class S2CSyncNoteblockSpeakersMessage extends PlayMessage<S2CSyncNoteblockSpeakersMessage> {
    private BlockPos speakerPos;
    private String instrumentName;
    private int note;
    private float volume;

    public S2CSyncNoteblockSpeakersMessage() {}

    public S2CSyncNoteblockSpeakersMessage(BlockPos speakerPos, String instrumentName, int note, float volume) {
        this.speakerPos = speakerPos;
        this.instrumentName = instrumentName;
        this.note = note;
        this.volume = volume;
    }


    @Override
    public void encode(S2CSyncNoteblockSpeakersMessage message, FriendlyByteBuf buf) {
        buf.writeBlockPos(message.speakerPos);
        buf.writeUtf(message.instrumentName);
        buf.writeInt(message.note);
        buf.writeFloat(message.volume);
    }

    @Override
    public S2CSyncNoteblockSpeakersMessage decode(FriendlyByteBuf buf) {
        return new S2CSyncNoteblockSpeakersMessage(buf.readBlockPos(), buf.readUtf(), buf.readInt(), buf.readFloat());
    }

    @Override
    public void handle(S2CSyncNoteblockSpeakersMessage message, MessageContext ctx) {
        if (!Objects.requireNonNull(ctx.getDirection()).isClient()) {
            return;
        }
        SpeakerSoundEvent.playNoteBlock(message.speakerPos, message.instrumentName, message.note, message.volume);
        ctx.setHandled(true);
    }
}
