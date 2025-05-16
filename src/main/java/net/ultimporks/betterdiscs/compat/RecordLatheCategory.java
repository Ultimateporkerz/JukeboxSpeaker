package net.ultimporks.betterdiscs.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.init.ModBlocks;
import net.ultimporks.betterdiscs.recipe.RecordLatheRecipe;
import org.jetbrains.annotations.Nullable;

public class RecordLatheCategory implements IRecipeCategory<RecordLatheRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(Reference.MOD_ID, "record_lathe");
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID,
            "textures/gui/record_lathe_station_gui.png");

    public static RecipeType<RecordLatheRecipe> RECORD_LATHE_RECIPE_TYPE =
            new RecipeType<>(UID, RecordLatheRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public RecordLatheCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 83);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.RECORD_LATHE.get()));
    }


    @Override
    public RecipeType<RecordLatheRecipe> getRecipeType() {
        return RECORD_LATHE_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.betterdiscs.record_lathe");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecordLatheRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 33).addIngredients(recipe.getIngredients().get(0));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 143, 33).addItemStack(recipe.getResultItem(null));

    }
}
