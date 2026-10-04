package net.replaceitem.integratedcircuit.client.network;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.replaceitem.integratedcircuit.circuit.Component;
import net.replaceitem.integratedcircuit.circuit.ComponentState;
import net.replaceitem.integratedcircuit.client.circuit.ClientCircuit;
import net.replaceitem.integratedcircuit.client.circuit.ClientCircuitContext;
import net.replaceitem.integratedcircuit.client.circuit.ClientCircuitSerializer;
import net.replaceitem.integratedcircuit.client.gui.IntegratedCircuitScreen;
import net.replaceitem.integratedcircuit.network.packet.ComponentUpdateS2CPacket;
import net.replaceitem.integratedcircuit.network.packet.OpenScreenS2CPacket;
import net.replaceitem.integratedcircuit.util.ComponentPos;

@SideOnly(Side.CLIENT)
public class ClientPacketHandler {

    public static void handleOpenScreen(OpenScreenS2CPacket packet) {
        Minecraft mc = Minecraft.getMinecraft();
        NBTTagCompound nbt = packet.getCircuitNbt();
        ClientCircuitContext context = new ClientCircuitContext(packet.getPos());
        ClientCircuitSerializer serializer = new ClientCircuitSerializer(nbt);
        ClientCircuit circuit = serializer.readClientCircuit(context);
        String name = packet.getCustomName();
        if (name == null || name.isEmpty()) name = "Integrated Circuit";
        mc.displayGuiScreen(new IntegratedCircuitScreen(circuit, name));
    }

    public static void handleComponentUpdate(ComponentUpdateS2CPacket packet) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen instanceof IntegratedCircuitScreen) {
            IntegratedCircuitScreen screen = (IntegratedCircuitScreen) mc.currentScreen;
            int id = packet.getStateId();
            if (id < 0 || id >= Component.STATE_IDS.size()) return;
            ComponentState state = Component.STATE_IDS.get(id);
            ComponentPos pos = packet.getPos();
            screen.getClientCircuit().onComponentUpdateFromServer(state, pos);
        }
    }
}
