package net.replaceitem.integratedcircuit.circuit;

import net.replaceitem.integratedcircuit.util.ComponentPos;
import net.replaceitem.integratedcircuit.util.FlatDirection;

/** Mirrors modern MC's LevelAccessor for the circuit grid. */
public interface CircuitAccess {

    CircuitTickScheduler getCircuitTickScheduler();

    long getTime();

    long getTickOrder();

    default void scheduleBlockTick(ComponentPos pos, Component component, int delay, TickPriority priority) {
        this.getCircuitTickScheduler().scheduleTick(
                new OrderedCircuitTick(component, pos, this.getTime() + (long) delay, priority, this.getTickOrder()));
    }

    default void scheduleBlockTick(ComponentPos pos, Component component, int delay) {
        this.getCircuitTickScheduler().scheduleTick(
                new OrderedCircuitTick(component, pos, this.getTime() + (long) delay, this.getTickOrder()));
    }

    default void updateNeighbors(ComponentPos pos, Component component) {
    }

    default void updateNeighborsExcept(ComponentPos pos, Component sourceComponent, FlatDirection direction) {
    }

    default void updateNeighborsAlways(ComponentPos pos, Component sourceComponent) {
    }

    default void updateNeighbor(ComponentPos pos, Component sourceComponent, ComponentPos sourcePos) {
    }

    default void updateNeighbor(ComponentState state, ComponentPos pos, Component sourceComponent, ComponentPos sourcePos, boolean notify) {
    }

    default void replaceWithStateForNeighborUpdate(FlatDirection direction, ComponentState neighborState, ComponentPos pos, ComponentPos neighborPos, int flags, int maxUpdateDepth) {
    }
}
