package net.ultimporks.betterdiscs.network;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.ultimporks.betterdiscs.block.entity.JukeblockBlockEntity;
import net.ultimporks.betterdiscs.block.entity.SpeakerBlockEntity;
import net.ultimporks.betterdiscs.network.C2S.C2SParticleMessage;
import net.ultimporks.betterdiscs.network.C2S.C2SButtonMessage;
import net.ultimporks.betterdiscs.network.C2S.C2SVolumeMessage;

public class JukeblockPayloadHandler {

    // Handle the Play, Stop, Next, and Previous button functions.
    public static void handleButtonsMessage(final C2SButtonMessage data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            BlockPos jukeblockPos = data.jukeblockPos();
            Level level = context.player().level();
            BlockEntity blockEntity = level.getBlockEntity(jukeblockPos);
            String buttonName = data.buttonName();

            if (blockEntity instanceof JukeblockBlockEntity jukeblockBlock) {
                if (buttonName.equals("PlayButton")) {
                    jukeblockBlock.startPlaying(false, false);
                }

                if (buttonName.equals("StopButton")) {
                    jukeblockBlock.stopPlaying(false);
                }

                if (buttonName.equals("NextButton")) {
                   jukeblockBlock.nextTrack();
                }

                if (buttonName.equals("RewindButton")) {
                    jukeblockBlock.previousTrack();
                }
            }
        });
    }

    // Handle the Particle Toggle button
    public static void handleSyncParticleMessage(final C2SParticleMessage data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            BlockEntity blockEntity = player.level().getBlockEntity(data.speakerPos());
            if (blockEntity instanceof SpeakerBlockEntity speakerBlock) {
                speakerBlock.setParticlesEnabled(data.particlesEnabled());
            }

            if (blockEntity instanceof JukeblockBlockEntity jukeblockBlockEntity) {
                jukeblockBlockEntity.setParticlesEnabled(data.particlesEnabled());
            }
        });
    }

    // Handle the volume control slider/button
    public static void handleSyncVolumeMessage(final C2SVolumeMessage data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            BlockEntity blockEntity = player.level().getBlockEntity(data.blockPos());
            if (blockEntity instanceof SpeakerBlockEntity speakerBlock) {
                speakerBlock.setVolume(data.volume());
            }
            if (blockEntity instanceof JukeblockBlockEntity jukeblockBlock) {
                jukeblockBlock.setVolume(data.volume());
            }
        });
    }


}