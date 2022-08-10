package wutian.unlit.blocks.implementations;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import wutian.unlit.api.ItemStackAPI;
import wutian.unlit.blocks.ModBlocks;
import wutian.unlit.config.ConfigHandler;
import wutian.unlit.items.ModItems;

import java.util.Random;
import java.util.function.ToIntFunction;

public class StandingTorchBlock extends TorchBlock {

    public static final int TICK_INTERVAL = 1200;

    protected static final int INITIAL_BURN_TIME = ConfigHandler.torchBurnoutTime.get();

    protected static final boolean CAUSE_FIRE = ConfigHandler.torchCauseFire.get();

    protected static final boolean SHOULD_BURN_OUT = INITIAL_BURN_TIME >= 0;

    public static final IntegerProperty LIT_STATE = IntegerProperty.create("lit_state", 0, 2);
    public static final IntegerProperty BURN_TIME = IntegerProperty.create("burn_time",0,SHOULD_BURN_OUT ? INITIAL_BURN_TIME:1);


    public static final int LIT = 2;
    public static final int SMOULDERING = 1;
    public static final int UNLIT = 0;

    public StandingTorchBlock() {
        super(BlockBehaviour.Properties.of(Material.ICE).noCollission().instabreak().lightLevel(getLightLevelFromState()), ParticleTypes.FLAME);
        this.registerDefaultState(this.defaultBlockState().setValue(LIT_STATE, 0).setValue(BURN_TIME,0));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource source) {
        if (state.getValue(LIT_STATE) == LIT && level.getRandom().nextInt(2) == 1) {
            super.animateTick(state, level, pos, source);
        }
        else if(state.getValue(LIT_STATE) == SMOULDERING)
        {
            double d0 = (double)pos.getX() + 0.5D;
            double d1 = (double)pos.getY() + 0.7D;
            double d2 = (double)pos.getZ() + 0.5D;
            level.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
        //LogUtils.getLogger().debug(String.valueOf("mark2"));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        //LogUtils.getLogger().debug("use");
        if (pPlayer.getItemInHand(pHand).getItem() == Items.FLINT_AND_STEEL) {
            this.playLightingSound(pLevel, pPos);
            if (!pPlayer.isCreative()) {
                ItemStack heldStack = pPlayer.getItemInHand(pHand);
                heldStack.setDamageValue(1);
                if (pLevel.isRainingAt(pPos.above())) {
                    changeToSmoldering(pLevel,pPos,pState,getInitialBurnTime());
                    playExtinguishSound(pLevel, pPos);
                } else {
                    if(pLevel.isRainingAt(pPos.above()))
                    {
                        changeToSmoldering(pLevel,pPos,pState,getInitialBurnTime());
                        playExtinguishSound(pLevel, pPos);
                    }
                    else
                    {
                        changeToLit(pLevel, pPos, pState);
                        playLightingSound(pLevel,pPos);
                    }
                    pLevel.updateNeighborsAt(pPos,this);
                }
            }
            else
            {
                if(pLevel.isRainingAt(pPos.above()))
                {
                    changeToSmoldering(pLevel,pPos,pState,getInitialBurnTime());
                    playExtinguishSound(pLevel,pPos);
                }
                else
                {
                    changeToLit(pLevel, pPos, pState);
                }
                pLevel.updateNeighborsAt(pPos,this);
                //LogUtils.getLogger().debug(String.valueOf("mark3"));
            }
            return InteractionResult.SUCCESS;
        }
        else if (pPlayer.getItemInHand(pHand).getItem() == ModItems.MATCHBOX.get()) {
            this.playLightingSound(pLevel, pPos);
            if (!pPlayer.isCreative()) {
                ItemStack heldStack = pPlayer.getItemInHand(pHand);
                heldStack.shrink(1);
                if (pLevel.isRainingAt(pPos.above())) {
                    changeToSmoldering(pLevel,pPos,pState,getInitialBurnTime());
                    playExtinguishSound(pLevel, pPos);
                } else {
                    if(pLevel.isRainingAt(pPos.above()))
                    {
                        changeToSmoldering(pLevel,pPos,pState,getInitialBurnTime());
                        playExtinguishSound(pLevel, pPos);
                    }
                    else
                    {
                        changeToLit(pLevel, pPos, pState);
                        playLightingSound(pLevel,pPos);
                    }
                    pLevel.updateNeighborsAt(pPos,this);
                }
            }
            else
            {
                if(pLevel.isRainingAt(pPos.above()))
                {
                    changeToSmoldering(pLevel,pPos,pState,getInitialBurnTime());
                    playExtinguishSound(pLevel,pPos);
                }
                else
                {
                    changeToLit(pLevel, pPos, pState);
                }
                pLevel.updateNeighborsAt(pPos,this);
                //LogUtils.getLogger().debug(String.valueOf("mark3"));
            }
            return InteractionResult.SUCCESS;
        }
        else if( pState.getValue(LIT_STATE) == 2 && pPlayer.getItemInHand(pHand).is(ModItems.UNLIT_TORCH.get()))
        {
            pPlayer.setItemInHand(pHand, ItemStackAPI.replaceItemWithCopyNBTTagAndCountButResetBurnTime(pPlayer.getItemInHand(pHand),ModItems.LIT_TORCH.get(),INITIAL_BURN_TIME));
            return InteractionResult.SUCCESS;
        }
        //LogUtils.getLogger().debug(String.valueOf("mark4"));
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
//        LogUtils.getLogger().debug(String.valueOf(pState.getValue(LIT_STATE) != UNLIT));
        return CAUSE_FIRE &&pState.getValue(LIT_STATE) == LIT  && new Random().nextInt(9) == 0;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        //LogUtils.getLogger().debug("Fire");

        if(pos.getY() == level.getHeight() -2 ) return;
        if(level.getBlockState(pos.above()).getMaterial() == Material.WOOD || level.getBlockState(pos.above()).getMaterial() == Material.WOOL || level.getBlockState(pos.above()).getMaterial() == Material.LEAVES)
        {
            //LogUtils.getLogger().debug("hello1");
            if(level.getBlockState(new BlockPos(pos.getX(),pos.getY()+2,pos.getZ())).getMaterial() == Material.WOOD||level.getBlockState(new BlockPos(pos.getX(),pos.getY()+2,pos.getZ())).getMaterial() == Material.WOOL||level.getBlockState(new BlockPos(pos.getX(),pos.getY()+2,pos.getZ())).getMaterial() == Material.LEAVES || level.getBlockState(new BlockPos(pos.getX(),pos.getY()+2,pos.getZ())).getMaterial() == Material.AIR)
                level.setBlockAndUpdate(new BlockPos(pos.getX(),pos.getY()+2,pos.getZ()),Blocks.FIRE.defaultBlockState());
            else
                level.setBlockAndUpdate(pos.above(),Blocks.AIR.defaultBlockState());
        }
        else if(level.getBlockState(pos.above()).getMaterial() == Material.AIR)
        {
            //LogUtils.getLogger().debug("hello2");
            if(level.getBlockState(new BlockPos(pos.getX(),pos.getY()+2,pos.getZ())).getMaterial() != Material.AIR)
                level.setBlockAndUpdate(pos.above(),Blocks.FIRE.defaultBlockState().setValue(FireBlock.UP,true));
        }

    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        LogUtils.getLogger().debug("ab?");
        if(!level.isClientSide() && SHOULD_BURN_OUT && state.getValue(LIT_STATE) > UNLIT)
        {
            int newBurnTime = state.getValue(BURN_TIME) -1;
            //LogUtils.getLogger().debug(String.valueOf(level.isRainingAt(pos.above())));
            if(level.isRainingAt(pos.above()))
            {
                LogUtils.getLogger().debug("is Rainning");
                playRainingExtinguishSound(level,pos);
                newBurnTime -= random.nextInt(20,35);
                //LogUtils.getLogger().debug(String.valueOf(newBurnTime));
                if(newBurnTime <= 0)
                    changeToUnlit(level,pos,state);
                else
                    changeToSmoldering(level,pos,state,newBurnTime);
                level.updateNeighborsAt(pos,this);
                return;
            }
            if(newBurnTime <= 0)
            {
                playExtinguishSound(level,pos);
                changeToUnlit(level,pos,state);
                level.updateNeighborsAt(pos,this);
            }
            else if(state.getValue(LIT_STATE) == LIT &&(newBurnTime <= INITIAL_BURN_TIME / 10 || newBurnTime <=1))
            {
                changeToSmoldering(level,pos,state,newBurnTime);
                level.updateNeighborsAt(pos,this);
            }
            else {
                level.setBlockAndUpdate(pos,state.setValue(BURN_TIME,state.getValue(BURN_TIME) -1 ));
                //level.scheduleTick(pos,this,TICK_INTERVAL);
            }
            //LogUtils.getLogger().debug(String.valueOf(newBurnTime));
        }
        //LogUtils.getLogger().debug(String.valueOf("mark5"));
    }


    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if(!pIsMoving && pState.getBlock() != pOldState.getBlock())
        {
            defaultBlockState().updateIndirectNeighbourShapes(pLevel,pPos,3);
        }
        if(SHOULD_BURN_OUT&&pState.getBlock() instanceof StandingTorchBlock&&pState.getValue(LIT_STATE) > UNLIT)
            pLevel.scheduleTick(pPos,this,TICK_INTERVAL);
        LogUtils.getLogger().debug("place");
        super.onPlace(pState,pLevel,pPos,pOldState,pIsMoving);
    }

