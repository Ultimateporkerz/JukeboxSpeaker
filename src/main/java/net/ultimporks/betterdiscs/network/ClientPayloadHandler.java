package net.ultimporks.betterdiscs.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.ultimporks.betterdiscs.client.JukeblockSoundEvents;
import net.ultimporks.betterdiscs.client.SpeakerSoundEvent;
import net.ultimporks.betterdiscs.network.S2C.*;

public class ClientPayloadHandler {

    public static void handleJukeblockPlayMessage(final S2CSyncJukeblockPlayMessage data, final IPayloadContext context) {
        if (data.speakers()) {
            JukeblockSoundEvents.playJukeblock(data.jukeblockOrSpeakerPos(), data.currentDisc(), data.volume());
        } else {
            JukeblockSoundEvents.playJukeblockSpeakers(data.jukeblockOrSpeakerPos(), data.currentDisc(), data.volume());
        }
    }

    public static void handleJukeblockStopMessage(final S2CSyncJukeblockStopMessage data, final IPayloadContext context) {
        if (data.isSpeaker()) {
            JukeblockSoundEvents.stopJukeblockSound(data.jukeblockPos());
        } else {
            if (data.isStoppingAll()) {
                JukeblockSoundEvents.stopAllSpeakerSounds();
            } else {
                JukeblockSoundEvents.stopSpeakerSound(data.jukeblockPos());
            }
        }
    }

    public static void handleJukeboxOrNoteblockStopMessage(final S2CSyncJukeboxOrNoteblockStopMessage data, final IPayloadContext context) {
        if (data.isStoppingAll()) {
            SpeakerSoundEvent.stopAllSounds();
        } else {
            SpeakerSoundEvent.stopSound(data.speakerPos());
        }
    }

    public static void handleJukeboxSpeakerMessage(final S2CSyncJukeboxSpeakersMessage data, final IPayloadContext context) {
        SpeakerSoundEvent.playSound(data.currentDisc(), data.blockPos(), data.volume());
    }

    public static void handleNoteblockSpeakerMessage(final S2CSyncNoteblockSpeakersMessage data, final IPayloadContext context) {
        SpeakerSoundEvent.playNoteBlock(data.speakerPos(), data.instrumentName(), data.note(), data.volume());

    }

}
