package wutian.unlit.items.implementations;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wutian.unlit.blocks.implementations.JackOLantern;
import wutian.unlit.config.ConfigHandler;

public class JackOLanternItem extends BlockItem {
    public JackOLanternItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockPlaceContext pContext) {
        ItemStack pStack = pContext.getItemInHand();
        if(!pStack.getOrCreateTag().contains("burnTime"))
            pStack.getOrCreateTag().putInt("burnTime", ConfigHandler.torchBurnoutTime.get());
        if(!pStack.getOrCreateTag().contains("lit_state"))
            pStack.getOrCreateTag().putInt("lit_state",1);
        BlockState state = super.getPlacementState(pContext);
        if(state != null)
        {
            return state.setValue(JackOLantern.LIT_STATE,pStack.getOrCreateTag().getInt("lit_state")).setValue(JackOLantern.BURN_TIME,pContext.getItemInHand().getOrCreateTag().getInt("burnTime"));
        }
        return null;
    }
    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        //LogUtils.getLogger().debug(String.valueOf(oldStack.getOrCreateTag().getInt("burnTime") == oldStack.getOrCreateTag().getInt("burnTime")));
        if(oldStack.getOrCreateTag().contains("burnTime") && newStack.getOrCreateTag().contains("burnTime"))
        {
            return oldStack.getOrCreateTag().getInt("burnTime") != oldStack.getOrCreateTag().getInt("burnTime");
        }
        return false;
    }
}
