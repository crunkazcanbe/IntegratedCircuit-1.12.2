package net.replaceitem.integratedcircuit;

import net.minecraft.block.Block;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.replaceitem.integratedcircuit.circuit.Components;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(modid = IntegratedCircuit.MODID, name = IntegratedCircuit.NAME, version = IntegratedCircuit.VERSION)
public class IntegratedCircuit {
    public static final String MODID = "integrated_circuit";
    public static final String NAME = "Integrated Circuit";
    public static final String VERSION = "1.6.9";

    public static final String BLOCK_ID = "integrated_circuit";

    public static Logger LOGGER;

    @Mod.Instance(MODID)
    public static IntegratedCircuit INSTANCE;

    @net.minecraftforge.fml.common.SidedProxy(
            clientSide = "net.replaceitem.integratedcircuit.client.ClientProxy",
            serverSide = "net.replaceitem.integratedcircuit.CommonProxy")
    public static CommonProxy proxy;

    public static final net.minecraft.creativetab.CreativeTabs CREATIVE_TAB =
            new net.minecraft.creativetab.CreativeTabs(MODID) {
                @Override
                public net.minecraft.item.ItemStack getTabIconItem() {
                    return new net.minecraft.item.ItemStack(INTEGRATED_CIRCUIT_BLOCK);
                }
            };

    /**
     * The 16 dye colors the original mod ships, in DyeColor order. The asset/registry name for the
     * light-gray variant is "light_gray" (vanilla 1.12.2 calls it "silver", so we map it explicitly).
     */
    public static final EnumDyeColor[] COLORS = EnumDyeColor.values();

    /** The default (uncolored) circuit block, also used as the creative-tab icon. */
    public static final IntegratedCircuitBlock INTEGRATED_CIRCUIT_BLOCK = new IntegratedCircuitBlock();

    /** All circuit blocks (the 16 colored ones + the default), in registration order. */
    public static final List<IntegratedCircuitBlock> ALL_BLOCKS = new ArrayList<>();

    static {
        for (EnumDyeColor color : COLORS) {
            ALL_BLOCKS.add(new IntegratedCircuitBlock(color));
        }
        ALL_BLOCKS.add(INTEGRATED_CIRCUIT_BLOCK);
    }

    /** Asset/registry name for a dye color, matching the original mod (light gray, not silver). */
    public static String colorName(EnumDyeColor color) {
        return color == EnumDyeColor.SILVER ? "light_gray" : color.getName();
    }

    /** Variant registry path for a block, e.g. "blue_integrated_circuit". */
    public static String variantName(IntegratedCircuitBlock block) {
        EnumDyeColor color = block.getColor();
        return color == null ? BLOCK_ID : colorName(color) + "_" + BLOCK_ID;
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID, path);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
        // force component registry + global state-id assignment
        Components.register();
        GameRegistry.registerTileEntity(IntegratedCircuitBlockEntity.class, id(BLOCK_ID));
        net.replaceitem.integratedcircuit.network.IntegratedCircuitNetwork.register();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventBusSubscriber(modid = MODID)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {
            IForgeRegistry<Block> registry = event.getRegistry();
            for (IntegratedCircuitBlock block : ALL_BLOCKS) {
                String name = variantName(block);
                block.setCreativeTab(CREATIVE_TAB);
                registry.register(block.setRegistryName(id(name)).setUnlocalizedName(MODID + "." + name));
            }
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            IForgeRegistry<Item> registry = event.getRegistry();
            for (IntegratedCircuitBlock block : ALL_BLOCKS) {
                ItemBlock itemBlock = new IntegratedCircuitItem(block);
                itemBlock.setRegistryName(block.getRegistryName());
                registry.register(itemBlock);
            }
        }
    }
}
