package wutian.unlit.items.implementations;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;
import wutian.unlit.blocks.ModBlocks;
import wutian.unlit.blocks.implementations.JackOLantern;
import wutian.unlit.blocks.implementations.LanternBlock;
import wutian.unlit.blocks.implementations.StandingTorchBlock;
import wutian.unlit.blocks.implementations.WallTorchBlock;
import wutian.unlit.config.ConfigHandler;

import java.util.List;
import java.util.Random;

public class FireStarterItem extends Item {

    public static final int USE_DURATION = 72000;
    public FireStarterItem() {
        super(new Properties().tab(CreativeModeTab.TAB_TOOLS).stacksTo(1).durability(10));
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
        BlockHitResult hit = pLevel.clip(new ClipContext(pLivingEntity.getEyePosition(), pLivingEntity.getEyePosition().add(pLivingEntity.getLookAngle().scale(pLivingEntity.getAttributeValue(ForgeMod.REACH_DISTANCE.get()))), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, pLivingEntity));
        BlockPos pos = hit.getBlockPos();
        Block block =pLevel.getBlockState(pos).getBlock();
        BlockState state = pLevel.getBlockState(pos);
        if(pLevel.isClientSide() || state.getMaterial() == Material.AIR) return;
        Boolean success = new Random().nextDouble() >=0.6;
        Boolean attempt = false;
        if(pTimeCharged <= USE_DURATION -15  && pLivingEntity instanceof Player) {
            boolean simulateFlintAndSteel = false;
            LogUtils.getLogger().debug(String.valueOf(block));
            if (block instanceof CampfireBlock) {
                attempt = true;
                if (success) simulateFlintAndSteel = true;
                else
                    pLevel.playSound(null,pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS,1,pLevel.random.nextFloat() * 0.1F + 0.3F);
            }
            else if(block instanceof JackOLantern)
            {
                attempt = true;
                if (success) {
                    pLevel.setBlockAndUpdate(pos,ModBlocks.JACK_O_LANTERN.get().defaultBlockState().setValue(LanternBlock.LIT_STATE, LanternBlock.LIT).setValue(LanternBlock.BURN_TIME,ConfigHandler.torchBurnoutTime.get()).setValue(JackOLantern.FACING,state.getValue(JackOLantern.FACING)));
                    pLevel.updateNeighborsAt(pos,state.getBlock());
                    pLevel.playSound(null,pos,SoundEvents.FLINTANDSTEEL_USE,SoundSource.PLAYERS,1,0.9f);
                }
                else
                    pLevel.playSound(null,pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS,1,pLevel.random.nextFloat() * 0.1F + 0.3F);
            }
            else if(block instanceof LanternBlock){
                attempt = true;
                if (success) {
                    if(state.getValue(LanternBlock.OIL)<=0) {
                        pLevel.playSound(null,pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS,1,pLevel.random.nextFloat() * 0.1F + 0.3F);
                        return;
                    }
                    pLevel.setBlockAndUpdate(pos,ModBlocks.LANTERN.get().defaultBlockState().setValue(LanternBlock.WATERLOGGED,state.getValue(LanternBlock.WATERLOGGED)).
                            setValue(LanternBlock.HANGING,state.getValue(LanternBlock.HANGING)).setValue(LanternBlock.LIT_STATE, LanternBlock.LIT).setValue(LanternBlock.BURN_TIME,ConfigHandler.lanternBurnOutTime.get()).setValue(LanternBlock.OIL,state.getValue(LanternBlock.OIL)));
                    pLevel.updateNeighborsAt(pos,state.getBlock());
                    pLevel.playSound(null,pos,SoundEvents.FLINTANDSTEEL_USE,SoundSource.PLAYERS,1,0.9f);
                }
                else
                    pLevel.playSound(null,pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS,1,pLevel.random.nextFloat() * 0.1F + 0.3F);

            } else if (block instanceof StandingTorchBlock) {
                if (state.getValue(StandingTorchBlock.LIT_STATE) != StandingTorchBlock.LIT) {
                    attempt = true;
                    if (success){
                        playLightingSound(pLevel,pos);
                        if(pLevel.isRainingAt(pos.above()))
                        {
                            if(state.is(ModBlocks.STANDING_TORCH.get()))
                            {
                                pLevel.setBlockAndUpdate(pos, ModBlocks.STANDING_TORCH.get().defaultBlockState().setValue(StandingTorchBlock.LIT_STATE,StandingTorchBlock.SMOULDERING).setValue(StandingTorchBlock.BURN_TIME,StandingTorchBlock.getInitialBurnTime()));
                                //pLevel.scheduleTick(pos,block,StandingTorchBlock.TICK_INTERVAL);
                            }
                            else if(state.is(ModBlocks.WALL_TORCH.get()))
                            {
                                pLevel.setBlockAndUpdate(pos, ModBlocks.WALL_TORCH.get().defaultBlockState().setValue(StandingTorchBlock.LIT_STATE,StandingTorchBlock.SMOULDERING).setValue(StandingTorchBlock.BURN_TIME,StandingTorchBlock.getInitialBurnTime()).setValue(WallTorchBlock.FACING,state.getValue(WallTorchBlock.FACING)));
                                //pLevel.scheduleTick(pos,block,StandingTorchBlock.TICK_INTERVAL);
                            }
                            pLevel.playSound(null,pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS,0.3f, pLevel.random.nextFloat() * 0.1F + 0.6F);
                        }
                        else {
                            LogUtils.getLogger().debug("aaa");
                            if(state.is(ModBlocks.STANDING_TORCH.get()))
                            {
                                pLevel.setBlockAndUpdate(pos, ModBlocks.STANDING_TORCH.get().defaultBlockState().setValue(StandingTorchBlock.LIT_STATE,StandingTorchBlock.LIT).setValue(StandingTorchBlock.BURN_TIME,StandingTorchBlock.getInitialBurnTime()));
                                //pLevel.scheduleTick(pos,block,StandingTorchBlock.TICK_INTERVAL);
                            }
                            else if(state.is(ModBlocks.WALL_TORCH.get()))
                            {
                                pLevel.setBlockAndUpdate(pos, ModBlocks.WALL_TORCH.get().defaultBlockState().setValue(StandingTorchBlock.LIT_STATE,StandingTorchBlock.LIT).setValue(StandingTorchBlock.BURN_TIME,StandingTorchBlock.getInitialBurnTime()).setValue(WallTorchBlock.FACING,state.getValue(WallTorchBlock.FACING)));
                                //pLevel.scheduleTick(pos,block,StandingTorchBlock.TICK_INTERVAL);
                            }
                        }
                        pLevel.updateNeighborsAt(pos,block);
                    }
                    else pLevel.playSound(null,pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS,1,pLevel.random.nextFloat() * 0.1F + 0.3F);
                }
            } //else if (block instanceof ) {
//                if (((AbstractLanternBlock) block).canLight(pLevel, pos)) {
//                    attempt = true;
//                    if (success) ((AbstractLanternBlock) block).light(pLevel, pos, pLevel.getBlockState(pos));
//                }
            else {
                attempt = true;
                if (success) {
                    simulateFlintAndSteel = true;
                }
                else {
                    pLevel.playSound(null,pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS,1,pLevel.random.nextFloat() * 0.1F + 0.3F);
                }
            }
            if (simulateFlintAndSteel)
            {
                playLightingSound(pLevel,pos);
                Items.FLINT_AND_STEEL.useOn(new UseOnContext((Player) pLivingEntity, pLivingEntity.getUsedItemHand(), hit));
            }
            if(pLivingEntity instanceof Player && !((Player) pLivingEntity).getAbilities().instabuild)
                pStack.setDamageValue(pStack.getDamageValue()+1);
        }
    }

    public void playLightingSound(Level pLevel,BlockPos pPos)
    {
        pLevel.playSound(null,pPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS,1,pLevel.random.nextFloat() * 0.1F + 1.2F);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        pPlayer.startUsingItem(pUsedHand);
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltips.fire_starter.help").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return USE_DURATION;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }
}
