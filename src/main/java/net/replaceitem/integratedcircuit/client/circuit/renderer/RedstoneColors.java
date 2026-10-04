package net.replaceitem.integratedcircuit.client.circuit.renderer;

/** Reproduces vanilla redstone-wire power colouring (modern RedStoneWireBlock.getColorForPower). */
public final class RedstoneColors {
    private RedstoneColors() {}

    /** Returns 0xRRGGBB for a given 0-15 power level (matches vanilla's red gradient). */
    public static int getColorForPower(int power) {
        float f = power / 15.0f;
        float r = f * 0.6f + (f > 0.0f ? 0.4f : 0.3f);
        float g = clamp(f * f * 0.7f - 0.5f, 0.0f, 1.0f);
        float b = clamp(f * f * 0.6f - 0.7f, 0.0f, 1.0f);
        int ri = (int) (r * 255.0f);
        int gi = (int) (g * 255.0f);
        int bi = (int) (b * 255.0f);
        return (ri << 16) | (gi << 8) | bi;
    }

    public static float red(int rgb) {
        return ((rgb >> 16) & 0xFF) / 255.0f;
    }

    public static float green(int rgb) {
        return ((rgb >> 8) & 0xFF) / 255.0f;
    }

    public static float blue(int rgb) {
        return (rgb & 0xFF) / 255.0f;
    }

    private static float clamp(float v, float lo, float hi) {
        return v < lo ? lo : (v > hi ? hi : v);
    }
}
