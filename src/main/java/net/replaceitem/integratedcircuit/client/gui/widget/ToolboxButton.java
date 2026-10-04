package net.replaceitem.integratedcircuit.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.replaceitem.integratedcircuit.circuit.Component;

/** A single palette slot button rendering a component icon; selectable. */
public class ToolboxButton extends GuiButton {
    public static final int SIZE = 18;
    public static final int MARGIN = 2;

    private final Component component;
    private final ResourceLocation icon;
    public boolean selected = false;

    public ToolboxButton(int id, int x, int y, Component component, ResourceLocation icon) {
        super(id, x, y, SIZE, SIZE, "");
        this.component = component;
        this.icon = icon;
    }

    public Component getComponent() {
        return component;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) return;
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

        // slot background
        int bg = selected ? 0xFFFFFFAA : (this.hovered ? 0xFF707070 : 0xFF404040);
        drawRect(this.x, this.y, this.x + this.width, this.y + this.height, bg);
        drawRect(this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, 0xFF8B8B8B);

        // icon (16x16) centered in the 18px slot
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(icon);
        drawModalRectWithCustomSizedTexture(this.x + 1, this.y + 1, 0, 0, 16, 16, 16, 16);
    }
}
