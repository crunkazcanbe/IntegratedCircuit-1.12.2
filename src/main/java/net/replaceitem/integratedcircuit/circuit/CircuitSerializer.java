package net.replaceitem.integratedcircuit.circuit;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.replaceitem.integratedcircuit.circuit.context.ServerCircuitContext;
import net.replaceitem.integratedcircuit.util.ComponentPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Hand-rolled NBT (de)serialization for circuits. The modern code used Mojang Codecs +
 * a PalettedContainer; here we persist a flat int[] of global state ids (15x15), the 4 port
 * states, and the pending tick queue.
 *
 * NBT layout under the block entity's "circuit" compound:
 *   section  : int[225] of global ComponentState ids
 *   ports    : int[4]   of global ComponentState ids (per FlatDirection index)
 *   ticks    : list of compounds { c:String(component name), x:int, y:int, t:int(delay), p:int(priority), s:long(subTickOrder) }
 */
public class CircuitSerializer {

    public static final String SECTION_TAG = "section";
    public static final String PORTS_TAG = "ports";
    public static final String TICKS_TAG = "ticks";

    private final NBTTagCompound root;

    public CircuitSerializer(NBTTagCompound nbt) {
        this.root = nbt;
    }

    public ServerCircuit readServerCircuit(ServerCircuitContext context) {
        ComponentState[] ports = readPortStates();
        CircuitSection section = readSection();
        CircuitTickScheduler scheduler = readTickScheduler(context.getTime());
        return new ServerCircuit(context, ports, section, scheduler);
    }

    private static ComponentState stateById(int id, ComponentState fallback) {
        if (id >= 0 && id < Component.STATE_IDS.size()) {
            return Component.STATE_IDS.get(id);
        }
        return fallback;
    }

    protected ComponentState[] readPortStates() {
        if (!root.hasKey(PORTS_TAG)) return Circuit.createDefaultPorts();
        int[] ids = root.getIntArray(PORTS_TAG);
        if (ids.length != Circuit.PORT_COUNT) return Circuit.createDefaultPorts();
        ComponentState[] defaults = Circuit.createDefaultPorts();
        ComponentState[] ports = new ComponentState[Circuit.PORT_COUNT];
        for (int i = 0; i < ports.length; i++) {
            ports[i] = stateById(ids[i], defaults[i]);
        }
        return ports;
    }

    protected CircuitSection readSection() {
        int edge = Circuit.SIZE;
        if (!root.hasKey(SECTION_TAG)) return new CircuitSection();
        int[] ids = root.getIntArray(SECTION_TAG);
        if (ids.length != edge * edge) return new CircuitSection();
        ComponentState air = Components.AIR_DEFAULT_STATE;
        ComponentState[] states = new ComponentState[edge * edge];
        for (int i = 0; i < states.length; i++) {
            states[i] = stateById(ids[i], air);
        }
        return new CircuitSection(states);
    }

    protected CircuitTickScheduler readTickScheduler(long now) {
        if (!root.hasKey(TICKS_TAG)) return new CircuitTickScheduler();
        NBTTagList list = root.getTagList(TICKS_TAG, 10); // 10 = compound
        List<OrderedCircuitTick> ticks = new ArrayList<>();
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            Component component = Components.byName(tag.getString("c"));
            if (component == null) continue;
            ComponentPos pos = new ComponentPos(tag.getInteger("x"), tag.getInteger("y"));
            long trigger = now + tag.getInteger("t");
            TickPriority priority = TickPriority.byValue(tag.getInteger("p"));
            long subOrder = tag.getLong("s");
            ticks.add(new OrderedCircuitTick(component, pos, trigger, priority, subOrder));
        }
        return new CircuitTickScheduler(ticks);
    }

    public static void writeCircuit(ServerCircuit circuit, NBTTagCompound root) {
        // section
        ComponentState[] states = circuit.getSection().getStates();
        int[] sectionIds = new int[states.length];
        for (int i = 0; i < states.length; i++) sectionIds[i] = states[i].getStateId();
        root.setIntArray(SECTION_TAG, sectionIds);

        // ports
        ComponentState[] ports = circuit.getPorts();
        int[] portIds = new int[ports.length];
        for (int i = 0; i < ports.length; i++) portIds[i] = ports[i].getStateId();
        root.setIntArray(PORTS_TAG, portIds);

        // pending ticks
        NBTTagList list = new NBTTagList();
        long now = circuit.getTime();
        for (OrderedCircuitTick tick : circuit.getCircuitTickScheduler().getTickQueue()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("c", tick.type().getRegistryName());
            tag.setInteger("x", tick.pos().getX());
            tag.setInteger("y", tick.pos().getY());
            tag.setInteger("t", (int) (tick.triggerTick() - now));
            tag.setInteger("p", tick.priority().getValue());
            tag.setLong("s", tick.subTickOrder());
            list.appendTag(tag);
        }
        root.setTag(TICKS_TAG, list);
    }
}
