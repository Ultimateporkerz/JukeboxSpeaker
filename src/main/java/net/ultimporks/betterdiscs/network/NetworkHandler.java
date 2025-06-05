package net.ultimporks.betterdiscs.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.ultimporks.betterdiscs.network.C2S.C2SSyncParticleMessage;
import net.ultimporks.betterdiscs.network.C2S.C2SSyncPlayButtonMessage;
import net.ultimporks.betterdiscs.network.C2S.C2SSyncStopButtonMessage;
import net.ultimporks.betterdiscs.network.C2S.C2SSyncVolumeMessage;
import net.ultimporks.betterdiscs.network.S2C.*;

public class NetworkHandler {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1")
                .executesOn(HandlerThread.MAIN);

        // Server to Client
        registrar.playToClient(
                S2CSyncJukeblockPlayMessage.TYPE,
                S2CSyncJukeblockPlayMessage.STREAM_CODEC,
                ClientPayloadHandler::handleJukeblockPlayMessage
        );

        registrar.playToClient(
                S2CSyncJukeblockStopMessage.TYPE,
                S2CSyncJukeblockStopMessage.STREAM_CODEC,
                ClientPayloadHandler::handleJukeblockStopMessage
        );

        registrar.playToClient(
                S2CSyncJukeboxOrNoteblockStopMessage.TYPE,
                S2CSyncJukeboxOrNoteblockStopMessage.STREAM_CODEC,
                ClientPayloadHandler::handleJukeboxOrNoteblockStopMessage
        );

        registrar.playToClient(
                S2CSyncJukeboxSpeakersMessage.TYPE,
                S2CSyncJukeboxSpeakersMessage.STREAM_CODEC,
                ClientPayloadHandler::handleJukeboxSpeakerMessage
        );

        registrar.playToClient(
                S2CSyncNoteblockSpeakersMessage.TYPE,
                S2CSyncNoteblockSpeakersMessage.STREAM_CODEC,
                ClientPayloadHandler::handleNoteblockSpeakerMessage
        );


        // Client to Server
        registrar.playToServer(
                C2SSyncParticleMessage.TYPE,
                C2SSyncParticleMessage.STREAM_CODEC,
                ServerPayloadHandler::handleSyncParticleMessage
        );

        registrar.playToServer(
                C2SSyncPlayButtonMessage.TYPE,
                C2SSyncPlayButtonMessage.STREAM_CODEC,
                ServerPayloadHandler::handleSyncPlayButtonMessage
        );

        registrar.playToServer(
                C2SSyncStopButtonMessage.TYPE,
                C2SSyncStopButtonMessage.STREAM_CODEC,
                ServerPayloadHandler::handleSyncStopButtonMessage
        );

        registrar.playToServer(
                C2SSyncVolumeMessage.TYPE,
                C2SSyncVolumeMessage.STREAM_CODEC,
                ServerPayloadHandler::handleSyncVolumeMessage
        );
    }
}
