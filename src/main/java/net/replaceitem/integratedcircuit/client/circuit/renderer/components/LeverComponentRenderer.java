package net.replaceitem.integratedcircuit.client.circuit.renderer.components;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.replaceitem.integratedcircuit.IntegratedCircuit;
import net.replaceitem.integratedcircuit.circuit.ComponentState;
import net.replaceitem.integratedcircuit.circuit.components.FacingComponent;
import net.replaceitem.integratedcircuit.circuit.components.LeverComponent;
import net.replaceitem.integratedcircuit.client.circuit.renderer.CircuitRenderer;
import net.replaceitem.integratedcircuit.client.circuit.renderer.ComponentRenderer;

import javax.annotation.Nullable;

public class LeverComponentRenderer extends ComponentRenderer {
    private static final ResourceLocation TEXTURE_OFF = IntegratedCircuit.id("textures/integrated_circuit/lever_off.png");
    private static final ResourceLocation TEXTURE_ON = IntegratedCircuit.id("textures/integrated_circuit/lever_on.png");

    @Override
    public void render(int x, int y, float a, ComponentState state) {
        ResourceLocation texture = state.getValue(LeverComponent.POWERED) ? TEXTURE_ON : TEXTURE_OFF;
        ComponentRenderer.renderTexture(texture, x, y, state.getValue(FacingComponent.FACING).getIndex(), a);
    }

    @Override
    public @Nullable ITextComponent getHoverInfoText(ComponentState state) {
        return CircuitRenderer.getSignalStrengthText(state.getValue(LeverComponent.POWERED) ? 15 : 0);
    }
}
