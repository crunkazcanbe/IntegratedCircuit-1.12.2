package net.replaceitem.integratedcircuit.client.circuit.renderer.components;

import net.minecraft.util.ResourceLocation;
import net.replaceitem.integratedcircuit.circuit.ComponentState;
import net.replaceitem.integratedcircuit.client.circuit.renderer.ComponentRenderer;

public class FullComponentRenderer extends ComponentRenderer {
    private final ResourceLocation texture;

    public FullComponentRenderer(ResourceLocation texture) {
        this.texture = texture;
    }

    @Override
    public void render(int x, int y, float a, ComponentState state) {
        ComponentRenderer.renderTexture(texture, x, y, 0, a);
    }
}
