package wutian.unlit.recipes;

import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import wutian.unlit.Unlit;

public class ModRecipes {
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPE_DEFERRED_REGISTER = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, Unlit.MODID);
    public static final RegistryObject<RecipeType<FatDrumRecipe>> FAT_DRUM_RECIPE = RECIPE_TYPE_DEFERRED_REGISTER.register("fat_drum", () -> new RecipeType<>() {});

    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER_DEFERRED_REGISTER = DeferredRegister.create(Registry.RECIPE_SERIALIZER_REGISTRY, Unlit.MODID);
    private static final RegistryObject<FatDrumRecipe.Serializer> FAT_DRUM_RECIPE_SERIALIZER = RECIPE_SERIALIZER_DEFERRED_REGISTER.register("fat_drum", FatDrumRecipe.Serializer::new);

    public static void register(IEventBus eventBus)
    {
        RECIPE_TYPE_DEFERRED_REGISTER.register(eventBus);
        RECIPE_SERIALIZER_DEFERRED_REGISTER.register(eventBus);
    }
}
