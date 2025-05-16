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
import net.ultimporks.betterdiscs.recipe.RecordPressRecipe;
import org.jetbrains.annotations.Nullable;

public class RecordPressCategory implements IRecipeCategory<RecordPressRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(Reference.MOD_ID, "record_press");
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID,
            "textures/gui/record_press_station_gui.png");

    public static RecipeType<RecordPressRecipe> RECORD_PRESS_RECIPE_TYPE =
            new RecipeType<>(UID, RecordPressRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public RecordPressCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 82);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.RECORD_PRESS.get()));
    }


    @Override
    public RecipeType<RecordPressRecipe> getRecipeType() {
        return RECORD_PRESS_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.betterdiscs.record_press");
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecordPressRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 80, 11).addIngredients(recipe.getIngredients().get(0));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 59).addItemStack(recipe.getResultItem(null));

    }
}
