package wutian.unlit;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import wutian.unlit.blocks.ModBlocks;
import wutian.unlit.conditions.ModConditions;
import wutian.unlit.config.ConfigHandler;
import wutian.unlit.items.ModItems;
import wutian.unlit.recipes.ModRecipes;
import wutian.unlit.worldgen.ModWorldgen;

@Mod(Unlit.MODID)
public class Unlit {
    public static final String MODID = "unlit";



    public Unlit(){
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.COMMON_CONFIG);
        ConfigHandler.loadConfig(ConfigHandler.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("unlit-common.toml"));
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModConditions.register(modEventBus);
        ModWorldgen.register(modEventBus);
        ModRecipes.register(modEventBus);
    }
}