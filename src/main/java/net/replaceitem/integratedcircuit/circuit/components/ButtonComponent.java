package net.replaceitem.integratedcircuit.circuit.components;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.replaceitem.integratedcircuit.circuit.Circuit;
import net.replaceitem.integratedcircuit.circuit.Component;
import net.replaceitem.integratedcircuit.circuit.ComponentState;
import net.replaceitem.integratedcircuit.circuit.ServerCircuit;
import net.replaceitem.integratedcircuit.circuit.state.BooleanProperty;
import net.replaceitem.integratedcircuit.circuit.state.StateManager;
import net.replaceitem.integratedcircuit.util.ComponentPos;
import net.replaceitem.integratedcircuit.util.FlatDirection;

import javax.annotation.Nullable;
import java.util.Random;

public class ButtonComponent extends FacingComponent {
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    private final boolean wooden;

    public ButtonComponent(Settings settings, boolean wooden) {
        super(settings);
        this.wooden = wooden;
        this.setDefaultState(this.getStateManager().any().setValue(POWERED, false));
    }

    @Override
    public void onUse(ComponentState state, Circuit circuit, ComponentPos pos, EntityPlayer player) {
        if (state.getValue(POWERED)) return;
        powerOn(state, circuit, pos);
        this.playClickSound(player, circuit, true);
    }

    protected void playClickSound(@Nullable EntityPlayer player, Circuit circuit, boolean powered) {
        circuit.playSound(powered ? player : null, this.getClickSound(powered), SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    protected SoundEvent getClickSound(boolean powered) {
        if (this.wooden) {
            return powered ? SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON : SoundEvents.BLOCK_WOOD_BUTTON_CLICK_OFF;
        }
        return powered ? SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON : SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF;
    }

    @Override
    public void onStateReplaced(ComponentState state, Circuit circuit, ComponentPos pos, ComponentState newState) {
        if (state.isOf(newState.getComponent())) return;
        if (state.getValue(POWERED)) {
            circuit.updateNeighborsAlways(pos, this);
        }
    }

    @Override
    public void scheduledTick(ComponentState state, ServerCircuit circuit, ComponentPos pos, Random random) {
        if (!state.getValue(POWERED)) return;
        circuit.setComponentState(pos, state.setValue(POWERED, false), Component.UPDATE_ALL);
        circuit.updateNeighborsAlways(pos, this);
        this.playClickSound(null, circuit, false);
    }

    public void powerOn(ComponentState state, Circuit circuit, ComponentPos pos) {
        circuit.setComponentState(pos, state.setValue(POWERED, true), Component.UPDATE_ALL);
        circuit.updateNeighborsAlways(pos, this);
        circuit.scheduleBlockTick(pos, this, wooden ? 30 : 20);
    }

    @Override
    public int getWeakRedstonePower(ComponentState state, Circuit circuit, ComponentPos pos, FlatDirection direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public boolean isSolidBlock(Circuit circuit, ComponentPos pos) {
        return false;
    }

    @Override
    public boolean emitsRedstonePower(ComponentState state) {
        return true;
    }

    @Override
    public void appendProperties(StateManager builder) {
        super.appendProperties(builder);
        builder.add(POWERED);
    }
}
