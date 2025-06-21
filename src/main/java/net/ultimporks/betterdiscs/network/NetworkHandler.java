package net.ultimporks.betterdiscs.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.ultimporks.betterdiscs.network.C2S.C2SParticleMessage;
import net.ultimporks.betterdiscs.network.C2S.C2SButtonMessage;
import net.ultimporks.betterdiscs.network.C2S.C2SVolumeMessage;
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
                C2SButtonMessage.TYPE,
                C2SButtonMessage.STREAM_CODEC,
                JukeblockPayloadHandler::handleButtonsMessage
        );

        registrar.playToServer(
                C2SParticleMessage.TYPE,
                C2SParticleMessage.STREAM_CODEC,
                JukeblockPayloadHandler::handleSyncParticleMessage
        );

        registrar.playToServer(
                C2SVolumeMessage.TYPE,
                C2SVolumeMessage.STREAM_CODEC,
                JukeblockPayloadHandler::handleSyncVolumeMessage
        );
    }
}
