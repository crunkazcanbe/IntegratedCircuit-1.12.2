package net.replaceitem.integratedcircuit.circuit.components;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.replaceitem.integratedcircuit.circuit.Circuit;
import net.replaceitem.integratedcircuit.circuit.Component;
import net.replaceitem.integratedcircuit.circuit.ComponentState;
import net.replaceitem.integratedcircuit.circuit.ServerCircuit;
import net.replaceitem.integratedcircuit.circuit.TickPriority;
import net.replaceitem.integratedcircuit.circuit.state.ComparatorMode;
import net.replaceitem.integratedcircuit.circuit.state.EnumProperty;
import net.replaceitem.integratedcircuit.circuit.state.IntegerProperty;
import net.replaceitem.integratedcircuit.circuit.state.StateManager;
import net.replaceitem.integratedcircuit.util.ComponentPos;
import net.replaceitem.integratedcircuit.util.FlatDirection;

import java.util.Random;

public class ComparatorComponent extends AbstractRedstoneGateComponent {
    public static final EnumProperty<ComparatorMode> MODE = EnumProperty.create("mode", ComparatorMode.class);
    public static final IntegerProperty OUTPUT_POWER = IntegerProperty.create("power", 0, 15);

    public ComparatorComponent(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().any().setValue(FACING, FlatDirection.NORTH).setValue(POWERED, false).setValue(MODE, ComparatorMode.COMPARE).setValue(OUTPUT_POWER, 0));
    }

    @Override
    protected int getUpdateDelayInternal(ComponentState state) {
        return 2;
    }

    @Override
    protected int getOutputLevel(Circuit circuit, ComponentPos pos, ComponentState state) {
        return state.getValue(OUTPUT_POWER);
    }

    private int calculateOutputSignal(Circuit world, ComponentPos pos, ComponentState state) {
        int i = this.getPower(world, pos, state);
        if (i == 0) {
            return 0;
        }
        int j = this.getMaxInputLevelSides(world, pos, state);
        if (j > i) {
            return 0;
        }
        if (state.getValue(MODE) == ComparatorMode.SUBTRACT) {
            return i - j;
        }
        return i;
    }

    @Override
    protected boolean hasPower(Circuit circuit, ComponentPos pos, ComponentState state) {
        int i = this.getPower(circuit, pos, state);
        if (i == 0) {
            return false;
        }
        int j = this.getMaxInputLevelSides(circuit, pos, state);
        if (i > j) {
            return true;
        }
        return i == j && state.getValue(MODE) == ComparatorMode.COMPARE;
    }

    @Override
    protected int getPower(Circuit circuit, ComponentPos pos, ComponentState state) {
        int power = super.getPower(circuit, pos, state);
        FlatDirection direction = state.getValue(FACING);
        ComponentPos offsetPos = pos.offset(direction);
        ComponentState offsetState = circuit.getComponentState(offsetPos);
        if (offsetState.hasComparatorOutput()) {
            power = offsetState.getComparatorOutput(circuit, offsetPos);
        } else if (power < 15 && offsetState.isSolidBlock(circuit, offsetPos)) {
            offsetPos = offsetPos.offset(direction);
            offsetState = circuit.getComponentState(offsetPos);
            if (offsetState.hasComparatorOutput()) {
                power = offsetState.getComparatorOutput(circuit, offsetPos);
            }
        }
        return power;
    }

    @Override
    public void onUse(ComponentState state, Circuit circuit, ComponentPos pos, EntityPlayer player) {
        state = state.cycle(MODE);
        circuit.setComponentState(pos, state, Component.UPDATE_CLIENTS);
        this.update(circuit, pos, state);
        float f = state.getValue(MODE) == ComparatorMode.SUBTRACT ? 0.55f : 0.5f;
        circuit.playSound(player, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 1, f);
    }

    @Override
    protected void updatePowered(Circuit circuit, ComponentPos pos, ComponentState state) {
        if (circuit.getCircuitTickScheduler().isTicking(pos, this)) {
            return;
        }
        int calculatedOutputSignal = this.calculateOutputSignal(circuit, pos, state);
        int outputSignal = state.getValue(OUTPUT_POWER);
        if (calculatedOutputSignal != outputSignal || state.getValue(POWERED) != this.hasPower(circuit, pos, state)) {
            TickPriority tickPriority = this.isTargetNotAligned(circuit, pos, state) ? TickPriority.HIGH : TickPriority.NORMAL;
            circuit.scheduleBlockTick(pos, this, 2, tickPriority);
        }
    }

    private void update(Circuit world, ComponentPos pos, ComponentState state) {
        int i = this.calculateOutputSignal(world, pos, state);
        int j = state.getValue(OUTPUT_POWER);
        state = state.setValue(OUTPUT_POWER, i); // done in a BE in vanilla; here we re-place the state below
        if (j != i || state.getValue(MODE) == ComparatorMode.COMPARE) {
            boolean hasPower = this.hasPower(world, pos, state);
            boolean powered = state.getValue(POWERED);
            if (powered && !hasPower) {
                world.setComponentState(pos, state.setValue(POWERED, false), NOTIFY_LISTENERS);
            } else if (!powered && hasPower) {
                world.setComponentState(pos, state.setValue(POWERED, true), NOTIFY_LISTENERS);
            } else if (j != i) {
                world.setComponentState(pos, state, NOTIFY_LISTENERS);
            }
            this.updateTarget(world, pos, state);
        }
    }

    @Override
    public void scheduledTick(ComponentState state, ServerCircuit circuit, ComponentPos pos, Random random) {
        this.update(circuit, pos, state);
    }

    @Override
    public void appendProperties(StateManager builder) {
        super.appendProperties(builder);
        builder.add(MODE);
        builder.add(OUTPUT_POWER);
    }
}
