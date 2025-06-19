package net.ultimporks.betterdiscs.datagen;

import net.minecraft.data.PackOutput;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.init.ModBlocks;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Reference.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        horizontalBlock(ModBlocks.RECORD_PRESS.get(),
                new ModelFile.UncheckedModelFile(ResourceLocation.
                        fromNamespaceAndPath(Reference.MOD_ID,"block/record_press")));

        horizontalBlock(ModBlocks.RECORD_LATHE.get(),
               new ModelFile.UncheckedModelFile(ResourceLocation.
                       fromNamespaceAndPath(Reference.MOD_ID, "block/record_lathe")));

        horizontalBlock(ModBlocks.SPEAKER.get(),
                new ModelFile.UncheckedModelFile(ResourceLocation.
                        fromNamespaceAndPath(Reference.MOD_ID, "block/speaker")));

        horizontalBlock(ModBlocks.CEILING_SPEAKER.get(),
                new ModelFile.UncheckedModelFile(ResourceLocation.
                        fromNamespaceAndPath(Reference.MOD_ID, "block/ceiling_speaker")));

        horizontalBlock(ModBlocks.WALL_SPEAKER.get(),
                new ModelFile.UncheckedModelFile(ResourceLocation.
                        fromNamespaceAndPath(Reference.MOD_ID, "block/wall_speaker")));

        horizontalBlock(ModBlocks.JUKEBLOCK.get(),
                new ModelFile.UncheckedModelFile(ResourceLocation.
                        fromNamespaceAndPath(Reference.MOD_ID, "block/jukeblock")));
    }

    // Helper Method
    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
}