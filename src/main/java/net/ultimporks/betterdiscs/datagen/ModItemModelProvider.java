package net.ultimporks.betterdiscs.datagen;

import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.init.ModItems;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Reference.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.BLANK_MUSIC_DISC.get());
        basicItem(ModItems.RESIN_BALL.get());
        basicItem(ModItems.TUNING_TOOL.get());
    }
}
