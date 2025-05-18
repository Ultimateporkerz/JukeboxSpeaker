package net.ultimporks.betterdiscs.init;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.ultimporks.betterdiscs.Reference;

import java.util.function.UnaryOperator;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Reference.MOD_ID);

    public static final RegistryObject<DataComponentType<BlockPos>> JUKEBOX_POS = register("jukebox_pos",
            builder -> builder.persistent(BlockPos.CODEC));

    public static final RegistryObject<DataComponentType<BlockPos>> JUKEBLOCK_POS = register("jukeblock_pos",
            builder -> builder.persistent(BlockPos.CODEC));

    public static final RegistryObject<DataComponentType<BlockPos>> SPEAKER_POS = register("speaker_pos",
            builder -> builder.persistent(BlockPos.CODEC));

    public static final RegistryObject<DataComponentType<BlockPos>> NOTEBLOCK_POS = register("noteblock_pos",
            builder -> builder.persistent(BlockPos.CODEC));



    private static <T>RegistryObject<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderUnaryOperator) {
        return DATA_COMPONENT_TYPES.register(name, () -> builderUnaryOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}
