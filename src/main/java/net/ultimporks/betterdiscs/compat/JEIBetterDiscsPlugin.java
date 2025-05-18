package net.ultimporks.betterdiscs.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.client.screen.RecordLatheStationScreen;
import net.ultimporks.betterdiscs.client.screen.RecordPressStationScreen;
import net.ultimporks.betterdiscs.init.ModRecipes;
import net.ultimporks.betterdiscs.recipe.RecordLatheRecipe;
import net.ultimporks.betterdiscs.recipe.RecordPressRecipe;

import java.util.List;

@JeiPlugin
public class JEIBetterDiscsPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new RecordPressCategory(registration.getJeiHelpers().getGuiHelper()));

        registration.addRecipeCategories(new RecordLatheCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<RecordPressRecipe> pressRecipes = recipeManager
                .getAllRecipesFor(ModRecipes.RECORD_PRESS_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(RecordPressCategory.RECORD_PRESS_RECIPE_TYPE, pressRecipes);

        List<RecordLatheRecipe> latheRecipes = recipeManager
                .getAllRecipesFor(ModRecipes.RECORD_LATHE_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(RecordLatheCategory.RECORD_LATHE_RECIPE_TYPE, latheRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(RecordPressStationScreen.class, 80, 30, 20, 20,
                RecordPressCategory.RECORD_PRESS_RECIPE_TYPE);

        registration.addRecipeClickArea(RecordLatheStationScreen.class, 80, 74, 20, 8,
                RecordLatheCategory.RECORD_LATHE_RECIPE_TYPE);
    }
}
