package net.ultimporks.betterdiscs.events;

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ultimporks.betterdiscs.BetterMusicDiscs;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.data.SpeakerLinkData;
import net.ultimporks.betterdiscs.util.SpeakerLinkUtil;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
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


