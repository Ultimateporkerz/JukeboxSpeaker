package net.ultimporks.betterdiscs;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.ultimporks.betterdiscs.client.entity.renderer.RecordLatheBlockEntityRenderer;
import net.ultimporks.betterdiscs.client.entity.renderer.RecordPressBlockEntityRenderer;
import net.ultimporks.betterdiscs.client.screen.RecordLatheStationScreen;
import net.ultimporks.betterdiscs.client.screen.RecordPressStationScreen;
import net.ultimporks.betterdiscs.client.screen.SpeakerScreen;
import net.ultimporks.betterdiscs.init.*;
import net.ultimporks.betterdiscs.init.ModMenuTypes;
import net.ultimporks.betterdiscs.network.NetworkHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.MOD_ID)
public class BetterMusicDiscs {
    // Debugging
    private static final boolean debuggingJukebox = true;
    private static final boolean debuggingNoteblock = true;
    private static final boolean debuggingJukeblock = true;
    private static final boolean debuggingSpeaker = true;
    private static final boolean debuggingGeneral = true;

    private static final Logger LOGGER = LogManager.getLogger();

    public BetterMusicDiscs(IEventBus modEventBus) {
        ModBlocks.register(modEventBus);
        ModItems.registerItems(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModDataComponents.register(modEventBus);

        modEventBus.addListener(NetworkHandler::register);
    }

    public static void jukeboxLOGGING(String logMessage) {
        if (debuggingJukebox) {
            LOGGER.info("BetterMusicDiscs LOGGER - JUKEBOX - {}", logMessage);
        }
    }
    public static void noteblockLOGGING(String logMessage) {
        if (debuggingNoteblock) {
            LOGGER.info("BetterMusicDiscs LOGGER - NOTEBLOCK - {}", logMessage);
        }
    }
    public static void jukeblockLOGGING(String logMessage) {
        if (debuggingJukeblock) {
            LOGGER.info("BetterMusicDiscs LOGGER - JUKEBLOCK - {}", logMessage);
        }
    }
    public static void speakerLOGGING(String logMessage) {
        if (debuggingSpeaker) {
            LOGGER.info("BetterMusicDiscs LOGGER - SPEAKER - {}", logMessage);
        }
    }
    public static void generalLOGGING(String logMessage) {
        if (debuggingGeneral) {
            LOGGER.info("BetterMusicDiscs LOGGER - TUNING_TOOL - {}", logMessage);
        }
    }

    @EventBusSubscriber(modid = Reference.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.RECORD_LATHE_STATION_MENU.get(), RecordLatheStationScreen::new);
            event.register(ModMenuTypes.RECORD_PRESS_STATION_MENU.get(), RecordPressStationScreen::new);
            event.register(ModMenuTypes.SPEAKER_MENU.get(), SpeakerScreen::new);
            // event.register(ModMenuTypes.JUKEBOX_MENU.get(), JukeblockScreen::new);
        }

        @SubscribeEvent
        public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.RECORD_LATHE_BE.get(), RecordLatheBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.RECORD_PRESS_BE.get(), RecordPressBlockEntityRenderer::new);

        }
    }
}
