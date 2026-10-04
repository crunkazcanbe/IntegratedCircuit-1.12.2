package net.replaceitem.integratedcircuit.circuit;

import net.minecraft.block.SoundType;
import net.replaceitem.integratedcircuit.circuit.components.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class Components {
    private static final Map<String, Component> REGISTRY = new LinkedHashMap<>();

    public static final AirComponent AIR = register("air", new AirComponent(new Component.Settings()));
    public static final BlockComponent BLOCK = register("block", new BlockComponent(new Component.Settings().sounds(SoundType.CLOTH)));
    public static final WireComponent WIRE = register("wire", new WireComponent(new Component.Settings()));
    public static final TorchComponent TORCH = register("torch", new TorchComponent(new Component.Settings().sounds(SoundType.WOOD)));
    public static final RepeaterComponent REPEATER = register("repeater", new RepeaterComponent(new Component.Settings().sounds(SoundType.WOOD)));
    public static final ComparatorComponent COMPARATOR = register("comparator", new ComparatorComponent(new Component.Settings().sounds(SoundType.WOOD)));
    public static final ObserverComponent OBSERVER = register("observer", new ObserverComponent(new Component.Settings()));
    public static final RedstoneBlockComponent REDSTONE_BLOCK = register("redstone_block", new RedstoneBlockComponent(new Component.Settings().sounds(SoundType.METAL)));
    public static final PortComponent PORT = register("port", new PortComponent(new Component.Settings()));
    public static final CrossoverComponent CROSSOVER = register("crossover", new CrossoverComponent(new Component.Settings()));
    public static final LeverComponent LEVER = register("lever", new LeverComponent(new Component.Settings().sounds(SoundType.WOOD)));
    public static final ButtonComponent STONE_BUTTON = register("stone_button", new ButtonComponent(new Component.Settings(), false));
    public static final ButtonComponent WOODEN_BUTTON = register("wooden_button", new ButtonComponent(new Component.Settings().sounds(SoundType.WOOD), true));
    public static final LampComponent LAMP = register("lamp", new LampComponent(new Component.Settings().sounds(SoundType.GLASS)));

    public static final ComponentState AIR_DEFAULT_STATE = AIR.getDefaultState();

    private static <T extends Component> T register(String id, T component) {
        component.setRegistryName(id);
        REGISTRY.put(id, component);
        return component;
    }

    public static Map<String, Component> getRegistry() {
        return REGISTRY;
    }

    public static Component byName(String name) {
        return REGISTRY.get(name);
    }

    static {
        // assign global state ids across all components, in registry order
        int id = 0;
        for (Component component : REGISTRY.values()) {
            for (ComponentState componentState : component.getStateManager().getStates()) {
                componentState.setStateId(id);
                Component.STATE_IDS.add(componentState);
                id++;
            }
        }
    }

    public static void register() {
        // noop, forces static init
    }
}
