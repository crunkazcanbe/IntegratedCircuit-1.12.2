package net.replaceitem.integratedcircuit.client.circuit.renderer.components;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.replaceitem.integratedcircuit.IntegratedCircuit;
import net.replaceitem.integratedcircuit.circuit.ComponentState;
import net.replaceitem.integratedcircuit.circuit.components.FacingComponent;
import net.replaceitem.integratedcircuit.circuit.components.TorchComponent;
import net.replaceitem.integratedcircuit.client.circuit.renderer.CircuitRenderer;
import net.replaceitem.integratedcircuit.client.circuit.renderer.ComponentRenderer;

import javax.annotation.Nullable;

public class TorchComponentRenderer extends ComponentRenderer {
    private static final ResourceLocation TEXTURE = IntegratedCircuit.id("textures/integrated_circuit/torch.png");
    private static final ResourceLocation TEXTURE_OFF = IntegratedCircuit.id("textures/integrated_circuit/torch_off.png");

    @Override
    public void render(int x, int y, float a, ComponentState state) {
        ResourceLocation texture = state.getValue(TorchComponent.LIT) ? TEXTURE : TEXTURE_OFF;
        ComponentRenderer.renderTexture(texture, x, y, state.getValue(FacingComponent.FACING).getIndex(), a);
    }

    @Override
    public @Nullable ITextComponent getHoverInfoText(ComponentState state) {
        return CircuitRenderer.getSignalStrengthText(state.getValue(TorchComponent.LIT) ? 15 : 0);
    }
}
