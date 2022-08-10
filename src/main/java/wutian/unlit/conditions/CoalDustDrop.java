package wutian.unlit.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Random;

public class CoalDustDrop implements LootItemCondition{

    @Override
    public LootItemConditionType getType() {
        return ModConditions.COAL_DUST_DROP.get();
    }

    @Override
    public boolean test(LootContext lootContext) {
        return new Random().nextBoolean();
    }

    public static class ConditionSerializer implements Serializer<CoalDustDrop> {
        @Override
        public void serialize(JsonObject json, CoalDustDrop value, JsonSerializationContext context) {
        }

        @Override
        public CoalDustDrop deserialize(JsonObject json, JsonDeserializationContext context) {
            return new CoalDustDrop();
        }
    }
}
