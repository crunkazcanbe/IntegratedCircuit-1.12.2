package net.replaceitem.integratedcircuit.circuit.components;

import net.replaceitem.integratedcircuit.circuit.Circuit;
import net.replaceitem.integratedcircuit.circuit.Component;
import net.replaceitem.integratedcircuit.circuit.ComponentState;
import net.replaceitem.integratedcircuit.circuit.ServerCircuit;
import net.replaceitem.integratedcircuit.circuit.state.BooleanProperty;
import net.replaceitem.integratedcircuit.circuit.state.StateManager;
import net.replaceitem.integratedcircuit.util.ComponentPos;
import net.replaceitem.integratedcircuit.util.FlatDirection;

import java.util.Random;

public class LampComponent extends Component {
    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    public LampComponent(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().any().setValue(LIT, false));
    }

    @Override
    public ComponentState getPlacementState(Circuit circuit, ComponentPos pos, FlatDirection rotation) {
        return this.getDefaultState().setValue(LIT, circuit.isReceivingRedstonePower(pos));
    }

    @Override
    public void neighborUpdate(ComponentState state, Circuit circuit, ComponentPos pos, Component sourceBlock, ComponentPos sourcePos, boolean notify) {
        if (!circuit.isClient) {
            boolean bl = state.getValue(LIT);
            if (bl != circuit.isReceivingRedstonePower(pos)) {
                if (bl) {
                    circuit.scheduleBlockTick(pos, this, 4);
                } else {
                    circuit.setComponentState(pos, state.cycle(LIT), Component.NOTIFY_LISTENERS);
                }
            }
        }
    }

    @Override
    public void scheduledTick(ComponentState state, ServerCircuit circuit, ComponentPos pos, Random random) {
        if (state.getValue(LIT) && !circuit.isReceivingRedstonePower(pos)) {
            circuit.setComponentState(pos, state.cycle(LIT), Component.NOTIFY_LISTENERS);
        }
    }

    @Override
    public boolean isSolidBlock(Circuit circuit, ComponentPos pos) {
        return true;
    }

    @Override
    public void appendProperties(StateManager builder) {
        super.appendProperties(builder);
        builder.add(LIT);
    }
}
