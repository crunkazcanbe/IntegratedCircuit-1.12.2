package net.replaceitem.integratedcircuit.client.circuit.renderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.replaceitem.integratedcircuit.circuit.Circuit;
import net.replaceitem.integratedcircuit.circuit.Component;
import net.replaceitem.integratedcircuit.circuit.ComponentState;
import net.replaceitem.integratedcircuit.circuit.Components;
import net.replaceitem.integratedcircuit.circuit.components.PortComponent;
import net.replaceitem.integratedcircuit.client.circuit.ClientCircuit;
import net.replaceitem.integratedcircuit.util.ComponentPos;
import net.replaceitem.integratedcircuit.util.FlatDirection;

import javax.annotation.Nullable;

/** Draws the 15x15 circuit grid (plus the 4 ports) inside the editor screen. */
public class CircuitRenderer {
    public static final int COMPONENT_SIZE = ComponentRenderer.COMPONENT_SIZE; // 16

    private final ClientCircuit circuit;
    private final int x, y;
    private final int renderComponentSize;
    private final float renderScale;

    public CircuitRenderer(ClientCircuit circuit, int x, int y, int renderComponentSize) {
        this.circuit = circuit;
        this.x = x;
        this.y = y;
        this.renderComponentSize = renderComponentSize;
        this.renderScale = ((float) renderComponentSize) / ((float) COMPONENT_SIZE);
    }

    private void inCircuitTransform(Runnable runnable) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(renderScale, renderScale, 1);
        runnable.run();
        GlStateManager.popMatrix();
    }

    public void render(int mouseX, int mouseY) {
        inCircuitTransform(() -> {
            for (FlatDirection direction : FlatDirection.VALUES) {
                ComponentState port = circuit.getPorts()[direction.getIndex()];
                ComponentPos pos = Circuit.PORT_POSITIONS.get(direction);
                renderStateInTransformAtComponentPos(port, pos.getX(), pos.getY(), 1);
            }
            for (int i = 0; i < Circuit.SIZE; i++) {
                for (int j = 0; j < Circuit.SIZE; j++) {
                    ComponentState componentState = circuit.getSection().getComponentState(i, j);
                    renderStateInTransformAtComponentPos(componentState, i, j, 1);
                }
            }
        });
    }

    public static class CircuitStatusTexts {
        @Nullable public final ITextComponent left;
        @Nullable public final ITextComponent right;
        public CircuitStatusTexts(@Nullable ITextComponent left, @Nullable ITextComponent right) {
            this.left = left;
            this.right = right;
        }
    }

    public CircuitStatusTexts getStatusText(int mouseX, int mouseY) {
        ComponentPos pos = getComponentPosAt(mouseX, mouseY);
        ComponentState componentState = circuit.getComponentState(pos);
        Component component = componentState.getComponent();

        int gridX = getGridXAt(mouseX);
        int gridY = getGridYAt(mouseY);

        ITextComponent leftSideText = null;
        ITextComponent rightSideText = null;

        if (circuit.isInside(pos)) {
            String componentName = component != Components.AIR ? component.getName().getFormattedText() : "";
            leftSideText = new TextComponentString(String.format("(%d, %d) %s", gridX, gridY, componentName));
        } else if (component instanceof PortComponent) {
            leftSideText = component.getName();
        }

        ComponentRenderer renderer = ComponentRenderers.get(component);
        if (renderer != null) {
            rightSideText = renderer.getHoverInfoText(componentState);
        }
        return new CircuitStatusTexts(leftSideText, rightSideText);
    }

    public int getGridXAt(int pixelX) {
        return Math.floorDiv(pixelX - this.x, renderComponentSize);
    }

    public int getGridYAt(int pixelY) {
        return Math.floorDiv(pixelY - this.y, renderComponentSize);
    }

    public ComponentPos getComponentPosAt(int pixelX, int pixelY) {
        return new ComponentPos(getGridXAt(pixelX), getGridYAt(pixelY));
    }

    private void renderStateInTransformAtComponentPos(ComponentState state, int gx, int gy, float a) {
        renderStateInTransformAt(state, gx * COMPONENT_SIZE, gy * COMPONENT_SIZE, a);
    }

    protected static void renderStateInTransformAt(ComponentState state, int px, int py, float a) {
        ComponentRenderer renderer = ComponentRenderers.get(state.getComponent());
        if (renderer != null) {
            renderer.render(px, py, a, state);
        }
    }

    /** Renders a ghost/preview state at a grid cell (used for the cursor). */
    public void renderStateAtComponentPos(ComponentState state, int gx, int gy, float a) {
        inCircuitTransform(() -> renderStateInTransformAtComponentPos(state, gx, gy, a));
    }

    public static ITextComponent getSignalStrengthText(int signalStrength) {
        int color = RedstoneColors.getColorForPower(signalStrength);
        TextComponentString s = new TextComponentString(String.valueOf(signalStrength));
        // 1.12.2 Style only supports named colours; approximate by brightness.
        Style style = new Style().setColor(signalStrength > 0 ? TextFormatting.RED : TextFormatting.DARK_GRAY);
        s.setStyle(style);
        return s;
    }
}
