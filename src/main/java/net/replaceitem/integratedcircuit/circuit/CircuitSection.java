package net.replaceitem.integratedcircuit.circuit;

import net.replaceitem.integratedcircuit.util.ComponentPos;

/** Flat 15x15 grid of component states (replaces the modern PalettedContainer). */
public class CircuitSection {
    public static final int EDGE = Circuit.SIZE;

    private final ComponentState[] states;

    public CircuitSection() {
        this.states = new ComponentState[EDGE * EDGE];
        ComponentState air = Components.AIR.getDefaultState();
        for (int i = 0; i < states.length; i++) states[i] = air;
    }

    public CircuitSection(ComponentState[] states) {
        this.states = states;
    }

    public ComponentState[] getStates() {
        return states;
    }

    private static int index(int x, int y) {
        return y * EDGE + x;
    }

    public ComponentState getComponentState(int x, int y) {
        return states[index(x, y)];
    }

    public void setComponentState(ComponentPos pos, ComponentState state) {
        states[index(pos.getX(), pos.getY())] = state;
    }

    public boolean isEmpty() {
        ComponentState air = Components.AIR.getDefaultState();
        for (ComponentState state : states) {
            if (state != air) return false;
        }
        return true;
    }
}
