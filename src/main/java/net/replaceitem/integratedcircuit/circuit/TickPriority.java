package net.replaceitem.integratedcircuit.circuit;

/**
 * Mirrors modern MC's net.minecraft.world.ticks.TickPriority (1.12.2's variant lacks the
 * EXTREMELY_HIGH/VERY_HIGH spread the modern redstone gate code relies on).
 */
public enum TickPriority {
    EXTREMELY_HIGH(-3),
    VERY_HIGH(-2),
    HIGH(-1),
    NORMAL(0),
    LOW(1),
    VERY_LOW(2),
    EXTREMELY_LOW(3);

    private final int index;

    TickPriority(int index) {
        this.index = index;
    }

    public int getValue() {
        return index;
    }

    public static TickPriority byValue(int value) {
        for (TickPriority p : values()) {
            if (p.index == value) return p;
        }
        return NORMAL;
    }
}
