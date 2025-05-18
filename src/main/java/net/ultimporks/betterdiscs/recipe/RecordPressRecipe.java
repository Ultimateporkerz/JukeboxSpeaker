package net.ultimporks.betterdiscs.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.ultimporks.betterdiscs.init.ModRecipes;

public record RecordPressRecipe(Ingredient inputItem, ItemStack output) implements Recipe<RecordPressRecipeInput> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(RecordPressRecipeInput pInput, Level pLevel) {
        if (pLevel.isClientSide) {
            return false;
        }
        return inputItem.test(pInput.getItem(0));
    }

    @Override
    public ItemStack assemble(RecordPressRecipeInput pInput, HolderLookup.Provider pRegistries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.RECORD_PRESS_STATION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.RECORD_PRESS_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<RecordPressRecipe> {
        public static final MapCodec<RecordPressRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(RecordPressRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(RecordPressRecipe::output)
        ).apply(inst, RecordPressRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, RecordPressRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, RecordPressRecipe::inputItem,
                        ItemStack.STREAM_CODEC, RecordPressRecipe::output,
                        RecordPressRecipe::new);

        @Override
        public MapCodec<RecordPressRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RecordPressRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}