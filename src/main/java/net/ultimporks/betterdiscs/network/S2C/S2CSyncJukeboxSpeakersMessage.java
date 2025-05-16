package net.ultimporks.betterdiscs.network.S2C;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.ultimporks.betterdiscs.client.SpeakerSoundEvent;

public class S2CSyncJukeboxSpeakersMessage {
    private ItemStack currentDisc;
    private BlockPos blockPos;
    private float volume;


    // Constructor for playing Jukebox
    public S2CSyncJukeboxSpeakersMessage(BlockPos speakerPos, ItemStack currentDisc, float volume) {
        this.blockPos = speakerPos;
        this.currentDisc = currentDisc;
        this.volume = volume;
    }

    public S2CSyncJukeboxSpeakersMessage (FriendlyByteBuf buf) {
        this.currentDisc = buf.readItemStack();
        this.blockPos = buf.readBlockPos();
        this.volume = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeItem(currentDisc);
        buf.writeFloat(volume);
    }

    public void handle(CustomPayloadEvent.Context context) {
        SpeakerSoundEvent.playSound(volume, blockPos, currentDisc.getItem());
    }
}