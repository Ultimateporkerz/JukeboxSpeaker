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

public record RecordLatheRecipe(Ingredient inputItem, ItemStack output) implements Recipe<RecordLatheRecipeInput> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(RecordLatheRecipeInput pInput, Level pLevel) {
        if (pLevel.isClientSide) {
            return false;
        }
        return inputItem.test(pInput.getItem(0));
    }

    @Override
    public ItemStack assemble(RecordLatheRecipeInput pInput, HolderLookup.Provider pRegistries) {
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
        return ModRecipes.RECORD_LATHE_STATION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.RECORD_LATHE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<RecordLatheRecipe> {
        public static final MapCodec<RecordLatheRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(RecordLatheRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(RecordLatheRecipe::output)
        ).apply(inst, RecordLatheRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, RecordLatheRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, RecordLatheRecipe::inputItem,
                        ItemStack.STREAM_CODEC, RecordLatheRecipe::output,
                        RecordLatheRecipe::new);

        @Override
        public MapCodec<RecordLatheRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RecordLatheRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}