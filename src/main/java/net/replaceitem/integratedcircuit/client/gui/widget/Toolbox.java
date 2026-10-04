package net.replaceitem.integratedcircuit.client.gui.widget;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.replaceitem.integratedcircuit.IntegratedCircuit;
import net.replaceitem.integratedcircuit.circuit.Component;
import net.replaceitem.integratedcircuit.circuit.Components;
import net.replaceitem.integratedcircuit.client.gui.IntegratedCircuitScreen;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** The component palette down the left side of the editor. */
public class Toolbox {
    private static final int PALETTE_COLS = 4;
    private static final int BUTTON_ID_BASE = 1000;

    private final IntegratedCircuitScreen owner;
    private final int x;
    private final int y;

    public static class PaletteEntry {
        public final Component component;
        public final ResourceLocation itemTexture;
        public PaletteEntry(Component component, ResourceLocation itemTexture) {
            this.component = component;
            this.itemTexture = itemTexture;
        }
    }

    private static ResourceLocation icon(String name) {
        return IntegratedCircuit.id("textures/toolbox/icons/" + name + ".png");
    }

    private static final PaletteEntry[] PALETTE = new PaletteEntry[] {
            new PaletteEntry(Components.WIRE, icon("redstone")),
            new PaletteEntry(Components.CROSSOVER, icon("crossover")),
            new PaletteEntry(Components.TORCH, icon("torch")),
            new PaletteEntry(Components.REDSTONE_BLOCK, icon("redstone_block")),
            new PaletteEntry(Components.REPEATER, icon("repeater")),
            new PaletteEntry(Components.COMPARATOR, icon("comparator")),
            new PaletteEntry(Components.OBSERVER, icon("observer")),
            new PaletteEntry(Components.BLOCK, icon("block")),
            new PaletteEntry(Components.LAMP, icon("lamp")),
            new PaletteEntry(Components.STONE_BUTTON, icon("button_stone")),
            new PaletteEntry(Components.WOODEN_BUTTON, icon("button_wood")),
            new PaletteEntry(Components.LEVER, icon("lever")),
    };

    private int selectedToolSlot = -1;
    private final List<ToolboxButton> componentButtons = new ArrayList<>(PALETTE.length);
    private final List<Consumer<Component>> toolSelectionSubscribers = new ArrayList<>();

    public Toolbox(IntegratedCircuitScreen owner, int x, int y) {
        this.owner = owner;
        this.x = x;
        this.y = y;
    }

    /** Builds the buttons and adds them to the screen's button list. */
    public void init(List<GuiButton> buttonList) {
        componentButtons.clear();
        for (int i = 0; i < PALETTE.length; i++) {
            PaletteEntry entry = PALETTE[i];

            int slotColumn = i % PALETTE_COLS;
            int slotX = slotColumn * ToolboxButton.SIZE + ToolboxButton.MARGIN * (slotColumn + 1);

            int slotRow = i / PALETTE_COLS;
            int slotY = slotRow * ToolboxButton.SIZE + ToolboxButton.MARGIN * (slotRow + 1);

            ToolboxButton button = new ToolboxButton(BUTTON_ID_BASE + i,
                    owner.getX() + this.x + slotX,
                    owner.getY() + this.y + slotY,
                    entry.component, entry.itemTexture);
            componentButtons.add(button);
            buttonList.add(button);
        }
    }

    /** Called by the screen's actionPerformed; returns true if it was a toolbox button. */
    public boolean onButtonClicked(GuiButton button) {
        int idx = componentButtons.indexOf(button);
        if (idx < 0) return false;
        if (selectedToolSlot == idx) {
            deselectTool();
        } else {
            selectTool(idx);
        }
        return true;
    }

    public void registerToolSelectionSubscriber(Consumer<Component> subscriber) {
        toolSelectionSubscribers.add(subscriber);
    }

    public void selectTool(int index) {
        if (index < 0 || index >= componentButtons.size()) {
            deselectTool();
            return;
        }
        if (selectedToolSlot >= 0 && selectedToolSlot < componentButtons.size())
            componentButtons.get(selectedToolSlot).setSelected(false);
        selectedToolSlot = index;
        ToolboxButton button = componentButtons.get(index);
        button.setSelected(true);
        notifySubscribers(button.getComponent());
    }

    public void deselectTool() {
        if (selectedToolSlot >= 0 && selectedToolSlot < componentButtons.size())
            componentButtons.get(selectedToolSlot).setSelected(false);
        selectedToolSlot = -1;
        notifySubscribers(null);
    }

    private void notifySubscribers(@Nullable Component component) {
        for (Consumer<Component> subscriber : toolSelectionSubscribers) {
            subscriber.accept(component);
        }
    }

    public int getComponentIndex(Component component) {
        for (int i = 0; i < componentButtons.size(); i++) {
            if (componentButtons.get(i).getComponent() == component) return i;
        }
        return -1;
    }

    public int getSelectedToolSlot() {
        return selectedToolSlot;
    }
}
