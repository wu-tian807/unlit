package wutian.unlit.blocks.implementations;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import wutian.unlit.blocks.ModBlocks;
import wutian.unlit.config.ConfigHandler;
import wutian.unlit.items.ModItems;

public class JackOLantern extends CarvedPumpkinBlock {


    public static final int LIT = 1;
    public static final int UNLIT = 0;
    public static final int TICK_INTERVAL = 1200;
    private static final int TOTAL_BURN_TIME = ConfigHandler.torchBurnoutTime.get();
    protected static final boolean SHOUD_BURN_OUT = TOTAL_BURN_TIME >= 0;
    public static final IntegerProperty BURN_TIME = IntegerProperty.create("burn_time",0,SHOUD_BURN_OUT ? TOTAL_BURN_TIME:1);
    public static final IntegerProperty LIT_STATE = IntegerProperty.create("lit_state",0,1);

    public JackOLantern() {
        super(BlockBehaviour.Properties.of(Material.METAL).strength(1F).sound(SoundType.WOOD).lightLevel((state) -> {
            return getLitState(state);
        }).isValidSpawn((a,b,c,d) -> {return true;}));
        registerDefaultState(defaultBlockState().setValue(LIT_STATE,1).setValue(BURN_TIME,0));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pPlayer.getItemInHand(pHand).getItem() == Items.FLINT_AND_STEEL) {
            return useAsFlint(pState,pLevel,pPos,pPlayer,pHand);
        }

        else if (pPlayer.getItemInHand(pHand).getItem() == ModItems.MATCHBOX.get()) {
            return useAsMatchbox(pState,pLevel,pPos,pPlayer,pHand);
        }
        return super.use(pState,pLevel,pPos,pPlayer,pHand,pHit);
    }

    private InteractionResult useAsMatchbox(BlockState pState,Level pLevel,BlockPos pPos, Player pPlayer,InteractionHand pHand)
    {
        replaceJackOLantern(pPos,pLevel,pState,TOTAL_BURN_TIME,LIT,pState.getValue(FACING));
        pLevel.updateNeighborsAt(pPos,this);
        pLevel.playSound(pPlayer,pPos,SoundEvents.FLINTANDSTEEL_USE,SoundSource.PLAYERS,1,0.9f);
        if(!pPlayer.isCreative())
            pPlayer.getItemInHand(pHand).shrink(1);
        return InteractionResult.SUCCESS;
    }

    private InteractionResult useAsFlint(BlockState pState,Level pLevel,BlockPos pPos, Player pPlayer,InteractionHand pHand)
    {
        replaceJackOLantern(pPos,pLevel,pState,TOTAL_BURN_TIME,LIT,pState.getValue(FACING));
        pLevel.updateNeighborsAt(pPos,this);
        pLevel.playSound(pPlayer,pPos,SoundEvents.FLINTANDSTEEL_USE,SoundSource.PLAYERS,1,0.9f);
        if(!pPlayer.isCreative())
            pPlayer.getItemInHand(pHand).setDamageValue(pPlayer.getItemInHand(pHand).getDamageValue() + 1);
        return InteractionResult.SUCCESS;
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        LogUtils.getLogger().debug(String.valueOf(state.getValue(BURN_TIME)+"    "+state.getValue(BURN_TIME)));
        if(!level.isClientSide() && SHOUD_BURN_OUT && state.getValue(LIT_STATE) > UNLIT)
        {
            int newBurnTime = state.getValue(BURN_TIME) -1;

            if(state.getValue(BURN_TIME) <= 0) {
                replaceJackOLantern(pos,level,state,UNLIT,0,state.getValue(FACING));
                playExtinguishSound(pos, level);
                return;
            }
            replaceJackOLantern(pos,level,state,newBurnTime,LIT,state.getValue(FACING));
            level.scheduleTick(pos,this,TICK_INTERVAL);
        }
    }

    private void replaceJackOLantern(BlockPos pos, Level level, BlockState state, int burnTime, int litState, Direction facing)
    {
        level.setBlockAndUpdate(pos, ModBlocks.JACK_O_LANTERN.get().defaultBlockState().setValue(BURN_TIME,burnTime).setValue(FACING,facing).setValue(LIT_STATE,litState));
        level.updateNeighborsAt(pos,this);
    }

    private void playExtinguishSound(BlockPos pos,Level level)
    {
        level.playSound(null,pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS,1, level.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if(!pIsMoving && pState.getBlock() != pOldState.getBlock()) {
            defaultBlockState().updateIndirectNeighbourShapes(pLevel,pPos,3);
        }
        if(SHOUD_BURN_OUT&& pState.getBlock() instanceof JackOLantern &&pState.getValue(LIT_STATE) > UNLIT)
            pLevel.scheduleTick(pPos,this,TICK_INTERVAL);
        super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
    }

    private static int getLitState(BlockState state)
    {
        if(state.getValue(LanternBlock.LIT_STATE) == 0)
        {
            return 0;
        }
        else
            return 15;
    }

    public static IntegerProperty getBurnTime()
    {
        return BURN_TIME;
    }

    public static IntegerProperty getLitState()
    {
        return LIT_STATE;
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(BURN_TIME,LIT_STATE);
        super.createBlockStateDefinition(pBuilder);
    }
}
