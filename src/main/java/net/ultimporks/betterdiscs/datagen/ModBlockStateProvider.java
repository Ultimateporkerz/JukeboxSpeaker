package net.ultimporks.betterdiscs.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.init.ModBlocks;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Reference.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        horizontalBlock(ModBlocks.RECORD_PRESS.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/record_press")));

        horizontalBlock(ModBlocks.RECORD_LATHE.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/record_lathe")));

        horizontalBlock(ModBlocks.SPEAKER.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/speaker")));

        horizontalBlock(ModBlocks.CEILING_SPEAKER.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/ceiling_speaker")));

        horizontalBlock(ModBlocks.WALL_SPEAKER.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/wall_speaker")));

        horizontalBlock(ModBlocks.JUKEBLOCK.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/jukeblock")));
    }

    // Helper Method
    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }




}