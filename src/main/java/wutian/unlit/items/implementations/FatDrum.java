package wutian.unlit.items.implementations;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.awt.*;

public class FatDrum extends Item {
    public FatDrum(Properties pProperties) {
        super(pProperties);
    }

    public static int getFuel(ItemStack pStack)
    {
        if(pStack.getOrCreateTag().contains("fat")) {
            return pStack.getOrCreateTag().getInt("fat");
        }
        pStack.getOrCreateTag().putInt("fat",0);
        return 0;

    }
    public static ItemStack setFuel(ItemStack pStack,int value)
    {
        pStack.getOrCreateTag().putInt("fat",value);
        return pStack;
    }
    public static void useFuel(ItemStack pStack)
    {
        if(getFuel(pStack)<=0) return;
        setFuel(pStack,getFuel(pStack) -1);
    }
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int fuel = ((FatDrum)(stack.getItem())).getFuel(stack);
        return Math.round(13.0f - (3 - fuel) * 13.0f / 3);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return Color.HSBtoRGB(0.5f, 1.0f, 1.0f);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        //LogUtils.getLogger().debug(String.valueOf(oldStack.getOrCreateTag().getInt("fat") == oldStack.getOrCreateTag().getInt("fat")));
        if(oldStack.getOrCreateTag().contains("fat") && newStack.getOrCreateTag().contains("fat"))
        {
            return oldStack.getOrCreateTag().getInt("fat") != oldStack.getOrCreateTag().getInt("fat");
        }
        return false;
    }
}
