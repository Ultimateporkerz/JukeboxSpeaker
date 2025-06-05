package net.ultimporks.betterdiscs.init;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.item.BlankMusicDisc;
import net.ultimporks.betterdiscs.item.ResinBallItem;
import net.ultimporks.betterdiscs.item.TuningTool;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(Reference.MOD_ID);

    // Blank Music Disc
    public static final DeferredItem<Item> RESIN_BALL = ITEMS.register("resin_ball",
            () -> new ResinBallItem(new Item.Properties()
                    .stacksTo(64)));

    public static final DeferredItem<Item> BLANK_MUSIC_DISC = ITEMS.register("blank_music_disc",
            () -> new BlankMusicDisc(new Item.Properties()
                    .stacksTo(16)));

    // Speaker Tuner
    public static final DeferredItem<Item> TUNING_TOOL = ITEMS.register("tuning_tool",
            () -> new TuningTool(new Item.Properties()
                    .stacksTo(1)));


    public static void registerItems(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

