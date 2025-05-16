package net.ultimporks.betterdiscs.datagen.loot;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import net.ultimporks.betterdiscs.init.ModBlocks;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.RECORD_PRESS.get());
        this.dropSelf(ModBlocks.RECORD_LATHE.get());
        this.dropSelf(ModBlocks.SPEAKER.get());
        this.dropSelf(ModBlocks.CEILING_SPEAKER.get());
        this.dropSelf(ModBlocks.WALL_SPEAKER.get());
        this.dropSelf(ModBlocks.JUKEBLOCK.get());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
