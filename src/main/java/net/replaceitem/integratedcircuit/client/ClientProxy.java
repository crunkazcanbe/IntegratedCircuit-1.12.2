package net.replaceitem.integratedcircuit.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.replaceitem.integratedcircuit.CommonProxy;
import net.replaceitem.integratedcircuit.IntegratedCircuit;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Mod.EventBusSubscriber(modid = IntegratedCircuit.MODID, value = net.minecraftforge.fml.relauncher.Side.CLIENT)
    public static class ClientRegistrationHandler {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event) {
            for (net.replaceitem.integratedcircuit.IntegratedCircuitBlock block : IntegratedCircuit.ALL_BLOCKS) {
                Item item = Item.getItemFromBlock(block);
                ModelLoader.setCustomModelResourceLocation(
                        item, 0,
                        new ModelResourceLocation(IntegratedCircuit.id(IntegratedCircuit.variantName(block)), "inventory")
                );
            }
        }
    }
}
