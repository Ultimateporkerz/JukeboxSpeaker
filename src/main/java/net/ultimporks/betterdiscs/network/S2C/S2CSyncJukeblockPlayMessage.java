package net.ultimporks.betterdiscs.network.S2C;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.ultimporks.betterdiscs.client.JukeblockSoundEvents;

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
        CompoundTag tag = buf.readNbt();
        this.currentDisc = ItemStack.CODEC.parse(NbtOps.INSTANCE, tag)
                .resultOrPartial(error -> System.err.println("Failed to decode ItemStack: " + error))
                .orElse(ItemStack.EMPTY);
        this.volume = buf.readFloat();
        this.speakers = buf.readBoolean();
    }


    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(jukeblockOrSpeakerPos);
        buf.writeNbt(ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, currentDisc)
                .getOrThrow());
        buf.writeFloat(volume);
        buf.writeBoolean(speakers);
    }


    public void handle(CustomPayloadEvent.Context context) {
        if (speakers) {
            JukeblockSoundEvents.playJukeblock(jukeblockOrSpeakerPos, currentDisc, volume);
            context.setPacketHandled(true);
        } else {
            JukeblockSoundEvents.playJukeblockSpeakers(jukeblockOrSpeakerPos, currentDisc, volume);
            context.setPacketHandled(true);
        }
    }
}
