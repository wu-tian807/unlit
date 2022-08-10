package wutian.unlit.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import wutian.unlit.Unlit;

public class ModTags {
    public class Blocks{
        public static final TagKey<Block> TORCH_CAN_BE_BURNT_OUT = register("torch_can_be_burnt_out");

        private static TagKey<Block> register(String name)
        {
            return BlockTags.create(new ResourceLocation(Unlit.MODID,name));
        }
    }
}
