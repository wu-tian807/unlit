package wutian.unlit.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import wutian.unlit.Unlit;
import wutian.unlit.blocks.implementations.JackOLantern;
import wutian.unlit.blocks.implementations.LanternBlock;
import wutian.unlit.blocks.implementations.StandingTorchBlock;
import wutian.unlit.blocks.implementations.WallTorchBlock;

public class ModBlocks {
    private static final DeferredRegister<Block>  BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Unlit.MODID);
    public static final RegistryObject<Block> STANDING_TORCH = BLOCKS.register("standing_torch", StandingTorchBlock::new);
    public static final RegistryObject<Block> WALL_TORCH = BLOCKS.register(WallTorchBlock.NAME, WallTorchBlock::new);
    public static final RegistryObject<Block> LANTERN = BLOCKS.register("lantern", LanternBlock::new);

    public static final RegistryObject<Block> JACK_O_LANTERN = BLOCKS.register("jack_o_lantern", JackOLantern::new);

//@ObjectHolder(RealisticTorches.MODID)
//public class RealisticTorchesBlocks {
//
//    @ObjectHolder(RealisticTorchBlock.NAME)
//    public static final RealisticTorchBlock TORCH = null;
//
//    @ObjectHolder(RealisticWallTorchBlock.NAME)
//    public static final RealisticWallTorchBlock WALL_TORCH = null;
//
//    @SubscribeEvent
//    public static void registerBlocks(final RegistryEvent.Register<Block> blockRegistry) {
//        blockRegistry.getRegistry().registerAll(
//                new RealisticTorchBlock().setRegistryName(new ResourceLocation(RealisticTorches.MODID, RealisticTorchBlock.NAME)),
//                new RealisticWallTorchBlock().setRegistryName(new ResourceLocation(RealisticTorches.MODID, RealisticWallTorchBlock.NAME))
//        );
//    }

    public static void register(IEventBus eventBus)
    {
        BLOCKS.register(eventBus);
    }
}
