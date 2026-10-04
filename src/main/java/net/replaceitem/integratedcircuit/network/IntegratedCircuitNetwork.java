package net.replaceitem.integratedcircuit.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.replaceitem.integratedcircuit.IntegratedCircuit;
import net.replaceitem.integratedcircuit.network.packet.ComponentInteractionC2SPacket;
import net.replaceitem.integratedcircuit.network.packet.ComponentUpdateS2CPacket;
import net.replaceitem.integratedcircuit.network.packet.FinishEditingC2SPacket;
import net.replaceitem.integratedcircuit.network.packet.OpenScreenS2CPacket;
import net.replaceitem.integratedcircuit.network.packet.PlaceComponentC2SPacket;

/**
 * Forge SimpleImpl network channel. Three gameplay packets are ported from the Fabric reference:
 *   - PlaceComponentC2SPacket    (client edits a cell -> server applies)
 *   - ComponentInteractionC2SPacket (client clicks a placed component -> server runs onUse)
 *   - ComponentUpdateS2CPacket   (server pushes a single changed cell -> editor clients)
 * Plus OpenScreenS2CPacket (server -> client to open the editor with the circuit snapshot)
 * and FinishEditingC2SPacket (client closes editor).
 */
public class IntegratedCircuitNetwork {
    public static final SimpleNetworkWrapper CHANNEL =
            NetworkRegistry.INSTANCE.newSimpleChannel(IntegratedCircuit.MODID);

    public static void register() {
        int id = 0;
        // C2S
        CHANNEL.registerMessage(PlaceComponentC2SPacket.Handler.class, PlaceComponentC2SPacket.class, id++, Side.SERVER);
        CHANNEL.registerMessage(ComponentInteractionC2SPacket.Handler.class, ComponentInteractionC2SPacket.class, id++, Side.SERVER);
        CHANNEL.registerMessage(FinishEditingC2SPacket.Handler.class, FinishEditingC2SPacket.class, id++, Side.SERVER);
        // S2C
        CHANNEL.registerMessage(ComponentUpdateS2CPacket.Handler.class, ComponentUpdateS2CPacket.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(OpenScreenS2CPacket.Handler.class, OpenScreenS2CPacket.class, id++, Side.CLIENT);
    }
}
