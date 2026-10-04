package net.replaceitem.integratedcircuit.client.circuit.renderer.components;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.replaceitem.integratedcircuit.IntegratedCircuit;
import net.replaceitem.integratedcircuit.circuit.ComponentState;
import net.replaceitem.integratedcircuit.circuit.components.AbstractRedstoneGateComponent;
import net.replaceitem.integratedcircuit.circuit.components.ComparatorComponent;
import net.replaceitem.integratedcircuit.circuit.components.FacingComponent;
import net.replaceitem.integratedcircuit.circuit.state.ComparatorMode;
import net.replaceitem.integratedcircuit.client.circuit.renderer.CircuitRenderer;
import net.replaceitem.integratedcircuit.client.circuit.renderer.ComponentRenderer;

import javax.annotation.Nullable;

public class ComparatorComponentRenderer extends ComponentRenderer {
    private static final ResourceLocation TEXTURE = IntegratedCircuit.id("textures/integrated_circuit/comparator.png");
    private static final ResourceLocation TEXTURE_ON = IntegratedCircuit.id("textures/integrated_circuit/comparator_on.png");
    private static final ResourceLocation TEXTURE_TORCH_OFF = IntegratedCircuit.id("textures/integrated_circuit/torch_top_off.png");
    private static final ResourceLocation TEXTURE_TORCH_ON = IntegratedCircuit.id("textures/integrated_circuit/torch_top_on.png");

    @Override
    public void render(int x, int y, float a, ComponentState state) {
        boolean powered = state.getValue(AbstractRedstoneGateComponent.POWERED);
        int rot = state.getValue(FacingComponent.FACING).getOpposite().getIndex();
        ComponentRenderer.renderTexture(powered ? TEXTURE_ON : TEXTURE, x, y, rot, a);

        ResourceLocation torchTexture = powered ? TEXTURE_TORCH_ON : TEXTURE_TORCH_OFF;
        ComponentRenderer.renderPartial(torchTexture, x, y, rot, 1, 1, 1, a, 3, 10, 4, 4, 16, 16);
        ComponentRenderer.renderPartial(torchTexture, x, y, rot, 1, 1, 1, a, 9, 10, 4, 4, 16, 16);

        ResourceLocation modeTorchTexture = state.getValue(ComparatorComponent.MODE) == ComparatorMode.SUBTRACT ? TEXTURE_TORCH_ON : TEXTURE_TORCH_OFF;
        ComponentRenderer.renderPartial(modeTorchTexture, x, y, rot, 1, 1, 1, a, 6, 1, 4, 4, 16, 16);
    }

    @Override
    public @Nullable ITextComponent getHoverInfoText(ComponentState state) {
        return CircuitRenderer.getSignalStrengthText(state.getValue(ComparatorComponent.OUTPUT_POWER));
    }
}
