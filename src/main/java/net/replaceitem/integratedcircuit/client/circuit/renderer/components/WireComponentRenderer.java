package net.replaceitem.integratedcircuit.client.circuit.renderer.components;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.replaceitem.integratedcircuit.IntegratedCircuit;
import net.replaceitem.integratedcircuit.circuit.ComponentState;
import net.replaceitem.integratedcircuit.circuit.components.WireComponent;
import net.replaceitem.integratedcircuit.client.circuit.renderer.CircuitRenderer;
import net.replaceitem.integratedcircuit.client.circuit.renderer.ComponentRenderer;
import net.replaceitem.integratedcircuit.client.circuit.renderer.RedstoneColors;
import net.replaceitem.integratedcircuit.util.FlatDirection;

import javax.annotation.Nullable;

public class WireComponentRenderer extends ComponentRenderer {
    private static final ResourceLocation TEXTURE_DOT = IntegratedCircuit.id("textures/integrated_circuit/wire_dot.png");
    protected static final ResourceLocation TEXTURE_X = IntegratedCircuit.id("textures/integrated_circuit/wire_x.png");
    protected static final ResourceLocation TEXTURE_Y = IntegratedCircuit.id("textures/integrated_circuit/wire_y.png");

    @Override
    public void render(int x, int y, float a, ComponentState state) {
        final int size = ComponentRenderer.COMPONENT_SIZE;
        final int half = size / 2;

        int rgb = RedstoneColors.getColorForPower(state.getValue(WireComponent.POWER));
        float r = RedstoneColors.red(rgb), g = RedstoneColors.green(rgb), b = RedstoneColors.blue(rgb);

        if (state.getValue(WireComponent.CONNECTED_NORTH))
            ComponentRenderer.renderSub16(TEXTURE_Y, x, y, 0, r, g, b, a, 0, 0, size, half);
        if (state.getValue(WireComponent.CONNECTED_EAST))
            ComponentRenderer.renderSub16(TEXTURE_X, x, y, 0, r, g, b, a, half, 0, half, size);
        if (state.getValue(WireComponent.CONNECTED_SOUTH))
            ComponentRenderer.renderSub16(TEXTURE_Y, x, y, 0, r, g, b, a, 0, half, size, half);
        if (state.getValue(WireComponent.CONNECTED_WEST))
            ComponentRenderer.renderSub16(TEXTURE_X, x, y, 0, r, g, b, a, 0, 0, half, size);

        int connections = 0;
        for (FlatDirection direction : FlatDirection.VALUES)
            if (state.getValue(WireComponent.DIRECTION_TO_CONNECTION_PROPERTY.get(direction))) connections++;

        boolean straight = (state.getValue(WireComponent.CONNECTED_NORTH) && state.getValue(WireComponent.CONNECTED_SOUTH))
                || (state.getValue(WireComponent.CONNECTED_EAST) && state.getValue(WireComponent.CONNECTED_WEST));
        if (connections != 2 || !straight) {
            ComponentRenderer.renderSub16(TEXTURE_DOT, x, y, 0, r, g, b, a, 0, 0, size, size);
        }
    }

    @Override
    public @Nullable ITextComponent getHoverInfoText(ComponentState state) {
        return CircuitRenderer.getSignalStrengthText(state.getValue(WireComponent.POWER));
    }
}
