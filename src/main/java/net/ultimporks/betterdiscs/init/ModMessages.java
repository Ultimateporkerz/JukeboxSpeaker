package net.ultimporks.betterdiscs.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.network.C2S.*;
import net.ultimporks.betterdiscs.network.S2C.*;

public class ModMessages {
    public static SimpleChannel INSTANCE;
    private static int packetId = 1;

    private static int id() {
        return packetId++;
    }

    public static void register() {

        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Reference.MOD_ID, "betterdiscs_network"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(C2SSyncParticleMessage.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(C2SSyncParticleMessage::encode)
                .decoder(C2SSyncParticleMessage::new)
                .consumerMainThread(C2SSyncParticleMessage::handle)
                .add();

        net.messageBuilder(C2SSyncPlayButtonMessage.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(C2SSyncPlayButtonMessage::encode)
                .decoder(C2SSyncPlayButtonMessage::new)
                .consumerMainThread(C2SSyncPlayButtonMessage::handle)
                .add();

        net.messageBuilder(C2SSyncStopButtonMessage.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(C2SSyncStopButtonMessage::encode)
                .decoder(C2SSyncStopButtonMessage::new)
                .consumerMainThread(C2SSyncStopButtonMessage::handle)
                .add();

        net.messageBuilder(C2SSyncVolumeMessage.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(C2SSyncVolumeMessage::encode)
                .decoder(C2SSyncVolumeMessage::new)
                .consumerMainThread(C2SSyncVolumeMessage::handle)
                .add();



        net.messageBuilder(S2CSyncJukeblockPlayMessage.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(S2CSyncJukeblockPlayMessage::encode)
                .decoder(S2CSyncJukeblockPlayMessage::new)
                .consumerMainThread(S2CSyncJukeblockPlayMessage::handle)
                .add();

        net.messageBuilder(S2CSyncJukeblockStopMessage.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(S2CSyncJukeblockStopMessage::encode)
                .decoder(S2CSyncJukeblockStopMessage::new)
                .consumerMainThread(S2CSyncJukeblockStopMessage::handle)
                .add();

        net.messageBuilder(S2CSyncJukeboxOrNoteblockStopMessage.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(S2CSyncJukeboxOrNoteblockStopMessage::encode)
                .decoder(S2CSyncJukeboxOrNoteblockStopMessage::new)
                .consumerMainThread(S2CSyncJukeboxOrNoteblockStopMessage::handle)
                .add();

        net.messageBuilder(S2CSyncJukeboxSpeakersMessage.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(S2CSyncJukeboxSpeakersMessage::encode)
                .decoder(S2CSyncJukeboxSpeakersMessage::new)
                .consumerMainThread(S2CSyncJukeboxSpeakersMessage::handle)
                .add();

        net.messageBuilder(S2CSyncNoteblockSpeakersMessage.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(S2CSyncNoteblockSpeakersMessage::encode)
                .decoder(S2CSyncNoteblockSpeakersMessage::new)
                .consumerMainThread(S2CSyncNoteblockSpeakersMessage::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
    public static <MSG> void sendToAllPlayers(MSG message) {
        for (ServerPlayer player : net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
        }
    }


}
