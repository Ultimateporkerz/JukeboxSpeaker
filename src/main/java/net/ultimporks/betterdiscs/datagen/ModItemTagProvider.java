package net.ultimporks.betterdiscs.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.ultimporks.betterdiscs.BetterMusicDiscs;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.init.CustomModItems;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, Reference.MOD_ID, existingFileHelper);
    }




    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        if (!BetterMusicDiscs.isRelease()) {
            this.tag(ItemTags.MUSIC_DISCS)
                    .add(CustomModItems.WOMP_PORTAL.get())
                    .add(CustomModItems.HAVE_GHOSTS.get())
                    .add(CustomModItems.BAD.get());

            this.tag(ItemTags.CREEPER_DROP_MUSIC_DISCS)
                    .add(CustomModItems.WOMP_PORTAL.get())
                    .add(CustomModItems.HAVE_GHOSTS.get())
                    .add(CustomModItems.BAD.get());

        }
    }
}
