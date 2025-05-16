package net.ultimporks.betterdiscs.network.S2C;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.ultimporks.betterdiscs.client.SpeakerSoundEvent;

import java.util.Objects;

public class S2CSyncJukeboxSpeakersMessage extends PlayMessage<S2CSyncJukeboxSpeakersMessage> {

    private ItemStack currentDisc;
    private BlockPos blockPos;
    private float volume;

    public S2CSyncJukeboxSpeakersMessage() {}

    // Constructor for playing Jukebox
    public S2CSyncJukeboxSpeakersMessage(BlockPos speakerPos, ItemStack currentDisc, float volume) {
        this.blockPos = speakerPos;
        this.currentDisc = currentDisc;
        this.volume = volume;
    }

    @Override
    public void encode(S2CSyncJukeboxSpeakersMessage message, FriendlyByteBuf buf) {
        buf.writeBlockPos(message.blockPos);
        buf.writeItem(message.currentDisc);
        buf.writeFloat(message.volume);
    }

    @Override
    public S2CSyncJukeboxSpeakersMessage decode(FriendlyByteBuf buf) {
        return new S2CSyncJukeboxSpeakersMessage(buf.readBlockPos(), buf.readItem(), buf.readInt());

    }

    @Override
    public void handle(S2CSyncJukeboxSpeakersMessage message, MessageContext ctx) {
        if (!Objects.requireNonNull(ctx.getDirection()).isClient()) {
            return;
        }
        SpeakerSoundEvent.playSound(message.volume, message.blockPos, message.currentDisc.getItem());
        ctx.setHandled(true);

    }
}