    public static IntegerProperty getBurnTime() {
        return BURN_TIME;
    }

    public static IntegerProperty getLitState() {
        return LIT_STATE;
    }

    public static int getInitialBurnTime() {
        return SHOULD_BURN_OUT ? INITIAL_BURN_TIME : 0;
    }
    public void changeToLit(Level pLevel, BlockPos pPos, BlockState pState)
    {
        pLevel.setBlockAndUpdate(pPos, ModBlocks.STANDING_TORCH.get().defaultBlockState().setValue(LIT_STATE,2).setValue(BURN_TIME,INITIAL_BURN_TIME));
        if(SHOULD_BURN_OUT)
        {
            //pLevel.scheduleTick(pPos,this,TICK_INTERVAL);
        }
    }
    public void changeToSmoldering(Level pLevel, BlockPos pPos, BlockState pState, int burnTime)
    {
        pLevel.setBlockAndUpdate(pPos,ModBlocks.STANDING_TORCH.get().defaultBlockState().setValue(LIT_STATE,1).setValue(BURN_TIME,burnTime));
        if(SHOULD_BURN_OUT)
        {
            //pLevel.scheduleTick(pPos,this,TICK_INTERVAL);
        }
    }
    public void changeToUnlit(Level pLevel,BlockPos pPos,BlockState pState)
    {
        pLevel.setBlockAndUpdate(pPos,ModBlocks.STANDING_TORCH.get().defaultBlockState());
        //pLevel.scheduleTick(pPos,this,TICK_INTERVAL);
    }
    public void playExtinguishSound(Level pLevel,BlockPos pPos)
    {
        pLevel.playSound(null,pPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS,1, pLevel.random.nextFloat() * 0.1F + 0.9F);
    }
    public void playRainingExtinguishSound(Level pLevel,BlockPos pPos)
    {
        pLevel.playSound(null,pPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS,0.3f, pLevel.random.nextFloat() * 0.1F + 0.6F);
    }
    private void playLightingSound(Level pLevel,BlockPos pPos)
    {
        pLevel.playSound(null,pPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS,1, pLevel.random.nextFloat() * 0.1F + 0.9F);
    }
    public static ToIntFunction<BlockState> getLightLevelFromState()
    {
        return (state) ->{
            if(state.getValue(StandingTorchBlock.LIT_STATE) == 2)
            {
                return 14;
            }
            else if(state.getValue(StandingTorchBlock.LIT_STATE) == 1)
            {
                return 8;
            }
            else
            {
                return 0;
            }
        };
    }
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state =Blocks.TORCH.getStateForPlacement(pContext);
        ItemStack placeStack = pContext.getPlayer().getItemInHand(pContext.getHand());
        if(!placeStack.is(ModItems.LIT_TORCH.get())) return state == null ? null:this.defaultBlockState();
        if(placeStack.getOrCreateTag().contains("burnTime"))
        {
            int burnTime = placeStack.getTag().getInt("burnTime");
            if(pContext.getLevel().isRainingAt(pContext.getClickedPos().above()))
            {
                if(burnTime > INITIAL_BURN_TIME)
                {
                    return  state == null ? null:this.defaultBlockState().setValue(BURN_TIME,INITIAL_BURN_TIME).setValue(LIT_STATE,1);
                }
                else if(burnTime <= 0)
                {
                    return  state == null ? null:this.defaultBlockState();
                }
                else
                {
                    return state == null ? null:this.defaultBlockState().setValue(BURN_TIME,burnTime).setValue(LIT_STATE,1);
                }
            }
            if(burnTime > INITIAL_BURN_TIME)
            {
                return  state == null ? null:this.defaultBlockState().setValue(BURN_TIME,INITIAL_BURN_TIME).setValue(LIT_STATE,2);
            }
            else if(burnTime <= 0)
            {
                return  state == null ? null:this.defaultBlockState();
            }
            else
            {
                return state == null ? null:this.defaultBlockState().setValue(BURN_TIME,burnTime).setValue(LIT_STATE,2);
            }

        }
        else
        {
            if(pContext.getLevel().isRainingAt(pContext.getClickedPos().above()))
            {
                return  state == null ? null:this.defaultBlockState().setValue(BURN_TIME,INITIAL_BURN_TIME).setValue(LIT_STATE,1);
            }
            return state == null ? null:this.defaultBlockState().setValue(BURN_TIME,INITIAL_BURN_TIME).setValue(LIT_STATE,2);
        }
    }

    //1.18+ new need
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
           pBuilder.add(LIT_STATE,BURN_TIME);
    }
}
