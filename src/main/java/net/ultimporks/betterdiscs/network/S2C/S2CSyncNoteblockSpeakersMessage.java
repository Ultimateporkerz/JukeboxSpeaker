package net.ultimporks.betterdiscs.network.S2C;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.ultimporks.betterdiscs.client.SpeakerSoundEvent;

public class S2CSyncNoteblockSpeakersMessage {
    private final BlockPos speakerPos;
    private final String instrumentName;
    private final int note;
    private final float volume;

    public S2CSyncNoteblockSpeakersMessage(BlockPos speakerPos, String instrumentName, int note, float volume) {
        this.speakerPos = speakerPos;
        this.instrumentName = instrumentName;
        this.note = note;
        this.volume = volume;
    }

    public S2CSyncNoteblockSpeakersMessage (FriendlyByteBuf buf) {
        this.speakerPos = buf.readBlockPos();
        this.instrumentName = buf.readUtf();
        this.note = buf.readInt();
        this.volume = buf.readFloat();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(speakerPos);
        buf.writeUtf(instrumentName);
        buf.writeInt(note);
        buf.writeFloat(volume);
    }

    public void handle(CustomPayloadEvent.Context context) {
        SpeakerSoundEvent.playNoteBlock(speakerPos, instrumentName, note, volume);
        context.setPacketHandled(true);
    }
}
