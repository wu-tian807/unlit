package wutian.unlit.recipes;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import wutian.unlit.Unlit;
import wutian.unlit.items.implementations.FatDrum;

public class FatDrumRecipe extends ShapelessRecipe {
    final int fatAmount;

    public FatDrumRecipe(ResourceLocation pId, String pGroup, ItemStack pResult, NonNullList<Ingredient> pIngredients,int fatAmount) {
        super(pId, pGroup, pResult, pIngredients);
        this.fatAmount = fatAmount;
    }

    @Override
    public ItemStack assemble(CraftingContainer pInv) {
        int startFat;
        for(int i= 0;i<pInv.getContainerSize();i++)
        {
            ItemStack pStack = pInv.getItem(i);

            if(pStack.getItem() instanceof FatDrum item)
            {
                startFat = FatDrum.getFuel(pStack);
                if(startFat >=3) return ItemStack.EMPTY;
                return FatDrum.setFuel(pStack.copy(),(int)(startFat + fatAmount));
            }
        }
        return ItemStack.EMPTY;
    }

    public static class Serializer implements RecipeSerializer<FatDrumRecipe>
    {
        private static final ResourceLocation NAME = new ResourceLocation(Unlit.MODID,"fat_drum");

        public FatDrumRecipe fromJson(ResourceLocation resourceLocation, JsonObject json)
        {
            int fat;
            ShapelessRecipe recipe = ShapelessRecipe.Serializer.SHAPELESS_RECIPE.fromJson(resourceLocation,json);
            try {
                fat = json.get("fat").getAsInt();
            }
            catch (NullPointerException e)
            {
                return null;
            }

            return new FatDrumRecipe(recipe.getId(),recipe.getGroup(),recipe.getResultItem(),recipe.getIngredients(),fat);
        }

        public FatDrumRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf)
        {
            ShapelessRecipe recipe = ShapelessRecipe.Serializer.SHAPELESS_RECIPE.fromNetwork(resourceLocation,friendlyByteBuf);

            int fat = friendlyByteBuf.readVarInt();
            return  new FatDrumRecipe(recipe.getId(),recipe.getGroup(),recipe.getResultItem(),recipe.getIngredients(),fat);
        }

        public void toNetwork(FriendlyByteBuf buf,FatDrumRecipe recipe)
        {
            ShapelessRecipe shapelessRecipe = new ShapelessRecipe(recipe.getId(),recipe.getGroup(),recipe.getResultItem(),recipe.getIngredients());
            ShapelessRecipe.Serializer.SHAPELESS_RECIPE.toNetwork(buf,shapelessRecipe);

            buf.writeVarInt(recipe.fatAmount);
        }
    }
}
