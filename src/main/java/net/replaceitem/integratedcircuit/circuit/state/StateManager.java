package net.replaceitem.integratedcircuit.circuit.state;

import net.replaceitem.integratedcircuit.circuit.Component;
import net.replaceitem.integratedcircuit.circuit.ComponentState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Replaces modern MC's StateDefinition. Holds the ordered list of properties for a Component
 * and the full cross-product of immutable ComponentStates, each addressable by a local index.
 */
public class StateManager {
    private final Component owner;
    private final List<Property<?>> properties = new ArrayList<>();
    private List<ComponentState> states;
    private ComponentState defaultState;

    public StateManager(Component owner) {
        this.owner = owner;
    }

    public void add(Property<?>... props) {
        Collections.addAll(this.properties, props);
    }

    public List<Property<?>> getProperties() {
        return properties;
    }

    public boolean hasProperty(Property<?> property) {
        return properties.contains(property);
    }

    /** Builds the cross-product of all property values into immutable states. */
    public void build() {
        List<Map<Property<?>, Comparable<?>>> combinations = new ArrayList<>();
        combinations.add(new LinkedHashMap<>());
        for (Property<?> property : properties) {
            List<Map<Property<?>, Comparable<?>>> next = new ArrayList<>();
            for (Map<Property<?>, Comparable<?>> base : combinations) {
                for (Comparable<?> value : property.getValues()) {
                    Map<Property<?>, Comparable<?>> copy = new LinkedHashMap<>(base);
                    copy.put(property, value);
                    next.add(copy);
                }
            }
            combinations = next;
        }
        List<ComponentState> built = new ArrayList<>(combinations.size());
        for (Map<Property<?>, Comparable<?>> values : combinations) {
            built.add(new ComponentState(owner, properties, values, this));
        }
        // link states so .setValue() can navigate without rebuilding
        for (ComponentState state : built) {
            state.linkTable(built);
        }
        this.states = built;
        this.defaultState = built.isEmpty() ? null : built.get(0);
    }

    public List<ComponentState> getStates() {
        return states;
    }

    public ComponentState any() {
        return defaultState;
    }

    /** Computes the local index of a state given its property value map. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public int indexOf(Map<Property<?>, Comparable<?>> values) {
        int index = 0;
        for (Property<?> property : properties) {
            index *= property.size();
            Property p = property;
            index += p.indexOf(values.get(property));
        }
        return index;
    }
}
