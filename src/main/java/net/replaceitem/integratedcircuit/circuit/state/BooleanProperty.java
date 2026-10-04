package net.replaceitem.integratedcircuit.circuit.state;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BooleanProperty extends Property<Boolean> {
    private static final List<Boolean> VALUES = Arrays.asList(true, false);

    private BooleanProperty(String name) {
        super(name, Boolean.class);
    }

    public static BooleanProperty create(String name) {
        return new BooleanProperty(name);
    }

    @Override
    public Collection<Boolean> getValues() {
        return VALUES;
    }

    @Override
    public int indexOf(Boolean value) {
        return value ? 0 : 1;
    }

    @Override
    public Boolean fromIndex(int index) {
        return index == 0;
    }

    @Override
    public String valueToString(Boolean value) {
        return value.toString();
    }
}
