package net.replaceitem.integratedcircuit.circuit.components;

import net.replaceitem.integratedcircuit.circuit.Component;
import net.replaceitem.integratedcircuit.circuit.state.EnumProperty;
import net.replaceitem.integratedcircuit.circuit.state.StateManager;
import net.replaceitem.integratedcircuit.util.FlatDirection;

public abstract class FacingComponent extends Component {

    public static final EnumProperty<FlatDirection> FACING = EnumProperty.create("facing", FlatDirection.class);

    public FacingComponent(Settings settings) {
        super(settings);
    }

    @Override
    public void appendProperties(StateManager builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }
}
