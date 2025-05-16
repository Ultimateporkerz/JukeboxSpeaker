package net.ultimporks.betterdiscs;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.ultimporks.betterdiscs.init.*;
import net.ultimporks.betterdiscs.init.ModMenuTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.MOD_ID)
public class BetterMusicDiscs {
    // Release
    private static final boolean isRelease = false;

    // Debugging
    private static final boolean debuggingJukebox = false;
    private static final boolean debuggingNoteblock = false;
    private static final boolean debuggingJukeblock = false;
    private static final boolean debuggingSpeaker = false;
    private static final boolean debuggingGeneral = false;

    private static final Logger LOGGER = LogManager.getLogger();

    public BetterMusicDiscs(FMLJavaModLoadingContext context) {
        ModContainer modContainer = context.getContainer();
        IEventBus modEventBus = context.getModEventBus();

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipes.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        if (!isRelease) {
            ModSounds.register(modEventBus);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModMessages.register();
    }

    public static boolean isRelease() {
        return isRelease;
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
