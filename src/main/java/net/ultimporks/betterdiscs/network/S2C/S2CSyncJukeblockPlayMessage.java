package net.ultimporks.betterdiscs.network.S2C;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.ultimporks.betterdiscs.client.JukeblockSoundEvents;

import java.util.Objects;

public class S2CSyncJukeblockPlayMessage extends PlayMessage <S2CSyncJukeblockPlayMessage> {
    private BlockPos jukeblockOrSpeakerPos;
    private ItemStack currentDisc;
    private float volume;
    private boolean speakers;

    public S2CSyncJukeblockPlayMessage() {}

    // Constructor for playing Jukeblock
    public S2CSyncJukeblockPlayMessage(BlockPos jukeblockPos, ItemStack currentDisc, float volume) {
        this.jukeblockOrSpeakerPos = jukeblockPos;
        this.currentDisc = currentDisc;
        this.volume = volume;
        this.speakers = false;
    }

    // Constructor for playing Jukeblock Speakers
    public S2CSyncJukeblockPlayMessage(BlockPos speakerPos, ItemStack currentDisc, float volume, boolean isSpeaker) {
        this.jukeblockOrSpeakerPos = speakerPos;
        this.currentDisc = currentDisc;
        this.volume = volume;
        this.speakers = isSpeaker;
    }

    @Override
    public void encode(S2CSyncJukeblockPlayMessage message, FriendlyByteBuf buf) {
        buf.writeBlockPos(message.jukeblockOrSpeakerPos);
        buf.writeItemStack(message.currentDisc, false);
        buf.writeFloat(message.volume);
        buf.writeBoolean(message.speakers);
    }

    @Override
    public S2CSyncJukeblockPlayMessage decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        ItemStack disc = buf.readItem();
        float volume = buf.readFloat();
        boolean isSpeakers = buf.readBoolean();

        return new S2CSyncJukeblockPlayMessage(pos, disc, volume, isSpeakers);
    }

    @Override
    public void handle(S2CSyncJukeblockPlayMessage message, MessageContext context) {
        if (!Objects.requireNonNull(context.getDirection()).isClient()) {
            return;
        }

        if (!message.speakers) {
            JukeblockSoundEvents.playJukeblock(message.jukeblockOrSpeakerPos, message.currentDisc.getItem(), message.volume);
            context.setHandled(true);
        } else {
            JukeblockSoundEvents.playJukeblockSpeakers(message.jukeblockOrSpeakerPos, message.currentDisc.getItem(), message.volume);
            context.setHandled(true);
        }
    }
}
