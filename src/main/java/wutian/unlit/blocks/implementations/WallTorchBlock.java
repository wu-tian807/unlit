package wutian.unlit.blocks.implementations;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import wutian.unlit.blocks.ModBlocks;
import wutian.unlit.items.ModItems;

public class WallTorchBlock extends StandingTorchBlock{
    public static final String NAME = "wall_torch";
    public static final int TICK_RATE = 1200;

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public WallTorchBlock()
    {
        super();
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource source) {
        if(state.getValue(LIT_STATE) == LIT && level.getRandom().nextInt(2) == 1)
        {
            Direction direction = state.getValue(FACING);
            double d0 = (double)pos.getX() + 0.5D;
            double d1 = (double)pos.getY() + 0.7D;
            double d2 = (double)pos.getZ() + 0.5D;
            double d3 = 0.22D;
            double d4 = 0.27D;
            Direction direction1 = direction.getOpposite();
            level.addParticle(ParticleTypes.SMOKE, d0 + 0.27D * (double)direction1.getStepX(), d1 + 0.22D, d2 + 0.27D * (double)direction1.getStepZ(), 0.0D, 0.0D, 0.0D);
            level.addParticle(this.flameParticle, d0 + 0.27D * (double)direction1.getStepX(), d1 + 0.22D, d2 + 0.27D * (double)direction1.getStepZ(), 0.0D, 0.0D, 0.0D);
        }
        else if(state.getValue(LIT_STATE) == SMOULDERING && level.getRandom().nextInt(2) == 1 )
        {
            Direction direction = state.getValue(FACING);
            double d0 = (double)pos.getX() + 0.5D;
            double d1 = (double)pos.getY() + 0.7D;
            double d2 = (double)pos.getZ() + 0.5D;
            double d3 = 0.22D;
            double d4 = 0.27D;
            Direction direction1 = direction.getOpposite();
            level.addParticle(ParticleTypes.SMOKE, d0 + 0.27D * (double)direction1.getStepX(), d1 + 0.22D, d2 + 0.27D * (double)direction1.getStepZ(), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void changeToLit(Level pLevel, BlockPos pPos, BlockState pState) {
        pLevel.setBlockAndUpdate(pPos, ModBlocks.WALL_TORCH.get().defaultBlockState().
                setValue(LIT_STATE,LIT).
                setValue(BURN_TIME,getInitialBurnTime()).
                setValue(FACING,pState.getValue(FACING)));
        if(SHOULD_BURN_OUT){
            pLevel.scheduleTick(pPos,this,TICK_RATE);
        }
    }

    @Override
    public void changeToSmoldering(Level pLevel, BlockPos pPos, BlockState pState, int burnTime) {
        pLevel.setBlockAndUpdate(pPos,ModBlocks.WALL_TORCH.get().defaultBlockState().
                setValue(LIT_STATE,SMOULDERING).
                setValue(BURN_TIME,burnTime).
                setValue(FACING,pState.getValue(FACING)));
        if(SHOULD_BURN_OUT){
            pLevel.scheduleTick(pPos,this,TICK_RATE);
        }
    }

    @Override
    public void changeToUnlit(Level pLevel, BlockPos pPos, BlockState pState) {
        pLevel.setBlockAndUpdate(pPos,ModBlocks.WALL_TORCH.get().defaultBlockState().
                setValue(FACING,pState.getValue(FACING)));
        if(SHOULD_BURN_OUT)
            pLevel.scheduleTick(pPos,this,TICK_RATE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return net.minecraft.world.level.block.WallTorchBlock.getShape(pState);
    }

    @Override
    public boolean isValidSpawn(BlockState state, BlockGetter level, BlockPos pos, SpawnPlacements.Type type, EntityType<?> entityType) {
        return Blocks.WALL_TORCH.isValidSpawn(state,level,pos,type,entityType);
    }

    //let it can be placed in the wall
    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return Blocks.WALL_TORCH.canSurvive(pState,pLevel,pPos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state =Blocks.WALL_TORCH.getStateForPlacement(pContext);
        ItemStack placeStack = pContext.getPlayer().getItemInHand(pContext.getHand());
        if(!placeStack.is(ModItems.LIT_TORCH.get())) return state == null ? null:this.defaultBlockState().setValue(FACING,state.getValue(FACING));
        if(placeStack.getOrCreateTag().contains("burnTime"))
        {
            int burnTime = placeStack.getTag().getInt("burnTime");
            if(pContext.getLevel().isRainingAt(pContext.getClickedPos().above()))
            {
                if(burnTime > INITIAL_BURN_TIME)
                {
                    return  state == null ? null:this.defaultBlockState().setValue(FACING,state.getValue(FACING)).setValue(BURN_TIME,INITIAL_BURN_TIME).setValue(LIT_STATE,1);
                }
                else if(burnTime <= 0)
                {
                    return  state == null ? null:this.defaultBlockState().setValue(FACING,state.getValue(FACING));
                }
                else
                {
                    return state == null ? null:this.defaultBlockState().setValue(FACING,state.getValue(FACING)).setValue(BURN_TIME,burnTime).setValue(LIT_STATE,1);
                }
            }
            if(burnTime > INITIAL_BURN_TIME)
            {
                return  state == null ? null:this.defaultBlockState().setValue(FACING,state.getValue(FACING)).setValue(BURN_TIME,INITIAL_BURN_TIME).setValue(LIT_STATE,2);
            }
            else if(burnTime <= 0)
            {
                return  state == null ? null:this.defaultBlockState().setValue(FACING,state.getValue(FACING));
            }
            else
            {
                return state == null ? null:this.defaultBlockState().setValue(FACING,state.getValue(FACING)).setValue(BURN_TIME,burnTime).setValue(LIT_STATE,2);
            }

        }
        else
        {
            if(pContext.getLevel().isRainingAt(pContext.getClickedPos().above()))
            {
                return  state == null ? null:this.defaultBlockState().setValue(FACING,state.getValue(FACING)).setValue(BURN_TIME,INITIAL_BURN_TIME).setValue(LIT_STATE,1);
            }
            return state == null ? null:this.defaultBlockState().setValue(FACING,state.getValue(FACING)).setValue(BURN_TIME,INITIAL_BURN_TIME).setValue(LIT_STATE,2);
        }
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        return Blocks.WALL_TORCH.updateShape(pState,pFacing,pFacingState,pLevel,pCurrentPos,pFacingPos);
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return Blocks.WALL_TORCH.rotate(pState,pRotation);
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return Blocks.WALL_TORCH.mirror(pState,pMirror);
    }
}