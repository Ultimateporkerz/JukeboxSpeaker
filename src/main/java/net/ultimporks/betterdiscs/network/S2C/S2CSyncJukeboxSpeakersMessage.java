package net.ultimporks.betterdiscs.network.S2C;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.ultimporks.betterdiscs.client.SpeakerSoundEvent;

import java.util.function.Supplier;

public class S2CSyncJukeboxSpeakersMessage {
    private final ItemStack currentDisc;
    private final BlockPos blockPos;
    private final float volume;

    // Constructor for playing Jukebox
    public S2CSyncJukeboxSpeakersMessage(BlockPos speakerPos, ItemStack currentDisc, float volume) {
        this.currentDisc = currentDisc;
        this.blockPos = speakerPos;
        this.volume = volume;
    }

    public S2CSyncJukeboxSpeakersMessage(FriendlyByteBuf buf) {
        this.currentDisc = buf.readItem();
        this.blockPos = buf.readBlockPos();
        this.volume = buf.readFloat();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeItem(currentDisc);
        buf.writeBlockPos(blockPos);
        buf.writeFloat(volume);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() ->
                SpeakerSoundEvent.playSound(volume, blockPos, currentDisc.getItem()));
        ctx.setPacketHandled(true);
    }
}