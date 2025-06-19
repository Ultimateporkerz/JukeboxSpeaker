package net.ultimporks.betterdiscs.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ultimporks.betterdiscs.Reference;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Reference.MOD_ID);

    public static final Supplier<CreativeModeTab> BETTERDISCS = CREATIVE_MODE_TABS.register("better_music_discs",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.SPEAKER.get()))
                    .title(Component.translatable("betterdiscs.creativetab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.RESIN_BALL.get());
                        output.accept(ModBlocks.RECORD_PRESS.get());
                        output.accept(ModItems.BLANK_MUSIC_DISC.get());
                        output.accept(ModBlocks.RECORD_LATHE.get());
                        output.accept(ModBlocks.SPEAKER.get());
                        output.accept(ModBlocks.CEILING_SPEAKER.get());
                        output.accept(ModBlocks.WALL_SPEAKER.get());
                        output.accept(ModItems.TUNING_TOOL.get());
                        output.accept(ModBlocks.JUKEBLOCK.get());
                    }).build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
