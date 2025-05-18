package net.ultimporks.betterdiscs.network.S2C;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.ultimporks.betterdiscs.client.JukeblockSoundEvents;

import java.util.function.Supplier;

public class S2CSyncJukeblockPlayMessage {
    private final BlockPos jukeblockOrSpeakerPos;
    private final ItemStack currentDisc;
    private final float volume;
    private final boolean speakers;

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

    public S2CSyncJukeblockPlayMessage(FriendlyByteBuf buf) {
        this.jukeblockOrSpeakerPos = buf.readBlockPos();
        this.currentDisc = buf.readItem();
        this.volume = buf.readFloat();
        this.speakers = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(jukeblockOrSpeakerPos);
        buf.writeItemStack(currentDisc, false);
        buf.writeFloat(volume);
        buf.writeBoolean(speakers);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
           if (!speakers) {
               JukeblockSoundEvents.playJukeblock(jukeblockOrSpeakerPos, currentDisc.getItem(), volume);
           } else {
               JukeblockSoundEvents.playJukeblockSpeakers(jukeblockOrSpeakerPos, currentDisc.getItem(), volume);
           }
        });
        ctx.setPacketHandled(true);
    }
}
