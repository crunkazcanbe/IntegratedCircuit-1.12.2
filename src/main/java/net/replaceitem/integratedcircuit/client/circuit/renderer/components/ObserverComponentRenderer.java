package net.replaceitem.integratedcircuit.client.circuit.renderer.components;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.replaceitem.integratedcircuit.IntegratedCircuit;
import net.replaceitem.integratedcircuit.circuit.ComponentState;
import net.replaceitem.integratedcircuit.circuit.components.ObserverComponent;
import net.replaceitem.integratedcircuit.client.circuit.renderer.CircuitRenderer;
import net.replaceitem.integratedcircuit.client.circuit.renderer.ComponentRenderer;

import javax.annotation.Nullable;

public class ObserverComponentRenderer extends ComponentRenderer {
    public static final ResourceLocation TEXTURE = IntegratedCircuit.id("textures/integrated_circuit/observer.png");
    public static final ResourceLocation TEXTURE_ON = IntegratedCircuit.id("textures/integrated_circuit/observer_on.png");

    @Override
    public void render(int x, int y, float a, ComponentState state) {
        ResourceLocation texture = state.getValue(ObserverComponent.POWERED) ? TEXTURE_ON : TEXTURE;
        ComponentRenderer.renderTexture(texture, x, y, state.getValue(ObserverComponent.FACING).getOpposite().getIndex(), a);
    }

    @Override
    public @Nullable ITextComponent getHoverInfoText(ComponentState state) {
        return CircuitRenderer.getSignalStrengthText(state.getValue(ObserverComponent.POWERED) ? 15 : 0);
    }
}
