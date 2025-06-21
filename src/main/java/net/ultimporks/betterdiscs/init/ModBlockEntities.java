package net.ultimporks.betterdiscs.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.block.entity.*;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Reference.MOD_ID);

    public static final RegistryObject<BlockEntityType<RecordPressBlockEntity>> RECORD_PRESS_BE =
            BLOCK_ENTITIES.register("record_press_be", () ->
                    BlockEntityType.Builder.of(RecordPressBlockEntity::new,
                            ModBlocks.RECORD_PRESS.get()).build(null));

    public static final RegistryObject<BlockEntityType<RecordLatheBlockEntity>> RECORD_LATHE_BE =
            BLOCK_ENTITIES.register("record_lathe_be", () ->
                    BlockEntityType.Builder.of(RecordLatheBlockEntity::new,
                            ModBlocks.RECORD_LATHE.get()).build(null));

    // Speakers
    public static final RegistryObject<BlockEntityType<SpeakerBlockEntity>> SPEAKER_BE =
            BLOCK_ENTITIES.register("speaker_be", () ->
                    BlockEntityType.Builder.of(SpeakerBlockEntity::new,
                            ModBlocks.SPEAKER.get(),
                            ModBlocks.CEILING_SPEAKER.get(),
                            ModBlocks.WALL_SPEAKER.get())
                            .build(null));

    // Jukebox
    public static final RegistryObject<BlockEntityType<JukeblockBlockEntity>> JUKEBOX_BE =
            BLOCK_ENTITIES.register("jukebox_be", () ->
                    BlockEntityType.Builder.of(JukeblockBlockEntity::new,
                            ModBlocks.JUKEBLOCK.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }


}
