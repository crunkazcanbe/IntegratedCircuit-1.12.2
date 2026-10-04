package net.replaceitem.integratedcircuit.circuit.state;

import java.util.Collection;

/**
 * Self-contained property system replacing modern MC's
 * net.minecraft.world.level.block.state.properties.Property.
 * 1.12.2's PropertyInteger/PropertyBool/PropertyEnum exist but are tied to IBlockState;
 * to keep the circuit state model independent of vanilla block states we roll our own.
 */
public abstract class Property<T extends Comparable<T>> {
    private final String name;
    private final Class<T> type;

    protected Property(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<T> getValueClass() {
        return type;
    }

    public abstract Collection<T> getValues();

    /** Index of a value within {@link #getValues()} order — used for state-id math. */
    public abstract int indexOf(T value);

    /** Value at a given index (inverse of {@link #indexOf}). */
    public abstract T fromIndex(int index);

    public int size() {
        return getValues().size();
    }

    public abstract String valueToString(T value);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Property)) return false;
        Property<?> p = (Property<?>) o;
        return this.name.equals(p.name) && this.type.equals(p.type);
    }

    @Override
    public int hashCode() {
        return 31 * name.hashCode() + type.hashCode();
    }

    @Override
    public String toString() {
        return "Property{" + name + "}";
    }
}
