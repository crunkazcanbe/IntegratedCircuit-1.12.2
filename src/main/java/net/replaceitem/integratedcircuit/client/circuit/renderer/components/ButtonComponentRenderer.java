package net.replaceitem.integratedcircuit.client.circuit.renderer.components;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.replaceitem.integratedcircuit.circuit.ComponentState;
import net.replaceitem.integratedcircuit.circuit.components.ButtonComponent;
import net.replaceitem.integratedcircuit.circuit.components.FacingComponent;
import net.replaceitem.integratedcircuit.client.circuit.renderer.CircuitRenderer;
import net.replaceitem.integratedcircuit.client.circuit.renderer.ComponentRenderer;

import javax.annotation.Nullable;

public class ButtonComponentRenderer extends ComponentRenderer {
    private final ResourceLocation texture;

    public ButtonComponentRenderer(ResourceLocation texture) {
        this.texture = texture;
    }

    @Override
    public void render(int x, int y, float a, ComponentState state) {
        float b = state.getValue(ButtonComponent.POWERED) ? 0.5f : 1f;
        ComponentRenderer.renderTexture(texture, x, y, state.getValue(FacingComponent.FACING).getIndex(), b, b, b, a);
    }

    @Override
    public @Nullable ITextComponent getHoverInfoText(ComponentState state) {
        return CircuitRenderer.getSignalStrengthText(state.getValue(ButtonComponent.POWERED) ? 15 : 0);
    }
}
