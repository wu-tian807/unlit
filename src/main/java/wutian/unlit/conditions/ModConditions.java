package wutian.unlit.conditions;

import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import wutian.unlit.Unlit;

public class ModConditions {
    private static final DeferredRegister<LootItemConditionType> CONDITIONS = DeferredRegister.create(Registry.LOOT_ITEM_REGISTRY, Unlit.MODID);

    public static final RegistryObject<LootItemConditionType> COAL_DUST_DROP = CONDITIONS.register("coal_dust_drop", () -> new LootItemConditionType(new CoalDustDrop.ConditionSerializer()));

    public static void register(IEventBus eventBus)
    {
        CONDITIONS.register(eventBus);
    }
}
