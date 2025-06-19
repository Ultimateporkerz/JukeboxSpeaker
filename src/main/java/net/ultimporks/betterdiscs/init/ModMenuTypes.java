package net.ultimporks.betterdiscs.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.util.menus.JukeblockMenu;
import net.ultimporks.betterdiscs.util.menus.RecordLatheStationMenu;
import net.ultimporks.betterdiscs.util.menus.RecordPressStationMenu;
import net.ultimporks.betterdiscs.util.menus.SpeakerMenus;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, Reference.MOD_ID);

    public static final DeferredHolder<MenuType<?>,MenuType<RecordPressStationMenu>> RECORD_PRESS_STATION_MENU =
            registerMenuType("record_press_station_menu", RecordPressStationMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<RecordLatheStationMenu>> RECORD_LATHE_STATION_MENU =
            registerMenuType("record_lathe_station_menu", RecordLatheStationMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<SpeakerMenus>> SPEAKER_MENU =
            registerMenuType("speaker_menu", SpeakerMenus::new);

    public static final DeferredHolder<MenuType<?>, MenuType<JukeblockMenu>> JUKEBOX_MENU =
            registerMenuType("jukebox_menu", JukeblockMenu::new);

    private static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
