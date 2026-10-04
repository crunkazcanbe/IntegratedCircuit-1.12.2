package net.replaceitem.integratedcircuit.client.circuit.renderer.components;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.replaceitem.integratedcircuit.IntegratedCircuit;
import net.replaceitem.integratedcircuit.circuit.ComponentState;
import net.replaceitem.integratedcircuit.circuit.components.CrossoverComponent;
import net.replaceitem.integratedcircuit.client.circuit.renderer.CircuitRenderer;
import net.replaceitem.integratedcircuit.client.circuit.renderer.ComponentRenderer;
import net.replaceitem.integratedcircuit.client.circuit.renderer.RedstoneColors;

import javax.annotation.Nullable;

public class CrossoverComponentRenderer extends ComponentRenderer {
    protected static final ResourceLocation TEXTURE_X = IntegratedCircuit.id("textures/integrated_circuit/wire_x.png");
    protected static final ResourceLocation TEXTURE_Y = IntegratedCircuit.id("textures/integrated_circuit/wire_y.png");
    private static final ResourceLocation TEXTURE_BRIDGE = IntegratedCircuit.id("textures/integrated_circuit/wire_bridge.png");

    @Override
    public void render(int x, int y, float a, ComponentState state) {
        int rgbX = RedstoneColors.getColorForPower(state.getValue(CrossoverComponent.POWER_X));
        int rgbY = RedstoneColors.getColorForPower(state.getValue(CrossoverComponent.POWER_Y));

        ComponentRenderer.renderTexture(TEXTURE_X, x, y, 0, RedstoneColors.red(rgbX), RedstoneColors.green(rgbX), RedstoneColors.blue(rgbX), a);
        ComponentRenderer.renderTexture(TEXTURE_BRIDGE, x, y, 0, a);
        ComponentRenderer.renderTexture(TEXTURE_Y, x, y, 0, RedstoneColors.red(rgbY), RedstoneColors.green(rgbY), RedstoneColors.blue(rgbY), a);
    }

    @Override
    public @Nullable ITextComponent getHoverInfoText(ComponentState state) {
        TextComponentString result = new TextComponentString("─ ");
        result.appendSibling(CircuitRenderer.getSignalStrengthText(state.getValue(CrossoverComponent.POWER_X)));
        result.appendText(" │ ");
        result.appendSibling(CircuitRenderer.getSignalStrengthText(state.getValue(CrossoverComponent.POWER_Y)));
        return result;
    }
}
