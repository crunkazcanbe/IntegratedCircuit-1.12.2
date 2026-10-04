package net.replaceitem.integratedcircuit.client.circuit.renderer.components;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.replaceitem.integratedcircuit.IntegratedCircuit;
import net.replaceitem.integratedcircuit.circuit.ComponentState;
import net.replaceitem.integratedcircuit.circuit.components.PortComponent;
import net.replaceitem.integratedcircuit.client.circuit.renderer.CircuitRenderer;
import net.replaceitem.integratedcircuit.client.circuit.renderer.ComponentRenderer;
import net.replaceitem.integratedcircuit.client.circuit.renderer.RedstoneColors;
import net.replaceitem.integratedcircuit.util.FlatDirection;

import javax.annotation.Nullable;

public class PortComponentRenderer extends ComponentRenderer {
    private static final ResourceLocation TEXTURE_ARROW = IntegratedCircuit.id("textures/integrated_circuit/port.png");
    protected static final ResourceLocation TEXTURE_X = IntegratedCircuit.id("textures/integrated_circuit/wire_x.png");
    protected static final ResourceLocation TEXTURE_Y = IntegratedCircuit.id("textures/integrated_circuit/wire_y.png");

    @Override
    public void render(int x, int y, float a, ComponentState state) {
        int rgb = RedstoneColors.getColorForPower(state.getValue(PortComponent.POWER));
        float r = RedstoneColors.red(rgb), g = RedstoneColors.green(rgb), b = RedstoneColors.blue(rgb);

        FlatDirection rotation = state.getValue(PortComponent.FACING);
        ComponentRenderer.renderTexture(TEXTURE_ARROW, x, y, rotation.getIndex(), r, g, b, a);

        ResourceLocation wireTexture = rotation.getAxis() == FlatDirection.Axis.X ? TEXTURE_X : TEXTURE_Y;
        ComponentRenderer.renderTexture(wireTexture, x, y, 0, r, g, b, a);
    }

    @Override
    public @Nullable ITextComponent getHoverInfoText(ComponentState state) {
        TextComponentTranslation result = new TextComponentTranslation(
                state.getValue(PortComponent.IS_OUTPUT)
                        ? "integrated_circuit.component.integrated_circuit.port_output"
                        : "integrated_circuit.component.integrated_circuit.port_input");
        result.appendText(" | ");
        result.appendSibling(CircuitRenderer.getSignalStrengthText(state.getValue(PortComponent.POWER)));
        return result;
    }
}
