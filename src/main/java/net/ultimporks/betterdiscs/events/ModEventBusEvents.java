package net.ultimporks.betterdiscs.events;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.ultimporks.betterdiscs.BetterMusicDiscs;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.data.SpeakerLinkData;

@EventBusSubscriber(modid = Reference.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level && !event.getLevel().isClientSide()) {
            try {
                SpeakerLinkData data = SpeakerLinkData.get((ServerLevel) event.getLevel());
            } catch (Exception ex) {
                BetterMusicDiscs.generalLOGGING("(ModEventBusEvents) - failed to load Speaker Data: " + ex);
            }
        }
    }
}


