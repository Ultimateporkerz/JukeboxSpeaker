package net.ultimporks.betterdiscs.init;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.recipe.RecordLatheRecipe;
import net.ultimporks.betterdiscs.recipe.RecordPressRecipe;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Reference.MOD_ID);

    public static final RegistryObject<RecipeSerializer<RecordLatheRecipe>> RECORD_LATHE_STATION_SERIALIZER =
            SERIALIZERS.register("record_lathe_station", () -> RecordLatheRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<RecordPressRecipe>> RECORD_PRESS_STATION_SERIALIZER =
            SERIALIZERS.register("record_press_station", () -> RecordPressRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
