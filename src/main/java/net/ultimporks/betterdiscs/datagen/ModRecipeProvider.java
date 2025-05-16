package net.ultimporks.betterdiscs.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.init.ModBlocks;
import net.ultimporks.betterdiscs.init.ModItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> pWriter) {
        // Resin Ball
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.RESIN_BALL.get())
                .requires(Items.BLACK_DYE)
                .requires(Items.SLIME_BALL)
                .unlockedBy("has_an_ingredient", has(Items.HONEY_BOTTLE))
                .save(pWriter);
        // Tuning Tool
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.TUNING_TOOL.get())
                .pattern(" I ")
                .pattern("I I")
                .pattern("CI ")
                .define('I', Items.IRON_INGOT)
                .define('C', Items.COPPER_INGOT)
                .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                .save(pWriter);
        // Speaker
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.SPEAKER.get(), 2)
                .pattern("III")
                .pattern("WNW")
                .pattern("III")
                .define('N', Blocks.NOTE_BLOCK)
                .define('I', Items.IRON_INGOT)
                .define('W', ItemTags.WOOL)
                .unlockedBy(getHasName(Blocks.NOTE_BLOCK), has(Blocks.NOTE_BLOCK))
                .save(pWriter);
        // Press Machine
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RECORD_PRESS.get())
                .pattern("SSS")
                .pattern("SIS")
                .pattern("SSS")
                .define('S', Blocks.SMOOTH_STONE)
                .define('I', Blocks.IRON_BLOCK)
                .unlockedBy(getHasName(Blocks.SMOOTH_STONE), has(Blocks.SMOOTH_STONE))
                .save(pWriter);
        // Lathe Machine
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RECORD_LATHE.get())
                .pattern("   ")
                .pattern(" T ")
                .pattern("SSS")
                .define('T', Items.STICK)
                .define('S', Blocks.SMOOTH_STONE_SLAB)
                .unlockedBy(getHasName(Blocks.SMOOTH_STONE_SLAB), has(Blocks.SMOOTH_STONE_SLAB))
                .save(pWriter);
        // Ceiling Speaker
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.CEILING_SPEAKER.get())
                .requires(ModBlocks.SPEAKER.get())
                .unlockedBy(getHasName(ModBlocks.SPEAKER.get()), has(ModBlocks.SPEAKER.get()))
                .save(pWriter);
        // Wall Speaker
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.WALL_SPEAKER.get())
                .requires(ModBlocks.CEILING_SPEAKER.get())
                .unlockedBy(getHasName(ModBlocks.CEILING_SPEAKER.get()), has(ModBlocks.CEILING_SPEAKER.get()))
                .save(pWriter);
        // Jukeblock
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.JUKEBLOCK.get())
                .pattern("WWW")
                .pattern("SJS")
                .pattern("WCW")
                .define('J', Items.JUKEBOX)
                .define('S', ModBlocks.SPEAKER.get())
                .define('C', Tags.Items.CHESTS_WOODEN)
                .define('W', ItemTags.PLANKS)
                .unlockedBy(getHasName(Blocks.JUKEBOX), has(Blocks.JUKEBOX))
                .save(pWriter);

    }



    protected static void oreSmelting(@NotNull Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.SMELTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(@NotNull Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.BLASTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static void oreCooking(@NotNull Consumer<FinishedRecipe> pFinishedRecipeConsumer, RecipeSerializer<? extends AbstractCookingRecipe> pCookingSerializer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult,
                    pExperience, pCookingTime, pCookingSerializer)
                    .group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(pFinishedRecipeConsumer, Reference.MOD_ID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }

}
