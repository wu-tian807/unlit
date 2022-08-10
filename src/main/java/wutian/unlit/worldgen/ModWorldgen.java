package wutian.unlit.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import wutian.unlit.Unlit;

import java.util.ArrayList;

public class ModWorldgen {
    private static final DeferredRegister<Feature<?>> FEATURE_REGISTER = DeferredRegister.create(ForgeRegistries.FEATURES, Unlit.MODID);
    private static final RegistryObject<ReplaceAllFeature> REPLACE_ALL_FEATURE = FEATURE_REGISTER.register("replace_all", () -> new ReplaceAllFeature(NoneFeatureConfiguration.CODEC.stable()));

    private static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIG_FEATURE_REGISTER = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, Unlit.MODID);
    private static final RegistryObject<ConfiguredFeature<NoneFeatureConfiguration, ReplaceAllFeature>> REPLACE_ALL_CONFIG_FEATURE = CONFIG_FEATURE_REGISTER.register("replace_all", () -> new ConfiguredFeature<>(REPLACE_ALL_FEATURE.get(), NoneFeatureConfiguration.INSTANCE));

    private static final DeferredRegister<PlacedFeature> PLACED_FEATURE_REGISTER = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, Unlit.MODID);
    public static final RegistryObject<PlacedFeature> REPLACE_ALL_PLACED_FEATURE = PLACED_FEATURE_REGISTER.register("replace_all", () -> new PlacedFeature(Holder.hackyErase(REPLACE_ALL_CONFIG_FEATURE.getHolder().get()), new ArrayList<>()));

    private static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Unlit.MODID);

    public static final RegistryObject<Codec<ReplaceAllFeatureModifier>> REPLACE_ALL_CODEC = BIOME_MODIFIER_SERIALIZERS.register("replace_all", () -> RecordCodecBuilder.create(builder -> builder.group(PlacedFeature.CODEC.fieldOf("feature").forGetter(ReplaceAllFeatureModifier::feature)).apply(builder, ReplaceAllFeatureModifier::new)));

    public static void register(IEventBus modEventBus)
    {
        FEATURE_REGISTER.register(modEventBus);
        CONFIG_FEATURE_REGISTER.register(modEventBus);
        PLACED_FEATURE_REGISTER.register(modEventBus);
        BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
    }
}
