package net.ultimporks.betterdiscs.events;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.client.entity.renderer.RecordLatheBlockEntityRenderer;
import net.ultimporks.betterdiscs.client.entity.renderer.RecordPressBlockEntityRenderer;
import net.ultimporks.betterdiscs.client.screen.JukeblockScreen;
import net.ultimporks.betterdiscs.client.screen.RecordLatheStationScreen;
import net.ultimporks.betterdiscs.client.screen.RecordPressStationScreen;
import net.ultimporks.betterdiscs.client.screen.SpeakerScreen;
import net.ultimporks.betterdiscs.init.ModBlockEntities;
import net.ultimporks.betterdiscs.init.ModMenuTypes;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
       MenuScreens.register(ModMenuTypes.RECORD_PRESS_STATION_MENU.get(), RecordPressStationScreen::new);
       MenuScreens.register(ModMenuTypes.RECORD_LATHE_STATION_MENU.get(), RecordLatheStationScreen::new);
       MenuScreens.register(ModMenuTypes.SPEAKER_MENU.get(), SpeakerScreen::new);
       MenuScreens.register(ModMenuTypes.JUKEBOX_MENU.get(), JukeblockScreen::new);
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
       event.registerBlockEntityRenderer(ModBlockEntities.RECORD_LATHE_BE.get(), RecordLatheBlockEntityRenderer::new);
       event.registerBlockEntityRenderer(ModBlockEntities.RECORD_PRESS_BE.get(), RecordPressBlockEntityRenderer::new);

    }
}
