package wutian.unlit.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import wutian.unlit.blocks.ModBlocks;
import wutian.unlit.blocks.implementations.JackOLantern;
import wutian.unlit.blocks.implementations.LanternBlock;
import wutian.unlit.blocks.implementations.StandingTorchBlock;
import wutian.unlit.blocks.implementations.WallTorchBlock;
import wutian.unlit.config.ConfigHandler;

public class ReplaceAllFeature extends Feature<NoneFeatureConfiguration> {
    public ReplaceAllFeature(Codec<NoneFeatureConfiguration> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(NoneFeatureConfiguration p_204741_, WorldGenLevel p_204742_, ChunkGenerator p_204743_, RandomSource p_204744_, BlockPos p_204745_) {
        return super.place(p_204741_, p_204742_, p_204743_, p_204744_, p_204745_);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if(!ConfigHandler.replaceVanillaBlock.get()) return false;
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();
        ChunkAccess chunk = level.getChunk(origin);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < chunk.getHeight(); y++) {
                    BlockPos pos = new BlockPos(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    BlockState state = chunk.getBlockState(pos);
                    if(state.is(Blocks.TORCH))
                    {
                        chunk.setBlockState(pos, ModBlocks.STANDING_TORCH.get().defaultBlockState().setValue(StandingTorchBlock.LIT_STATE,StandingTorchBlock.LIT).setValue(StandingTorchBlock.BURN_TIME,StandingTorchBlock.getInitialBurnTime()),false);
                        level.scheduleTick(pos,state.getBlock(),1200);
                    }
                    else if(state.is(Blocks.WALL_TORCH))
                    {
                        chunk.setBlockState(pos, ModBlocks.WALL_TORCH.get().defaultBlockState().setValue(StandingTorchBlock.LIT_STATE,StandingTorchBlock.LIT).setValue(StandingTorchBlock.BURN_TIME,StandingTorchBlock.getInitialBurnTime()).setValue(WallTorchBlock.FACING,state.getValue(WallTorchBlock.FACING)),false);
                        level.scheduleTick(pos,state.getBlock(),1200);
                    }
                    else if(state.is(Blocks.LANTERN))
                    {
                        chunk.setBlockState(pos,ModBlocks.LANTERN.get().defaultBlockState().setValue(LanternBlock.OIL,3).setValue(LanternBlock.BURN_TIME, ConfigHandler.lanternBurnOutTime.get()).setValue(LanternBlock.LIT_STATE,1).setValue(LanternBlock.HANGING,state.getValue(LanternBlock.HANGING)).setValue(LanternBlock.WATERLOGGED, state.getValue(LanternBlock.WATERLOGGED)),false);
                        level.scheduleTick(pos,state.getBlock(),1200);
                    }
                    else if(state.is(Blocks.JACK_O_LANTERN))
                    {
                        chunk.setBlockState(pos,ModBlocks.JACK_O_LANTERN.get().defaultBlockState().setValue(JackOLantern.FACING,state.getValue(JackOLantern.FACING)).setValue(JackOLantern.BURN_TIME, ConfigHandler.torchBurnoutTime.get()).setValue(LanternBlock.LIT_STATE,1),false);
                        level.scheduleTick(pos,state.getBlock(),1200);
                    }
                        }
                    }
                }

        return true;
    }
}
