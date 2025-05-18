package net.ultimporks.betterdiscs.init;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.util.menus.JukeboxMenu;
import net.ultimporks.betterdiscs.util.menus.RecordLatheStationMenu;
import net.ultimporks.betterdiscs.util.menus.RecordPressStationMenu;
import net.ultimporks.betterdiscs.util.menus.SpeakerMenus;


public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Reference.MOD_ID);

    public static final RegistryObject<MenuType<RecordPressStationMenu>> RECORD_PRESS_STATION_MENU =
            registerMenuType("record_press_station_menu", RecordPressStationMenu::new);

    public static final RegistryObject<MenuType<RecordLatheStationMenu>> RECORD_LATHE_STATION_MENU =
            registerMenuType("record_lathe_station_menu", RecordLatheStationMenu::new);

    public static final RegistryObject<MenuType<SpeakerMenus>> SPEAKER_MENU =
            registerMenuType("speaker_menu", SpeakerMenus::new);

    public static final RegistryObject<MenuType<JukeboxMenu>> JUKEBOX_MENU =
            registerMenuType("jukebox_menu", JukeboxMenu::new);

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
