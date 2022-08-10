package wutian.unlit.events;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import wutian.unlit.api.ItemStackAPI;
import wutian.unlit.blocks.ModBlocks;
import wutian.unlit.blocks.implementations.StandingTorchBlock;
import wutian.unlit.blocks.implementations.WallTorchBlock;
import wutian.unlit.config.ConfigHandler;
import wutian.unlit.items.ModItems;
import wutian.unlit.items.implementations.JackOLanternItem;
import wutian.unlit.items.implementations.LitLantern;
import wutian.unlit.items.implementations.LitTorchItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEventsHandler {

    private static final List<ChunkAccess> accesses = new ArrayList<>(5);
    private static int waitTime = 0;
    private static int rainingCheckTime = 0;

    public static final boolean HARDCORE = ConfigHandler.hardcore.get();

    public static final ItemStack[] stacks = new ItemStack[37];

    public static final int TICK_INTERVAL = 1200*3;

    public static final int INITIAL_BURN_TIME = ConfigHandler.torchBurnoutTime.get();

    @SubscribeEvent
    public static void rainingEvent(TickEvent.PlayerTickEvent event)
    {
        Level level = event.player.getLevel();
        Player player = event.player;
        BlockPos pos = event.player.getOnPos();
        if(level.isClientSide() || !level.isRaining()) return;
        if(rainingCheckTime <=20)
        {
            rainingCheckTime++;
            return;
        }
        for (int x=pos.getX() - 16;x< pos.getX() +16;x++)
        {
            for (int y= -64;y<level.getHeight();y++)
            {
                for (int z = pos.getZ() - 16;z<pos.getZ() + 16;z++)
                {
                    BlockPos newPos = new BlockPos(x,y,z);
                    BlockState state = level.getBlockState(newPos);
                    if(state.is(ModBlocks.STANDING_TORCH.get()) && state.getValue(StandingTorchBlock.LIT_STATE) == StandingTorchBlock.LIT)
                    {
                        if(level.isRainingAt(newPos.above()))
                        {
                            playRainingExtinguishSound(level,newPos);
                            changeToStandingSmoldering(level,newPos,state,state.getValue(StandingTorchBlock.BURN_TIME));
                            level.updateNeighborsAt(newPos,state.getBlock());
                            return;
                        }
                    }
                    else if(state.is(ModBlocks.WALL_TORCH.get()) && state.getValue(StandingTorchBlock.LIT_STATE) == StandingTorchBlock.LIT)
                    {
                        if(level.isRainingAt(newPos.above()))
                        {
                            playRainingExtinguishSound(level,newPos);
                            changeToWallSmoldering(level,newPos,state,state.getValue(StandingTorchBlock.BURN_TIME),state.getValue(WallTorchBlock.FACING));
                            level.updateNeighborsAt(newPos,state.getBlock());
                            return;
                        }
                    }
                }
            }
        }
        rainingCheckTime=0;
    }

    private static void changeToWallSmoldering(Level pLevel, BlockPos pPos, BlockState pState, int burnTime, Direction facing)
    {
        pLevel.setBlockAndUpdate(pPos,ModBlocks.WALL_TORCH.get().defaultBlockState().setValue(StandingTorchBlock.LIT_STATE,1).setValue(StandingTorchBlock.BURN_TIME,burnTime).setValue(WallTorchBlock.FACING,facing));
    }
    private static void changeToStandingSmoldering(Level pLevel, BlockPos pPos, BlockState pState, int burnTime)
    {
        pLevel.setBlockAndUpdate(pPos,ModBlocks.STANDING_TORCH.get().defaultBlockState().setValue(StandingTorchBlock.LIT_STATE,1).setValue(StandingTorchBlock.BURN_TIME,burnTime));
    }
    @SubscribeEvent
    public static void playerInventoryTick(TickEvent.PlayerTickEvent event)
    {
        if(!ConfigHandler.hardcore.get() || event.player.getLevel().isClientSide()) return;
        if(waitTime <= TICK_INTERVAL)
        {
            waitTime++;
            return;
        }
        Player player = event.player;
        Level pLevel = event.player.level;

        LogUtils.getLogger().debug(String.valueOf(player.getOffhandItem().getItem() instanceof JackOLanternItem));
        if(player.getOffhandItem().getItem() instanceof LitLantern)
        {
            ItemStack pStack = player.getOffhandItem();
            if(!ConfigHandler.hardcore.get() || pLevel.isClientSide()) return;
            if(!pStack.getOrCreateTag().contains("oil"))
                pStack.getOrCreateTag().putInt("oil",0);
            LogUtils.getLogger().debug(String.valueOf(pStack.getOrCreateTag().contains("burnTime")));
            if(!pStack.getOrCreateTag().contains("burnTime"))
                pStack.getOrCreateTag().putInt("burnTime",ConfigHandler.lanternBurnOutTime.get());
            if(!pStack.getOrCreateTag().contains("lit_state"))
                pStack.getOrCreateTag().putInt("lit_state",1);
            if(pStack.getOrCreateTag().getInt("burnTime")>0)
            {
                pStack.getOrCreateTag().putInt("burnTime",pStack.getOrCreateTag().getInt("burnTime") - 1 );
            }
            else
            {
                if(pStack.getOrCreateTag().getInt("oil") > 0)
                {
                    pStack.getOrCreateTag().putInt("burnTime", ConfigHandler.lanternBurnOutTime.get());
                    pStack.getOrCreateTag().putInt("oil",pStack.getOrCreateTag().getInt("oil") - 1 );
                }
                else
                    changeItem(player,pStack,ItemStackAPI.replaceItemWithCopyNBTTagAndCountButResetBurnTime(pStack, ModItems.UNLIT_LANTERN.get(),0),0);
            }
        }
        else if(player.getOffhandItem().getItem() instanceof LitLantern)
        {
            ItemStack pStack = player.getOffhandItem();
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

//        if(waitTime<StandingTorchBlock.TICK_INTERVAL)
//        {
//            LogUtils.getLogger().debug(String.valueOf(waitTime));
//            waitTime++;
//        }
//        else
//        {
//            waitTime=0;
            burnTime -= 1;
            //LogUtils.getLogger().debug(String.valueOf("BURNTIME:"+burnTime));
            pStack.getTag().putInt("burnTime",burnTime);
            //LogUtils.getLogger().debug("BURNTIMEINTAG:"+pStack.getTag().getInt("burnTime"));
//            LogUtils.getLogger().debug(String.valueOf(pLevel.isRainingAt(player.getOnPos().above(2)) && pIsSelected));
            if(burnTime <=0)
            {
                changeItem(player,pStack,ItemStackAPI.replaceItemWithCopyNBTTagAndCount(pStack,ModItems.UNLIT_TORCH.get()),0);
                pLevel.playSound(null,player.getOnPos(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS,1, pLevel.random.nextFloat() * 0.1F + 0.9F);
            }
        }
        else if(player.getOffhandItem().getItem() instanceof JackOLanternItem)
        {
            ItemStack pStack = player.getOffhandItem();
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

            burnTime -= 1;
            pStack.getTag().putInt("burnTime",burnTime);
            if(burnTime <=0)
            {
                changeItem(player,pStack,ItemStackAPI.replaceItemWithCopyNBTTagAndCount(pStack, Items.CARVED_PUMPKIN),0);
                pLevel.playSound(null,player.getOnPos(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS,1, pLevel.random.nextFloat() * 0.1F + 0.9F);
            }
        }

        for(int i=0;i<player.getInventory().getContainerSize();i++)
        {
            if(player.getInventory().getItem(i).getItem() instanceof LitLantern)
            {
                LogUtils.getLogger().debug("TimeToLantern");
                ItemStack pStack = player.getInventory().getItem(i);
                if(!ConfigHandler.hardcore.get() || pLevel.isClientSide()) return;
                if(!pStack.getOrCreateTag().contains("oil"))
                    pStack.getOrCreateTag().putInt("oil",0);
                LogUtils.getLogger().debug(String.valueOf(pStack.getOrCreateTag().contains("burnTime")));
                if(!pStack.getOrCreateTag().contains("burnTime"))
                    pStack.getOrCreateTag().putInt("burnTime",ConfigHandler.lanternBurnOutTime.get());
                if(!pStack.getOrCreateTag().contains("lit_state"))
                    pStack.getOrCreateTag().putInt("lit_state",1);
                if(pStack.getOrCreateTag().getInt("burnTime")>0)
                {
                    pStack.getOrCreateTag().putInt("burnTime",pStack.getOrCreateTag().getInt("burnTime") - 1 );
                }
                else
                {
                    if(pStack.getOrCreateTag().getInt("oil") > 0)
                    {
                        pStack.getOrCreateTag().putInt("burnTime", ConfigHandler.lanternBurnOutTime.get());
                        pStack.getOrCreateTag().putInt("oil",pStack.getOrCreateTag().getInt("oil") - 1 );
                    }
                    else
                        changeItem(player,pStack,ItemStackAPI.replaceItemWithCopyNBTTagAndCountButResetBurnTime(pStack, ModItems.UNLIT_LANTERN.get(),0),i);
                }
            }
            else if(player.getInventory().getItem(i).getItem() instanceof LitTorchItem) {
                ItemStack pStack = player.getInventory().getItem(i);
                int burnTime;
                if (!pStack.getOrCreateTag().contains("burnTime")) {
                    burnTime = StandingTorchBlock.getInitialBurnTime();
                    pStack.getTag().putInt("burnTime", burnTime);
                } else {
                    burnTime = pStack.getTag().getInt("burnTime");
                }

//        if(waitTime<StandingTorchBlock.TICK_INTERVAL)
//        {
//            LogUtils.getLogger().debug(String.valueOf(waitTime));
//            waitTime++;
//        }
//        else
//        {
//            waitTime=0;
                burnTime -= 1;
                //LogUtils.getLogger().debug(String.valueOf("BURNTIME:"+burnTime));
                pStack.getTag().putInt("burnTime", burnTime);
                //LogUtils.getLogger().debug("BURNTIMEINTAG:"+pStack.getTag().getInt("burnTime"));
//            LogUtils.getLogger().debug(String.valueOf(pLevel.isRainingAt(player.getOnPos().above(2)) && pIsSelected));
                if (burnTime <= 0) {
                    changeItem(player, pStack, ItemStackAPI.replaceItemWithCopyNBTTagAndCount(pStack, ModItems.UNLIT_TORCH.get()), i);
                    pLevel.playSound(null, player.getOnPos(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1, pLevel.random.nextFloat() * 0.1F + 0.9F);
                }
            }
            else if(player.getInventory().getItem(i).getItem() instanceof JackOLanternItem)
            {
                ItemStack pStack = player.getInventory().getItem(i);
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

                burnTime -= 1;
                pStack.getTag().putInt("burnTime",burnTime);
                if(burnTime <=0)
                {
                    changeItem(player,pStack,ItemStackAPI.replaceItemWithCopyNBTTagAndCount(pStack, Items.CARVED_PUMPKIN),0);
                    pLevel.playSound(null,player.getOnPos(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS,1, pLevel.random.nextFloat() * 0.1F + 0.9F);
                }
            }
        }
        waitTime=0;
    }


    public static void changeItem(Player player,ItemStack stack,ItemStack newStack,int slot)
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

    @SubscribeEvent
    public static void PlayerTickEventForTorchChange(TickEvent.PlayerTickEvent event)
    {
        if(!HARDCORE || event.player.getLevel().isClientSide()) return;
        if(waitTime <= TICK_INTERVAL)
        {
            //LogUtils.getLogger().debug(String.valueOf(waitTime));
            waitTime++;
            return;
        }
        torchChangeOnWorld(event);
        torchChangeOnBlockEntity(event);
        waitTime=0;
    }

    public static void torchChangeOnBlockEntity(TickEvent.PlayerTickEvent event)
    {
        ChunkAccess chunk = event.player.getLevel().getChunk(event.player.getOnPos());
        if(!accesses.contains(chunk)) accesses.add(chunk);
        accesses.forEach((eachChunk) ->{
            Set<BlockPos> blockPosSET  = eachChunk.getBlockEntitiesPos();
            blockPosSET.forEach(blockPos -> {
                Level level = event.player.level;
                BlockEntity blockEntity =level.getBlockEntity(blockPos);
                if(blockEntity instanceof BaseContainerBlockEntity container)
                {
                    ItemStack stack;
                    for(int i=0;i<container.getContainerSize();i++)
                    {
                        stack = container.getItem(i);
                        if(stack.is(ModItems.LIT_TORCH.get()))
                        {
                            if(stack.getOrCreateTag().contains("burnTime"))
                            {
                                if(stack.getTag().getInt("burnTime") <= 0)
                                {
                                    LogUtils.getLogger().debug("2");
                                    container.setItem(i,ItemStackAPI.replaceItemWithCopyNBTTagAndCount(stack,ModItems.UNLIT_TORCH.get()));
                                }
                                else
                                {
                                    LogUtils.getLogger().debug("1");
                                    stack.getTag().putInt("burnTime",stack.getTag().getInt("burnTime") -1 );
                                }
                            }
                            else
                                stack.getTag().putInt("burnTime",INITIAL_BURN_TIME);
                        }
                    }
                }
            });
        });
    }
    public static void torchChangeOnWorld(TickEvent.PlayerTickEvent event)
    {
        //LogUtils.getLogger().debug(String.valueOf("ab"));
        Player player = event.player;
        List<Entity> entities = player.getLevel().getEntities(null,new AABB(player.getBlockX()-10,player.getBlockY()-10,player.getBlockZ()-10,player.getBlockX()+10,player.getBlockY()+10,player.getBlockZ()+10));
        entities.forEach((entity -> {
            if(entity instanceof ItemEntity itemEntity)
            {
                LogUtils.getLogger().debug(String.valueOf(entity));
                if(!itemEntity.getItem().is(ModItems.LIT_TORCH.get())) return;
                if(!itemEntity.getItem().getOrCreateTag().contains("burnTime"))
                {
                    itemEntity.getItem().getOrCreateTag().putInt("burnTime",ConfigHandler.torchBurnoutTime.get());
                }
                else
                {
                    itemEntity.getItem().getTag().putInt("burnTime",itemEntity.getItem().getTag().getInt("burnTime")-1);
                }
                LogUtils.getLogger().debug(String.valueOf(itemEntity.getItem().getTag().getInt("burnTime")));
                if(itemEntity.getItem().getTag().getInt("burnTime") <= 0)
                {
                    itemEntity.setItem(ItemStackAPI.replaceItemWithCopyNBTTagAndCount(itemEntity.getItem(), ModItems.UNLIT_TORCH.get()));
                    playRainingExtinguishSound(itemEntity.getLevel(),itemEntity.getOnPos());
                }
                if(itemEntity.getLevel().isRainingAt(itemEntity.getOnPos().above(2)))
                {
                    itemEntity.setItem(ItemStackAPI.replaceItemWithCopyNBTTagAndCount(itemEntity.getItem(), ModItems.UNLIT_TORCH.get()));
                    playRainingExtinguishSound(itemEntity.getLevel(),itemEntity.getOnPos());
                }
            }
        }));
    }
    public static void playRainingExtinguishSound(Level pLevel, BlockPos pPos)
    {
        pLevel.playSound(null,pPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS,0.3f, pLevel.random.nextFloat() * 0.1F + 0.6F);
    }
}
