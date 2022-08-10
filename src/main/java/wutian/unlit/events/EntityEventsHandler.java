package wutian.unlit.events;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import wutian.unlit.items.ModItems;

import java.util.Random;

@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEventsHandler {
    @SubscribeEvent
    public static void AnimalDeath(LivingDeathEvent event)
    {
        if(event.getEntity().getLevel().isClientSide()) return;
        if(event.getEntity() instanceof Animal animal)
        {
            LogUtils.getLogger().debug("DEATH");
            int count;
            if(new Random().nextBoolean())
                count = new Random().nextInt(0,4);
            else
                count = new Random().nextInt(0,2);
            ItemStack stack = new ItemStack(ModItems.FAT.get());
            stack.setCount(count);
            ItemEntity itemEntity = (ItemEntity) EntityType.ITEM.spawn((ServerLevel) animal.getLevel(), null ,null,animal.getOnPos(), MobSpawnType.NATURAL,true,true);
            itemEntity.setItem(stack);
        }
    }
}
