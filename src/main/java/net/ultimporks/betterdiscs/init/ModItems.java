package net.ultimporks.betterdiscs.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.item.BlankMusicDisc;
import net.ultimporks.betterdiscs.item.ResinBallItem;
import net.ultimporks.betterdiscs.item.TuningTool;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);

    // Blank Music Disc
    public static final RegistryObject<Item> RESIN_BALL = ITEMS.register("resin_ball",
            () -> new ResinBallItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> BLANK_MUSIC_DISC = ITEMS.register("blank_music_disc",
            () -> new BlankMusicDisc(new Item.Properties().stacksTo(16)));

    // Speaker Tuner
    public static final RegistryObject<Item> TUNING_TOOL = ITEMS.register("tuning_tool",
            () -> new TuningTool(new Item.Properties().stacksTo(1)));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

