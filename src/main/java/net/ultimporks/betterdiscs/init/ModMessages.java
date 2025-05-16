package net.ultimporks.betterdiscs.init;


import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.MessageDirection;
import net.minecraft.resources.ResourceLocation;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.network.C2S.*;
import net.ultimporks.betterdiscs.network.S2C.*;

public class ModMessages {
    private static FrameworkNetwork playChannel;

    public static void init() {
        playChannel = FrameworkAPI.createNetworkBuilder(new ResourceLocation(
                Reference.MOD_ID, Reference.NETWORK_ID),1)

                // PLAY TO SERVER
                .registerPlayMessage(C2SSyncParticleMessage.class, MessageDirection.PLAY_SERVER_BOUND)
                .registerPlayMessage(C2SSyncVolumeMessage.class, MessageDirection.PLAY_SERVER_BOUND)
                .registerPlayMessage(C2SSyncPlayButtonMessage.class, MessageDirection.PLAY_SERVER_BOUND)
                .registerPlayMessage(C2SSyncStopButtonMessage.class, MessageDirection.PLAY_SERVER_BOUND)

                // PLAY TO CLIENT
                .registerPlayMessage(S2CSyncJukeblockPlayMessage.class, MessageDirection.PLAY_CLIENT_BOUND)
                .registerPlayMessage(S2CSyncJukeblockStopMessage.class, MessageDirection.PLAY_CLIENT_BOUND)
                .registerPlayMessage(S2CSyncJukeboxSpeakersMessage.class, MessageDirection.PLAY_CLIENT_BOUND)
                .registerPlayMessage(S2CSyncNoteblockSpeakersMessage.class, MessageDirection.PLAY_CLIENT_BOUND)
                .registerPlayMessage(S2CSyncJukeboxOrNoteblockStopMessage.class, MessageDirection.PLAY_CLIENT_BOUND)

                .build();
    }

    public static FrameworkNetwork getPlayChannel() {
        return playChannel;
    }


}
