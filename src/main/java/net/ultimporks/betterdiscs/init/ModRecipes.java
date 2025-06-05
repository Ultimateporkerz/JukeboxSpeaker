package net.ultimporks.betterdiscs.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.recipe.RecordLatheRecipe;
import net.ultimporks.betterdiscs.recipe.RecordPressRecipe;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Reference.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Reference.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RecordLatheRecipe>> RECORD_LATHE_STATION_SERIALIZER =
            SERIALIZERS.register("record_lathe_station", RecordLatheRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<RecordLatheRecipe>> RECORD_LATHE_TYPE =
            TYPES.register("record_lathe_station", () -> new RecipeType<RecordLatheRecipe>() {
                @Override
                public String toString() {
                    return "record_lathe_station";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RecordPressRecipe>> RECORD_PRESS_STATION_SERIALIZER =
            SERIALIZERS.register("record_press_station", RecordPressRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<RecordPressRecipe>> RECORD_PRESS_TYPE =
            TYPES.register("record_press_station", () -> new RecipeType<RecordPressRecipe>() {
                @Override
                public String toString() {
                    return "record_press_station";
                }
            });


    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
