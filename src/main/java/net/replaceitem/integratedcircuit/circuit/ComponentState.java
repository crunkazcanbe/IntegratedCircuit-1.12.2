package net.replaceitem.integratedcircuit.circuit;

import net.minecraft.entity.player.EntityPlayer;
import net.replaceitem.integratedcircuit.circuit.state.Property;
import net.replaceitem.integratedcircuit.circuit.state.StateManager;
import net.replaceitem.integratedcircuit.util.ComponentPos;
import net.replaceitem.integratedcircuit.util.FlatDirection;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Self-contained replacement for modern MC's StateHolder/BlockState, adapted to the circuit grid.
 * Immutable; setValue()/cycle() return sibling states from the component's pre-built table.
 */
public class ComponentState {

    protected final Component owner;
    private final List<Property<?>> properties;
    private final Map<Property<?>, Comparable<?>> values;
    private final StateManager stateManager;
    private final int localIndex;

    private List<ComponentState> table;
    private int stateId = -1; // global id, assigned during Components static init

    public ComponentState(Component owner, List<Property<?>> properties, Map<Property<?>, Comparable<?>> values, StateManager stateManager) {
        this.owner = owner;
        this.properties = properties;
        this.values = values;
        this.stateManager = stateManager;
        this.localIndex = stateManager.indexOf(values);
    }

    public void linkTable(List<ComponentState> table) {
        this.table = table;
    }

    public void setStateId(int id) {
        this.stateId = id;
    }

    public int getStateId() {
        return stateId;
    }

    public Component getComponent() {
        return owner;
    }

    public StateManager getStateManager() {
        return stateManager;
    }

    public boolean hasProperty(Property<?> property) {
        return values.containsKey(property);
    }

    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> T getValue(Property<T> property) {
        Comparable<?> value = values.get(property);
        if (value == null) {
            throw new IllegalArgumentException("Cannot get property " + property + " on " + owner);
        }
        return (T) value;
    }

    public <T extends Comparable<T>> ComponentState setValue(Property<T> property, T value) {
        if (!values.containsKey(property)) {
            throw new IllegalArgumentException("Cannot set property " + property + " on " + owner);
        }
        if (values.get(property).equals(value)) {
            return this;
        }
        Map<Property<?>, Comparable<?>> newValues = new LinkedHashMap<>(values);
        newValues.put(property, value);
        int idx = stateManager.indexOf(newValues);
        return table.get(idx);
    }

    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> ComponentState cycle(Property<T> property) {
        List<T> all = (List<T>) (List<?>) new java.util.ArrayList<>(property.getValues());
        T current = getValue(property);
        int i = all.indexOf(current);
        T next = all.get((i + 1) % all.size());
        return setValue(property, next);
    }

    // ---- behaviour delegation (mirrors original ComponentState) ----

    public boolean isAir() {
        return isOf(Components.AIR);
    }

    public boolean isOf(Component component) {
        return this.owner == component;
    }

    public void onUse(Circuit circuit, ComponentPos pos, EntityPlayer player) {
        this.owner.onUse(this, circuit, pos, player);
    }

    public boolean emitsRedstonePower() {
        return this.owner.emitsRedstonePower(this);
    }

    public ComponentState getStateForNeighborUpdate(FlatDirection direction, ComponentState neighborState, Circuit circuit, ComponentPos pos, ComponentPos neighborPos) {
        return this.owner.getStateForNeighborUpdate(this, direction, neighborState, circuit, pos, neighborPos);
    }

    public void neighborUpdate(Circuit world, ComponentPos pos, Component sourceBlock, ComponentPos sourcePos, boolean notify) {
        this.owner.neighborUpdate(this, world, pos, sourceBlock, sourcePos, notify);
    }

    public void updateNeighbors(CircuitAccess circuit, ComponentPos pos, int flags) {
        updateNeighbors(circuit, pos, flags, 512);
    }

    public void updateNeighbors(CircuitAccess circuit, ComponentPos pos, int flags, int maxUpdateDepth) {
        for (FlatDirection direction : Component.DIRECTIONS) {
            ComponentPos offsetPos = pos.offset(direction);
            circuit.replaceWithStateForNeighborUpdate(direction.getOpposite(), this, offsetPos, pos, flags, maxUpdateDepth);
        }
    }

    public void onStateReplaced(Circuit circuit, ComponentPos pos, ComponentState newState) {
        this.owner.onStateReplaced(this, circuit, pos, newState);
    }

    public void scheduledTick(ServerCircuit circuit, ComponentPos pos, Random random) {
        this.owner.scheduledTick(this, circuit, pos, random);
    }

    public void onBlockAdded(Circuit circuit, ComponentPos pos, ComponentState oldState) {
        this.owner.onBlockAdded(this, circuit, pos, oldState);
    }

    public void prepare(CircuitAccess circuit, ComponentPos pos, int flags) {
        this.prepare(circuit, pos, flags, 512);
    }

    public void prepare(CircuitAccess circuit, ComponentPos pos, int flags, int maxUpdateDepth) {
        this.owner.prepare(this, circuit, pos, flags, maxUpdateDepth);
    }

    public int getWeakRedstonePower(Circuit circuit, ComponentPos pos, FlatDirection direction) {
        return this.owner.getWeakRedstonePower(this, circuit, pos, direction);
    }

    public int getStrongRedstonePower(Circuit circuit, ComponentPos pos, FlatDirection direction) {
        return this.owner.getStrongRedstonePower(this, circuit, pos, direction);
    }

    public int increasePower(FlatDirection side) {
        return this.owner.increasePower(this, side);
    }

    public boolean isSolidBlock(Circuit circuit, ComponentPos pos) {
        return this.owner.isSolidBlock(circuit, pos);
    }

    public boolean canPlaceAt(Circuit circuit, ComponentPos pos) {
        return this.owner.canPlaceAt(this, circuit, pos);
    }

    public boolean hasComparatorOutput() {
        return this.owner.hasComparatorOutput(this);
    }

    public int getComparatorOutput(Circuit circuit, ComponentPos pos) {
        return this.owner.getComparatorOutput(this, circuit, pos);
    }
}
