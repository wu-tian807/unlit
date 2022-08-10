package wutian.unlit.items.implementations;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wutian.unlit.blocks.implementations.LanternBlock;
import wutian.unlit.config.ConfigHandler;

public class LitLantern extends BlockItem {

    public LitLantern(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public String getDescriptionId() {
        return "item.unlit.lit_lantern";
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockPlaceContext pContext) {
        ItemStack pStack = pContext.getItemInHand();
        if(!pStack.getOrCreateTag().contains("oil"))
            pStack.getOrCreateTag().putInt("oil",0);
        if(!pStack.getOrCreateTag().contains("burnTime"))
            pStack.getOrCreateTag().putInt("burnTime", ConfigHandler.lanternBurnOutTime.get());
        if(!pStack.getOrCreateTag().contains("lit_state"))
            pStack.getOrCreateTag().putInt("lit_state",1);
        BlockState state = super.getPlacementState(pContext);
        if(state != null)
        {
                return state.setValue(LanternBlock.LIT_STATE,pStack.getOrCreateTag().getInt("lit_state")).setValue(LanternBlock.BURN_TIME,pContext.getItemInHand().getOrCreateTag().getInt("burnTime")).setValue(LanternBlock.OIL,pContext.getItemInHand().getOrCreateTag().getInt("oil"));
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
