package net.ultimporks.betterdiscs.init;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.network.C2S.*;
import net.ultimporks.betterdiscs.network.S2C.*;

public class ModMessages {
    private final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = ChannelBuilder.named(
            ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "betterdiscs_network"))
            .serverAcceptedVersions((status, version) -> true)
            .clientAcceptedVersions((status, version) -> true)
            .networkProtocolVersion(1)
            .simpleChannel();

    public static void register() {

        // PLAY TO CLIENT
        INSTANCE.messageBuilder(S2CSyncJukeblockPlayMessage.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(S2CSyncJukeblockPlayMessage::encode)
                .decoder(S2CSyncJukeblockPlayMessage::new)
                .consumerMainThread(S2CSyncJukeblockPlayMessage::handle)
                .add();

        INSTANCE.messageBuilder(S2CSyncJukeblockStopMessage.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(S2CSyncJukeblockStopMessage::encode)
                .decoder(S2CSyncJukeblockStopMessage::new)
                .consumerMainThread(S2CSyncJukeblockStopMessage::handle)
                .add();

        INSTANCE.messageBuilder(S2CSyncJukeboxSpeakersMessage.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(S2CSyncJukeboxSpeakersMessage::encode)
                .decoder(S2CSyncJukeboxSpeakersMessage::new)
                .consumerMainThread(S2CSyncJukeboxSpeakersMessage::handle)
                .add();

        INSTANCE.messageBuilder(S2CSyncNoteblockSpeakersMessage.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(S2CSyncNoteblockSpeakersMessage::encode)
                .decoder(S2CSyncNoteblockSpeakersMessage::new)
                .consumerMainThread(S2CSyncNoteblockSpeakersMessage::handle)
                .add();

        INSTANCE.messageBuilder(S2CSyncJukeboxOrNoteblockStopMessage.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(S2CSyncJukeboxOrNoteblockStopMessage::encode)
                .decoder(S2CSyncJukeboxOrNoteblockStopMessage::new)
                .consumerMainThread(S2CSyncJukeboxOrNoteblockStopMessage::handle)
                .add();

        // PLAY TO SERVER
        INSTANCE.messageBuilder(C2SParticleMessage.class, NetworkDirection.PLAY_TO_SERVER)
                .encoder(C2SParticleMessage::encode)
                .decoder(C2SParticleMessage::new)
                .consumerMainThread(C2SParticleMessage::handle)
                .add();

        INSTANCE.messageBuilder(C2SVolumeMessage.class, NetworkDirection.PLAY_TO_SERVER)
                .encoder(C2SVolumeMessage::encode)
                .decoder(C2SVolumeMessage::new)
                .consumerMainThread(C2SVolumeMessage::handle)
                .add();

        INSTANCE.messageBuilder(C2SButtonMessage.class, NetworkDirection.PLAY_TO_SERVER)
                .encoder(C2SButtonMessage::encode)
                .decoder(C2SButtonMessage::new)
                .consumerMainThread(C2SButtonMessage::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG msg) {
        INSTANCE.send(msg, PacketDistributor.SERVER.noArg());
    }

    public static <MSG> void sendToPlayer(MSG msg, ServerPlayer player) {
        INSTANCE.send(msg, PacketDistributor.PLAYER.with(player));
    }

    public static <MSG> void sendToAllPlayers(MSG msg) {
        for (ServerPlayer player : net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            INSTANCE.send(msg, PacketDistributor.PLAYER.with(player));
        }
    }
}
