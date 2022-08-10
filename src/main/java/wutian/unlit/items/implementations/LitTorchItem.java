package wutian.unlit.items.implementations;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wutian.unlit.api.ItemStackAPI;
import wutian.unlit.blocks.ModBlocks;
import wutian.unlit.blocks.implementations.StandingTorchBlock;
import wutian.unlit.config.ConfigHandler;
import wutian.unlit.items.ModItems;
import wutian.unlit.util.ModTags;

public class LitTorchItem extends StandingAndWallBlockItem {

    public static final boolean HARDCORE = ConfigHandler.hardcore.get();

    public static final Boolean WATER_BURNT = ConfigHandler.waterBurnt.get();
    protected int waitTime;
    public LitTorchItem() {
        super(ModBlocks.STANDING_TORCH.get(),ModBlocks.WALL_TORCH.get(),new Item.Properties().stacksTo(64));
    }

    @Override
    public String getDescriptionId() {
        return "item.unlit.lit_torch";
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockPlaceContext pContext) {
        ItemStack pStack = pContext.getItemInHand();
        int burnTime;
        if(!pStack.getOrCreateTag().contains("burnTime"))
        {
            burnTime = StandingTorchBlock.getInitialBurnTime();
            pStack.getTag().putInt("burnTime",burnTime);
        }
        else
        {
            burnTime = pStack.getTag().getInt("burnTime");
        }
        BlockState state = super.getPlacementState(pContext);
        if(state != null)
        {
            if(pContext.getLevel().isRainingAt(pContext.getClickedPos().above()))
            {
                pContext.getLevel().playSound(null,pContext.getClickedPos(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS,0.3f, pContext.getLevel().random.nextFloat() * 0.1F + 0.6F);
                return state.setValue(StandingTorchBlock.getLitState(),StandingTorchBlock.SMOULDERING).setValue(StandingTorchBlock.BURN_TIME,burnTime);
            }
            else
                return state.setValue(StandingTorchBlock.getLitState(),StandingTorchBlock.LIT).setValue(StandingTorchBlock.BURN_TIME,burnTime);
        }
        return null;
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if(!HARDCORE || pLevel.isClientSide() || !(pEntity instanceof Player player)) return;
//        }
        if(pLevel.isRainingAt(player.getOnPos().above(2)) && pIsSelected)
        {
            changeTorch(player,pStack,ItemStackAPI.replaceItemWithCopyNBTTagAndCount(pStack,ModItems.UNLIT_TORCH.get()),pSlotId);
            pLevel.playSound(null,player.getOnPos(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS,0.3f, pLevel.random.nextFloat() * 0.1F + 0.6F);
        }
        if(inWater(player.getOnPos(),pLevel) && WATER_BURNT)
        {
            changeTorch(player,pStack,ItemStackAPI.replaceItemWithCopyNBTTagAndCount(pStack,ModItems.UNLIT_TORCH.get()),pSlotId);
            pLevel.playSound(null,player.getOnPos(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS,0.3f, pLevel.random.nextFloat() * 0.1F + 0.6F);
        }
    }

    public static boolean inWater(BlockPos pos,Level level)
    {
        return level.getBlockState(pos).is(ModTags.Blocks.TORCH_CAN_BE_BURNT_OUT);
    }

    public static void changeTorch(Player player,ItemStack stack,ItemStack newStack,int slot)
    {
        if(player.getItemInHand(InteractionHand.MAIN_HAND) == stack)
        {
            EquipmentSlot pSlot = EquipmentSlot.MAINHAND;
            player.setItemSlot(pSlot,newStack);
            return;
        }
        else if(player.getItemInHand(InteractionHand.OFF_HAND) == stack )
        {
            player.setItemSlot(EquipmentSlot.OFFHAND,newStack);
            return;
        }
        player.getInventory().setItem(slot,newStack);
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
