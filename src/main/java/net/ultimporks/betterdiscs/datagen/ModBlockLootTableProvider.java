package net.ultimporks.betterdiscs.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import net.ultimporks.betterdiscs.init.ModBlocks;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider pRegistries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), pRegistries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.CEILING_SPEAKER.get());
        dropSelf(ModBlocks.WALL_SPEAKER.get());
        dropSelf(ModBlocks.SPEAKER.get());

    //    dropSelf(ModBlocks.JUKEBLOCK.get());

        dropSelf(ModBlocks.RECORD_LATHE.get());
        dropSelf(ModBlocks.RECORD_PRESS.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
