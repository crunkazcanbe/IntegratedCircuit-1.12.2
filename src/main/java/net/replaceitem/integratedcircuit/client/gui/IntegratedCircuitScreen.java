package net.replaceitem.integratedcircuit.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.replaceitem.integratedcircuit.IntegratedCircuit;
import net.replaceitem.integratedcircuit.circuit.Circuit;
import net.replaceitem.integratedcircuit.circuit.Component;
import net.replaceitem.integratedcircuit.circuit.ComponentState;
import net.replaceitem.integratedcircuit.circuit.components.FacingComponent;
import net.replaceitem.integratedcircuit.client.circuit.ClientCircuit;
import net.replaceitem.integratedcircuit.client.circuit.renderer.CircuitRenderer;
import net.replaceitem.integratedcircuit.network.IntegratedCircuitNetwork;
import net.replaceitem.integratedcircuit.network.packet.FinishEditingC2SPacket;
import net.replaceitem.integratedcircuit.client.gui.widget.Toolbox;
import net.replaceitem.integratedcircuit.util.ComponentPos;
import net.replaceitem.integratedcircuit.util.FlatDirection;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import javax.annotation.Nullable;
import java.io.IOException;

public class IntegratedCircuitScreen extends GuiScreen {
    public static final ResourceLocation BACKGROUND_TEXTURE =
            IntegratedCircuit.id("textures/gui/container/integrated_circuit.png");

    protected static final int BACKGROUND_WIDTH = 302;
    protected static final int BACKGROUND_HEIGHT = 250;
    protected static final int TEXTURE_SIZE = 512;

    public static final int RENDER_COMPONENT_SIZE = 12;

    private static final int TITLE_X = 8;
    private static final int TITLE_Y = 9;
    private static final int CIRCUIT_X = 101;
    private static final int CIRCUIT_Y = 36;
    private static final int NAME_X = 153;
    private static final int NAME_Y = 9;
    private static final int NAME_W = 132;
    private static final int NAME_H = 12;
    private static final int TOOLBOX_X = 8;
    private static final int TOOLBOX_Y = 24;
    private static final int STATUSBAR_X = 89;
    private static final int STATUSBAR_Y = 234;
    private static final int STATUSBAR_RIGHT_MARGIN = 8;

    protected int x, y;
    protected int titleX, titleY;

    @Nullable protected Toolbox toolbox;
    @Nullable protected GuiTextField customNameTextField;

    protected String customName;
    protected final ClientCircuit circuit;

    @Nullable private CircuitRenderer circuitRenderer;

    private FlatDirection cursorRotation = FlatDirection.NORTH;
    @Nullable private ComponentState cursorState = null;

    private boolean startedDraggingInside = false;

    public IntegratedCircuitScreen(ClientCircuit circuit, String customName) {
        this.circuit = circuit;
        this.customName = customName;
    }

    public ClientCircuit getClientCircuit() {
        return circuit;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();

        this.x = (this.width - BACKGROUND_WIDTH) / 2;
        this.y = (this.height - BACKGROUND_HEIGHT) / 2;
        this.titleX = this.x + TITLE_X;
        this.titleY = this.y + TITLE_Y;

        this.toolbox = new Toolbox(this, TOOLBOX_X, TOOLBOX_Y);
        this.toolbox.init(this.buttonList);
        this.toolbox.registerToolSelectionSubscriber(this::updateCursorState);

        this.circuitRenderer = new CircuitRenderer(this.circuit, this.x + CIRCUIT_X, this.y + CIRCUIT_Y, RENDER_COMPONENT_SIZE);

        this.customNameTextField = new GuiTextField(0, this.fontRenderer,
                this.x + NAME_X, this.y + NAME_Y, NAME_W, NAME_H);
        this.customNameTextField.setEnableBackgroundDrawing(false);
        this.customNameTextField.setMaxStringLength(50);
        this.customNameTextField.setText(this.customName);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        IntegratedCircuitNetwork.CHANNEL.sendToServer(new FinishEditingC2SPacket(this.circuit.getContext().getBlockPos()));
    }

    // ---- rendering ----

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        GlStateManager.color(1, 1, 1, 1);
        this.mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawModalRectWithCustomSizedTexture(this.x, this.y, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);

        this.fontRenderer.drawString("Integrated Circuit", this.titleX, this.titleY, 0x404040);

        if (this.customNameTextField != null) {
            this.customNameTextField.drawTextBox();
        }

        if (this.circuitRenderer != null) {
            this.circuitRenderer.render(mouseX, mouseY);
        }

        renderCursorState(mouseX, mouseY);

        // buttons (toolbox) on top
        super.drawScreen(mouseX, mouseY, partialTicks);

