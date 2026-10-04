package net.replaceitem.integratedcircuit.circuit.state;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class EnumProperty<T extends Enum<T> & Comparable<T>> extends Property<T> {
    private final List<T> values;

    private EnumProperty(String name, Class<T> type, T[] values) {
        super(name, type);
        this.values = Arrays.asList(values);
    }

    public static <T extends Enum<T> & Comparable<T>> EnumProperty<T> create(String name, Class<T> type) {
        return new EnumProperty<>(name, type, type.getEnumConstants());
    }

    @Override
    public Collection<T> getValues() {
        return values;
    }

    @Override
    public int indexOf(T value) {
        return value.ordinal();
    }

    @Override
    public T fromIndex(int index) {
        return values.get(index);
    }

    @Override
    public String valueToString(T value) {
        return value.name().toLowerCase();
    }
}
