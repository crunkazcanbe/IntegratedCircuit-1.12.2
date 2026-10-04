package net.replaceitem.integratedcircuit.circuit.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IntegerProperty extends Property<Integer> {
    private final int min;
    private final int max;
    private final List<Integer> values;

    private IntegerProperty(String name, int min, int max) {
        super(name, Integer.class);
        this.min = min;
        this.max = max;
        List<Integer> v = new ArrayList<>();
        for (int i = min; i <= max; i++) v.add(i);
        this.values = v;
    }

    public static IntegerProperty create(String name, int min, int max) {
        return new IntegerProperty(name, min, max);
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public Collection<Integer> getValues() {
        return values;
    }

    @Override
    public int indexOf(Integer value) {
        return value - min;
    }

    @Override
    public Integer fromIndex(int index) {
        return min + index;
    }

    @Override
    public String valueToString(Integer value) {
        return value.toString();
    }
}
