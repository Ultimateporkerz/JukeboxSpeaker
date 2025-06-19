package net.ultimporks.betterdiscs.network;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.ultimporks.betterdiscs.block.entity.JukeblockBlockEntity;
import net.ultimporks.betterdiscs.block.entity.SpeakerBlockEntity;
import net.ultimporks.betterdiscs.network.C2S.C2SSyncParticleMessage;
import net.ultimporks.betterdiscs.network.C2S.C2SSyncPlayButtonMessage;
import net.ultimporks.betterdiscs.network.C2S.C2SSyncStopButtonMessage;
import net.ultimporks.betterdiscs.network.C2S.C2SSyncVolumeMessage;

public class ServerPayloadHandler {

    public static void handleSyncParticleMessage(final C2SSyncParticleMessage data, final IPayloadContext context) {
        Player player = context.player();
        BlockEntity blockEntity = player.level().getBlockEntity(data.speakerPos());
        if (blockEntity instanceof SpeakerBlockEntity speakerBlock) {
            speakerBlock.setParticlesEnabled(data.particlesEnabled());
        }

        if (blockEntity instanceof JukeblockBlockEntity jukeblockBlockEntity) {
         jukeblockBlockEntity.setParticlesEnabled(data.particlesEnabled());
           }
    }

    public static void handleSyncPlayButtonMessage(final C2SSyncPlayButtonMessage data, final IPayloadContext context) {
        Level level = context.player().level();
        BlockEntity blockEntity = level.getBlockEntity(data.jukeblockPos());
            if (blockEntity instanceof JukeblockBlockEntity jukeblockBlockEntity) {
        // Activate BlockEntity
                jukeblockBlockEntity.startPlaying();
           }
    }

    public static void handleSyncStopButtonMessage(final C2SSyncStopButtonMessage data, final IPayloadContext context) {
        Level level = context.player().level();
        BlockEntity blockEntity = level.getBlockEntity(data.jukeblockPos());
        if (blockEntity instanceof JukeblockBlockEntity jukeblockBlockEntity) {
            // Stop the BlockEntity from playing
            jukeblockBlockEntity.setStopped();
        }
    }

    public static void handleSyncVolumeMessage(final C2SSyncVolumeMessage data, final IPayloadContext context) {
        Player player = context.player();
        BlockEntity blockEntity = player.level().getBlockEntity(data.blockPos());
        if (blockEntity instanceof SpeakerBlockEntity speakerBlock) {
            speakerBlock.setVolume(data.volume());
               } else if (blockEntity instanceof JukeblockBlockEntity jukeblockBlock) {
                  jukeblockBlock.setVolume(data.volume());
        }
    }

}