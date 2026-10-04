package net.replaceitem.integratedcircuit.circuit;

import net.replaceitem.integratedcircuit.util.ComponentPos;

import java.util.Comparator;

public class OrderedCircuitTick {
    private final Component type;
    private final ComponentPos pos;
    private final long triggerTick;
    private final TickPriority priority;
    private final long subTickOrder;

    public OrderedCircuitTick(Component type, ComponentPos pos, long triggerTick, TickPriority priority, long subTickOrder) {
        this.type = type;
        this.pos = pos;
        this.triggerTick = triggerTick;
        this.priority = priority;
        this.subTickOrder = subTickOrder;
    }

    public OrderedCircuitTick(Component type, ComponentPos pos, long triggerTick, long subTickOrder) {
        this(type, pos, triggerTick, TickPriority.NORMAL, subTickOrder);
    }

    public Component type() {
        return type;
    }

    public ComponentPos pos() {
        return pos;
    }

    public long triggerTick() {
        return triggerTick;
    }

    public TickPriority priority() {
        return priority;
    }

    public long subTickOrder() {
        return subTickOrder;
    }

    public static final Comparator<OrderedCircuitTick> TRIGGER_TICK_COMPARATOR =
            Comparator.comparingLong((OrderedCircuitTick t) -> t.triggerTick)
                    .thenComparing(t -> t.priority)
                    .thenComparingLong(t -> t.subTickOrder);

    public static OrderedCircuitTick create(Component type, ComponentPos pos) {
        return new OrderedCircuitTick(type, pos, 0L, TickPriority.NORMAL, 0L);
    }

    /** Identity for the queued-tick set: a pos+type pair (matches original HASH_STRATEGY). */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderedCircuitTick)) return false;
        OrderedCircuitTick t = (OrderedCircuitTick) o;
        return this.type == t.type && this.pos.equals(t.pos);
    }

    @Override
    public int hashCode() {
        return 31 * pos.hashCode() + type.hashCode();
    }
}
