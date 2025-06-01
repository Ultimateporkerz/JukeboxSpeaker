package net.ultimporks.betterdiscs.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.block.*;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MOD_ID);

    public static final RegistryObject<Block> RECORD_PRESS = registerBlock("record_press",
            () -> new RecordPressBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.METAL)));

    public static final RegistryObject<Block> RECORD_LATHE = registerBlock("record_lathe",
            () -> new RecordLatheBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(4.0F, 5.5F).destroyTime(5.0F).sound(SoundType.METAL)));

    // Jukeblock
   // public static final RegistryObject<Block> JUKEBLOCK = registerBlock("jukeblock",
          //  () -> new JukeblockBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(3.5F, 4.0F).destroyTime(3.0F).sound(SoundType.WOOD)));

    // Base Speaker Block
    public static final RegistryObject<Block> SPEAKER = registerBlock("speaker",
            () -> new SpeakerBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(2.5F, 3.5F).destroyTime(3.0F).sound(SoundType.WOOD)));

    // Ceiling Speaker
    public static final RegistryObject<Block> CEILING_SPEAKER = registerBlock("ceiling_speaker",
            () -> new CeilingSpeakerBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(1.0F, 2.0F).destroyTime(1.5F).sound(SoundType.WOOD).noOcclusion()));

    // Wall Speaker
    public static final RegistryObject<Block> WALL_SPEAKER = registerBlock("wall_speaker",
            () -> new WallSpeakerBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(1.0F, 2.0F).destroyTime(1.5F).sound(SoundType.WOOD).noOcclusion()));


    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
