package wutian.unlit.items;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import wutian.unlit.Unlit;
import wutian.unlit.blocks.ModBlocks;
import wutian.unlit.items.implementations.*;

public class ModItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Unlit.MODID);

    public static final RegistryObject<Item> UNLIT_TORCH = ITEMS.register("unlit_torch",() -> new StandingAndWallBlockItem(ModBlocks.STANDING_TORCH.get(),ModBlocks.WALL_TORCH.get(),new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS).stacksTo(64)));

    public static final RegistryObject<Item> LIT_TORCH = ITEMS.register("lit_torch", LitTorchItem::new);

    public static final RegistryObject<Item> FIRE_STARTER = ITEMS.register("fire_starter", FireStarterItem::new);

    public static final RegistryObject<Item> COAL_DUST = ITEMS.register("coal_dust",()-> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS).stacksTo(64)){
        @Override
        public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
            return 400;
        }
    });

    public static final RegistryObject<Item> FAT = ITEMS.register("fat",()->new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS).stacksTo(64)));

    public static final RegistryObject<Item> FAT_DRUM = ITEMS.register("fat_drum",()->new FatDrum(new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(1)));

    public static final RegistryObject<Item> MATCHBOX = ITEMS.register("matchbox",()->new Item(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).stacksTo(64)){

        @Override
        public InteractionResult useOn(UseOnContext pContext) {
            pContext.getItemInHand().shrink(1);
            return Items.FLINT_AND_STEEL.useOn(pContext);
        }
    });

    public static final RegistryObject<Item> GLOWSTONE_CRYSTAL = ITEMS.register("glowstone_crystal",()->new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(64)));

    public static final RegistryObject<Item> GLOWSTONE_PASTE = ITEMS.register("glowstone_paste",()->new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC).stacksTo(64)));

    public static final RegistryObject<Item> UNLIT_LANTERN = ITEMS.register("unlit_lantern",()->new BlockItem(ModBlocks.LANTERN.get() , new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS).stacksTo(1)));

    public static final RegistryObject<Item> LIT_LANTERN = ITEMS.register("lit_lantern",()->new LitLantern(ModBlocks.LANTERN.get() , new Item.Properties().stacksTo(1)));


    public static final RegistryObject<Item> JACK_O_LANTERN = ITEMS.register("jack_o_lantern",() -> new JackOLanternItem(ModBlocks.JACK_O_LANTERN.get(),new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS).stacksTo(64)));

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
