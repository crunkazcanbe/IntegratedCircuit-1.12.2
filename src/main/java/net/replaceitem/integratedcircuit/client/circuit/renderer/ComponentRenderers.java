package net.replaceitem.integratedcircuit.client.circuit.renderer;

import net.replaceitem.integratedcircuit.IntegratedCircuit;
import net.replaceitem.integratedcircuit.circuit.Component;
import net.replaceitem.integratedcircuit.circuit.Components;
import net.replaceitem.integratedcircuit.client.circuit.renderer.components.*;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ComponentRenderers {
    private static final Map<Component, ComponentRenderer> RENDERERS = new HashMap<>();

    static {
        RENDERERS.put(Components.BLOCK, new FullComponentRenderer(IntegratedCircuit.id("textures/integrated_circuit/block.png")));
        RENDERERS.put(Components.WIRE, new WireComponentRenderer());
        RENDERERS.put(Components.PORT, new PortComponentRenderer());
        RENDERERS.put(Components.CROSSOVER, new CrossoverComponentRenderer());
        RENDERERS.put(Components.WOODEN_BUTTON, new ButtonComponentRenderer(IntegratedCircuit.id("textures/integrated_circuit/button_wood.png")));
        RENDERERS.put(Components.STONE_BUTTON, new ButtonComponentRenderer(IntegratedCircuit.id("textures/integrated_circuit/button_stone.png")));
        RENDERERS.put(Components.OBSERVER, new ObserverComponentRenderer());
        RENDERERS.put(Components.TORCH, new TorchComponentRenderer());
        RENDERERS.put(Components.REPEATER, new RepeaterComponentRenderer());
        RENDERERS.put(Components.COMPARATOR, new ComparatorComponentRenderer());
        RENDERERS.put(Components.LEVER, new LeverComponentRenderer());
        RENDERERS.put(Components.LAMP, new LampComponentRenderer());
        RENDERERS.put(Components.REDSTONE_BLOCK, new FullComponentRenderer(IntegratedCircuit.id("textures/integrated_circuit/redstone_block.png")));
    }

    @Nullable
    public static ComponentRenderer get(Component component) {
        if (component == Components.AIR) return null;
        return RENDERERS.get(component);
    }
}
