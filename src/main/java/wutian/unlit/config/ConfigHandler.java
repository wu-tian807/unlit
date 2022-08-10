package wutian.unlit.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class ConfigHandler {
    public static final String CATEGORY_GENERAL = "general";
    private static final ForgeConfigSpec.Builder  COMMON_BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec COMMON_CONFIG;

    public static ForgeConfigSpec.IntValue torchBurnoutTime;
    public static ForgeConfigSpec.BooleanValue vanillaTorchesDropUnlit;

    public static ForgeConfigSpec.BooleanValue torchCauseFire;

    public static ForgeConfigSpec.BooleanValue hardcore;

    public static ForgeConfigSpec.BooleanValue waterBurnt;

    public static ForgeConfigSpec.BooleanValue replaceVanillaBlock;

    public static ForgeConfigSpec.IntValue lanternBurnOutTime;
    static{
        COMMON_BUILDER.comment("General Settings").push(CATEGORY_GENERAL);
        String desc;

        desc = "The time of a torch or a Pumpkin burns out(Minute),Setting this to -1 will disable torch burnout.//以分钟为单位。设置为-1会禁止火把或南瓜熄灭(范围 -1 -- 2880)";
        torchBurnoutTime = COMMON_BUILDER.comment(desc).defineInRange("torchBurnoutTime",60,-1,2880);
//        desc = "Determines whether vanilla torches drop unlit torches when broken(True is ENABLE)//决定当破坏原版火把时是否会掉落 不亮的火把(True 是 开启)";
//        vanillaTorchesDropUnlit = COMMON_BUILDER.comment(desc).define("vanillaTorchesDropUnlit",true);
        desc = "Determines if torches cause combustibles burnt(True is ENABLE) (Only Lit Torch Not Smoldering Torch)//决定是否火把会导致易燃物燃烧(True 是 开启)(仅限点燃的火把不包含阴燃的火把)";
        torchCauseFire = COMMON_BUILDER.comment(desc).define("torchCauseFire",true);
        desc = "*Hardcore* Determines if Lit Blocks in the inventory or on the World or On the Containers Will be influenced(True is ENABLE)//*困难*决定是否背包里的点燃的方块会熄灭（True 是 开启）";
        hardcore = COMMON_BUILDER.comment(desc).define("hardcore",true);
        desc = "Determines if Lit torches in your inventory will be unlit when you are in the water(True is ENABLE)//决定当你进入水中时是否你背包里点燃的火把会被熄灭(True 是 开启)";
        waterBurnt = COMMON_BUILDER.comment(desc).define("water_burnt",true);
        desc  = "The time of a torch burns out(Minute),Setting this to -1 will disable torch burnout.//以分钟为单位。设置为-1会禁止火把熄灭(范围 -1 -- 2880)";
        lanternBurnOutTime = COMMON_BUILDER.comment(desc).defineInRange("lanternBurnOutTime",160,-1,2880);
        desc= "Determines if the vanilla light block will be replaced the mod light block(Warning:Please Don't enable it with TORCH_CAUSE_FIRE enabled , or villages will burn down )(True is ENABLE)//决定是否开启替换原版光照方块为模组的光照方块（请不要同时开启它和TORCH_CAUSE_FIRE，否则村庄回被烧毁）（True是开启）";
        replaceVanillaBlock = COMMON_BUILDER.comment(desc).define("replaceVanillaBlock",false);
        //CAUSE Serious Heap Overflow(Solved)
        COMMON_BUILDER.pop();
        COMMON_CONFIG=COMMON_BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        spec.setConfig(configData);
    }
}
