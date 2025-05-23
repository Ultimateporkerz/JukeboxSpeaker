package net.ultimporks.betterdiscs.network.S2C;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.ultimporks.betterdiscs.client.SpeakerSoundEvent;

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

    public S2CSyncJukeboxSpeakersMessage (FriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();
        this.currentDisc = ItemStack.CODEC.parse(NbtOps.INSTANCE, tag)
                .resultOrPartial(error -> System.err.println("Failed to decode ItemStack: " + error))
                .orElse(ItemStack.EMPTY);
        this.blockPos = buf.readBlockPos();
        this.volume = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, currentDisc)
                .getOrThrow());
        buf.writeBlockPos(blockPos);
        buf.writeFloat(volume);
    }

    public void handle(CustomPayloadEvent.Context context) {
        SpeakerSoundEvent.playSound(volume, blockPos, currentDisc);
        context.setPacketHandled(true);
    }
}