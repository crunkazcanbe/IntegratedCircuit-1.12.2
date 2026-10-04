package net.replaceitem.integratedcircuit.client.circuit.renderer.components;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.replaceitem.integratedcircuit.IntegratedCircuit;
import net.replaceitem.integratedcircuit.circuit.ComponentState;
import net.replaceitem.integratedcircuit.circuit.components.LampComponent;
import net.replaceitem.integratedcircuit.client.circuit.renderer.CircuitRenderer;
import net.replaceitem.integratedcircuit.client.circuit.renderer.ComponentRenderer;

import javax.annotation.Nullable;

public class LampComponentRenderer extends ComponentRenderer {
    public static final ResourceLocation TEXTURE = IntegratedCircuit.id("textures/integrated_circuit/lamp.png");
    public static final ResourceLocation TEXTURE_ON = IntegratedCircuit.id("textures/integrated_circuit/lamp_on.png");

    @Override
    public void render(int x, int y, float a, ComponentState state) {
        ResourceLocation texture = state.getValue(LampComponent.LIT) ? TEXTURE_ON : TEXTURE;
        ComponentRenderer.renderTexture(texture, x, y, 0, a);
    }

    @Override
    public @Nullable ITextComponent getHoverInfoText(ComponentState state) {
        return CircuitRenderer.getSignalStrengthText(state.getValue(LampComponent.LIT) ? 15 : 0);
    }
}