        renderStatusBar(mouseX, mouseY);
    }

    private void renderStatusBar(int mouseX, int mouseY) {
        if (this.circuitRenderer == null) return;
        CircuitRenderer.CircuitStatusTexts texts = this.circuitRenderer.getStatusText(mouseX, mouseY);
        if (texts.left != null) {
            this.fontRenderer.drawString(texts.left.getFormattedText(),
                    this.x + STATUSBAR_X, this.y + STATUSBAR_Y, 0x404040);
        }
        if (texts.right != null) {
            int w = this.fontRenderer.getStringWidth(texts.right.getFormattedText());
            this.fontRenderer.drawString(texts.right.getFormattedText(),
                    this.x + BACKGROUND_WIDTH - w - STATUSBAR_RIGHT_MARGIN, this.y + STATUSBAR_Y, 0x404040);
        }
    }

    private void renderCursorState(int mouseX, int mouseY) {
        if (this.circuitRenderer == null || this.cursorState == null) return;
        ComponentPos pos = this.circuitRenderer.getComponentPosAt(mouseX, mouseY);
        if (!circuit.isInside(pos)) return;
        boolean validSpot = circuit.getComponentState(pos).isAir();
        float a = validSpot ? 0.5f : 0.2f;
        this.circuitRenderer.renderStateAtComponentPos(this.cursorState, pos.getX(), pos.getY(), a);
    }

    // ---- interaction ----

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (this.toolbox != null && this.toolbox.onButtonClicked(button)) return;
        super.actionPerformed(button);
    }

    @Override
    protected void mouseClicked(int mx, int my, int mouseButton) throws IOException {
        if (this.circuitRenderer == null || this.mc.player == null) {
            super.mouseClicked(mx, my, mouseButton);
            return;
        }

        if (this.customNameTextField != null) {
            this.customNameTextField.mouseClicked(mx, my, mouseButton);
        }

        // middle-click = rotate cursor
        if (mouseButton == 2) {
            rotateComponent(1);
            return;
        }

        ComponentPos clickedPos = this.circuitRenderer.getComponentPosAt(mx, my);
        boolean isInCircuit = circuit.isInside(clickedPos);
        startedDraggingInside = false;

        if (isInCircuit) {
            if (mouseButton == 0) { // left = place / use
                ComponentState state = circuit.getComponentState(clickedPos);
                if (state.isAir()) {
                    placeComponent(clickedPos);
                    startedDraggingInside = true;
                } else {
                    circuit.useComponent(clickedPos, this.mc.player);
                }
                return;
            }
            if (mouseButton == 1) { // right = destroy
                breakComponent(clickedPos);
                startedDraggingInside = true;
                return;
            }
        } else {
            if (mouseButton == 0 && Circuit.isPortPos(clickedPos)) {
                circuit.useComponent(clickedPos, this.mc.player);
                return;
            }
        }

        super.mouseClicked(mx, my, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mx, int my, int mouseButton, long timeSinceLastClick) {
        if (this.circuitRenderer == null) return;
        if (!startedDraggingInside) return;
        ComponentPos pos = this.circuitRenderer.getComponentPosAt(mx, my);
        if (!circuit.isInside(pos)) return;
        if (mouseButton == 0) {
            placeComponent(pos);
        } else if (mouseButton == 1) {
            breakComponent(pos);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        if (scroll != 0) {
            int dir = scroll > 0 ? 1 : -1;
            rotateComponent(dir);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.customNameTextField != null && this.customNameTextField.isFocused()) {
            if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_ESCAPE) {
                this.customNameTextField.setFocused(false);
                if (keyCode == Keyboard.KEY_ESCAPE) return;
                return;
            }
            if (this.customNameTextField.textboxKeyTyped(typedChar, keyCode)) {
                this.customName = this.customNameTextField.getText();
                return;
            }
        }

        if (keyCode == Keyboard.KEY_R) {
            rotateComponent(1);
            return;
        }

        // number keys select palette
        if (keyCode >= Keyboard.KEY_1 && keyCode <= Keyboard.KEY_9 && this.toolbox != null) {
            this.toolbox.selectTool(keyCode - Keyboard.KEY_1);
            return;
        }
        if (keyCode == Keyboard.KEY_0 && this.toolbox != null) {
            this.toolbox.deselectTool();
            return;
        }

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void updateScreen() {
        if (this.customNameTextField != null) this.customNameTextField.updateCursorCounter();
    }

    // ---- helpers ----

    private void breakComponent(ComponentPos pos) {
        circuit.breakComponentState(pos);
    }

    private void placeComponent(ComponentPos pos) {
        ComponentState state = circuit.getComponentState(pos);
        if (state.isAir() && this.cursorState != null) {
            circuit.placeComponentState(pos, this.cursorState.getComponent(), this.cursorRotation);
        }
    }

    private void updateCursorState(@Nullable Component component) {
        if (component != null) {
            this.cursorState = component.getDefaultState();
            if (component instanceof FacingComponent) {
                this.cursorState = this.cursorState.setValue(FacingComponent.FACING, this.cursorRotation);
            }
        } else {
            this.cursorState = null;
        }
    }

    private void rotateComponent(int amount) {
        if (this.cursorState != null && this.cursorState.getComponent() instanceof FacingComponent) {
            this.cursorRotation = this.cursorRotation.rotated(amount);
            this.cursorState = this.cursorState.setValue(FacingComponent.FACING, this.cursorRotation);
        }
    }

    public void updateCustomNameForExternalChange(ITextComponent customName) {
        if (this.customNameTextField != null && !this.customNameTextField.isFocused()) {
            this.customNameTextField.setText(customName.getUnformattedComponentText());
        }
    }
}
