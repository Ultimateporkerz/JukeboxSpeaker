package net.ultimporks.betterdiscs.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ultimporks.betterdiscs.Reference;

public class CustomModItems {
    public static final DeferredRegister<Item> CUSTOM_ITEMS = 
            DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);


    public static final RegistryObject<Item> WOMP_PORTAL = CUSTOM_ITEMS.register("womp_portal",
            () -> new RecordItem(4, ModSounds.WOMP_PORTAL, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 5480));


    public static final RegistryObject<Item> HAVE_GHOSTS = CUSTOM_ITEMS.register("have_ghosts",
            () -> new RecordItem(6, ModSounds.HAVE_GHOSTS, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 3720));

    public static final RegistryObject<Item> BAD = CUSTOM_ITEMS.register("bad",
            () -> new RecordItem(3, ModSounds.BAD, new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 3000));



    public static void register(IEventBus eventBus) {
        CUSTOM_ITEMS.register(eventBus);
    }
}
