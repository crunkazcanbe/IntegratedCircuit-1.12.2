package net.replaceitem.integratedcircuit.client.circuit.renderer.components;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.replaceitem.integratedcircuit.IntegratedCircuit;
import net.replaceitem.integratedcircuit.circuit.ComponentState;
import net.replaceitem.integratedcircuit.circuit.components.AbstractRedstoneGateComponent;
import net.replaceitem.integratedcircuit.circuit.components.FacingComponent;
import net.replaceitem.integratedcircuit.circuit.components.RepeaterComponent;
import net.replaceitem.integratedcircuit.client.circuit.renderer.CircuitRenderer;
import net.replaceitem.integratedcircuit.client.circuit.renderer.ComponentRenderer;
import net.replaceitem.integratedcircuit.util.FlatDirection;

import javax.annotation.Nullable;

public class RepeaterComponentRenderer extends ComponentRenderer {
    private static final ResourceLocation TEXTURE_OFF = IntegratedCircuit.id("textures/integrated_circuit/repeater_off.png");
    private static final ResourceLocation TEXTURE_ON = IntegratedCircuit.id("textures/integrated_circuit/repeater_on.png");
    private static final ResourceLocation TEXTURE_TORCH_OFF = IntegratedCircuit.id("textures/integrated_circuit/torch_top_off.png");
    private static final ResourceLocation TEXTURE_TORCH_ON = IntegratedCircuit.id("textures/integrated_circuit/torch_top_on.png");
    private static final ResourceLocation TEXTURE_BAR = IntegratedCircuit.id("textures/integrated_circuit/repeater_bar.png");

    @Override
    public void render(int x, int y, float a, ComponentState state) {
        FlatDirection renderedRotation = state.getValue(FacingComponent.FACING).getOpposite();
        int rot = renderedRotation.getIndex();

        boolean powered = state.getValue(AbstractRedstoneGateComponent.POWERED);
        ResourceLocation baseTexture = powered ? TEXTURE_ON : TEXTURE_OFF;
        ComponentRenderer.renderTexture(baseTexture, x, y, rot, a);

        ResourceLocation torchTexture = powered ? TEXTURE_TORCH_ON : TEXTURE_TORCH_OFF;
        // fixed back torch
        ComponentRenderer.renderPartial(torchTexture, x, y, rot, 1, 1, 1, a, 6, 1, 4, 4, 16, 16);

        boolean locked = state.getValue(RepeaterComponent.LOCKED);
        int knobOffsetAmount = (state.getValue(RepeaterComponent.DELAY) - 1) * 2;
        if (locked) {
            ComponentRenderer.renderPartial(TEXTURE_BAR, x, y, rot, 1, 1, 1, a, 2, 6 + knobOffsetAmount, 12, 2, 16, 16);
        } else {
            ComponentRenderer.renderPartial(torchTexture, x, y, rot, 1, 1, 1, a, 6, 5 + knobOffsetAmount, 4, 4, 16, 16);
        }
    }

    @Override
    public @Nullable ITextComponent getHoverInfoText(ComponentState state) {
        return CircuitRenderer.getSignalStrengthText(state.getValue(AbstractRedstoneGateComponent.POWERED) ? 15 : 0);
    }
}
