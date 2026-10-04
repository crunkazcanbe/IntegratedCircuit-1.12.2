package net.replaceitem.integratedcircuit.client.circuit.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.replaceitem.integratedcircuit.circuit.ComponentState;

import javax.annotation.Nullable;

/**
 * 1.12.2 port of the modern ComponentRenderer. Renders 16x16 component textures into the editor grid
 * (the grid is GL-scaled by the CircuitRenderer so all coordinates here are in 16-px component space).
 * The modern matrix-based partial-texture blits are translated to GlStateManager rotate + a Tessellator
 * quad with explicit UVs.
 */
public abstract class ComponentRenderer {

    public static final int COMPONENT_SIZE = 16;

    public abstract void render(int x, int y, float a, ComponentState state);

    @Nullable
    public ITextComponent getHoverInfoText(ComponentState state) {
        return null;
    }

    // ---- full 16x16 texture with rotation (rot = number of 90deg CW steps) ----
    public static void renderTexture(ResourceLocation texture, int x, int y, int rot, float a) {
        renderTexture(texture, x, y, rot, 1f, 1f, 1f, a);
    }

    public static void renderTexture(ResourceLocation texture, int x, int y, int rot, float r, float g, float b, float a) {
        // full texture (u=0,v=0,w=16,h=16)
        renderPartial(texture, x, y, rot, r, g, b, a, 0, 0, COMPONENT_SIZE, COMPONENT_SIZE, COMPONENT_SIZE, COMPONENT_SIZE);
    }

    /**
     * Renders a sub-rectangle (u,v,w,h px) of a texture (sized texW x texH) at the given offset inside
     * the 16x16 component cell, with the cell rotated rot*90 degrees about its center.
     */
    public static void renderPartial(ResourceLocation texture, int componentX, int componentY, int rot,
                                     float r, float g, float b, float a,
                                     int u, int v, int w, int h, int texW, int texH) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

        GlStateManager.pushMatrix();
        GlStateManager.translate(componentX + 8.0, componentY + 8.0, 0);
        GlStateManager.rotate(rot * 90.0f, 0, 0, 1);
        GlStateManager.translate(-8.0, -8.0, 0);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(r, g, b, a);

        float fu0 = (float) u / texW;
        float fv0 = (float) v / texH;
        float fu1 = (float) (u + w) / texW;
        float fv1 = (float) (v + h) / texH;

        // destination matches the sub-rect's pixel position inside the cell
        double x0 = u;
        double y0 = v;
        double x1 = u + w;
        double y1 = v + h;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x0, y1, 0).tex(fu0, fv1).endVertex();
        buffer.pos(x1, y1, 0).tex(fu1, fv1).endVertex();
        buffer.pos(x1, y0, 0).tex(fu1, fv0).endVertex();
        buffer.pos(x0, y0, 0).tex(fu0, fv0).endVertex();
        tessellator.draw();

        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.popMatrix();
    }

    /** Convenience: render a 16-px texture's sub-rect (texture is 16x16). */
    public static void renderSub16(ResourceLocation texture, int x, int y, int rot, float r, float g, float b, float a,
                                   int u, int v, int w, int h) {
        renderPartial(texture, x, y, rot, r, g, b, a, u, v, w, h, COMPONENT_SIZE, COMPONENT_SIZE);
    }
}
