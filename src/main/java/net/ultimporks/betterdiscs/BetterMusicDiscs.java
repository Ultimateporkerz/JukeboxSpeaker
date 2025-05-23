package net.ultimporks.betterdiscs;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.ultimporks.betterdiscs.init.*;
import net.ultimporks.betterdiscs.init.ModMenuTypes;
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

    public BetterMusicDiscs(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModDataComponents.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModMessages.register();
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
}
