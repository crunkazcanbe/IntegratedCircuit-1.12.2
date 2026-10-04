package net.replaceitem.integratedcircuit.client.circuit;

import net.minecraft.nbt.NBTTagCompound;
import net.replaceitem.integratedcircuit.circuit.CircuitSection;
import net.replaceitem.integratedcircuit.circuit.CircuitSerializer;
import net.replaceitem.integratedcircuit.circuit.ComponentState;

/** Reads the circuit NBT (section + ports) into a {@link ClientCircuit}. Ticks are server-only. */
public class ClientCircuitSerializer extends CircuitSerializer {

    public ClientCircuitSerializer(NBTTagCompound nbt) {
        super(nbt);
    }

    public ClientCircuit readClientCircuit(ClientCircuitContext context) {
        ComponentState[] ports = readPortStates();
        CircuitSection section = readSection();
        return new ClientCircuit(context, ports, section);
    }
}
