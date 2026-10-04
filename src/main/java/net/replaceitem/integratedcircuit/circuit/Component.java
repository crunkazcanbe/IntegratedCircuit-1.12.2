package net.replaceitem.integratedcircuit.circuit;

import net.minecraft.block.SoundType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.replaceitem.integratedcircuit.IntegratedCircuit;
import net.replaceitem.integratedcircuit.circuit.components.FacingComponent;
import net.replaceitem.integratedcircuit.circuit.state.StateManager;
import net.replaceitem.integratedcircuit.util.ComponentPos;
import net.replaceitem.integratedcircuit.util.FlatDirection;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Component {

    // copy from Block, only few are needed, but all kept for future
    public static final int NOTIFY_NEIGHBORS = 1;
    public static final int NOTIFY_LISTENERS = 2;
    public static final int NO_REDRAW = 4;
    public static final int REDRAW_ON_MAIN_THREAD = 8;
    public static final int FORCE_STATE = 16;
    public static final int SKIP_DROPS = 32;
    public static final int MOVED = 64;
    public static final int SKIP_LIGHTING_UPDATES = 128;

    public static final int NOTIFY_ALL = 3;

    // vanilla Block flag aliases (modern Block.UPDATE_* constants used in the original)
    public static final int UPDATE_NEIGHBORS = NOTIFY_NEIGHBORS;
    public static final int UPDATE_CLIENTS = NOTIFY_LISTENERS;
    public static final int UPDATE_INVISIBLE = NO_REDRAW;
    public static final int UPDATE_KNOWN_SHAPE = 16;
    public static final int UPDATE_SUPPRESS_DROPS = SKIP_DROPS;
    public static final int UPDATE_ALL = NOTIFY_ALL;

    /** Global registry of every possible component state, addressed by id. Filled by Components. */
    public static final List<ComponentState> STATE_IDS = new ArrayList<>();

    private final Settings settings;
    private ComponentState defaultState;
    private final StateManager stateManager;

    private String registryName = "unknown";

    public static final FlatDirection[] DIRECTIONS = new FlatDirection[]{FlatDirection.WEST, FlatDirection.EAST, FlatDirection.NORTH, FlatDirection.SOUTH};

    public Component(Settings settings) {
        this.settings = settings;
        this.stateManager = new StateManager(this);
        this.appendProperties(this.stateManager);
        this.stateManager.build();
        this.setDefaultState(this.stateManager.any());
    }

    public void setRegistryName(String name) {
        this.registryName = name;
    }

    public String getRegistryName() {
        return registryName;
    }

    public Settings getSettings() {
        return settings;
    }

    public ITextComponent getName() {
        return new TextComponentTranslation("integrated_circuit.component." + IntegratedCircuit.MODID + "." + registryName);
    }

    public void appendProperties(StateManager builder) {
    }

    public void setDefaultState(ComponentState defaultState) {
        this.defaultState = defaultState;
    }

    public final ComponentState getDefaultState() {
        return this.defaultState;
    }

    @Nullable
    public ComponentState getPlacementState(Circuit circuit, ComponentPos pos, FlatDirection rotation) {
        ComponentState defaultState = this.getDefaultState();
        if (this.stateManager.hasProperty(FacingComponent.FACING)) return defaultState.setValue(FacingComponent.FACING, rotation);
        return defaultState;
    }

    public static void replace(ComponentState state, ComponentState newState, Circuit world, ComponentPos pos, int flags) {
        replace(state, newState, world, pos, flags, 512);
    }

    public static void replace(ComponentState state, ComponentState newState, Circuit world, ComponentPos pos, int flags, int maxUpdateDepth) {
        if (newState != state) {
            if (newState.isAir()) {
                if (!world.isClient) {
                    world.breakBlock(pos, maxUpdateDepth);
                }
            } else {
                world.setComponentState(pos, newState, flags & ~SKIP_DROPS, maxUpdateDepth);
            }
        }
    }

    public void neighborUpdate(ComponentState state, Circuit circuit, ComponentPos pos, Component sourceBlock, ComponentPos sourcePos, boolean notify) {
    }

    public void onBlockAdded(ComponentState state, Circuit circuit, ComponentPos pos, ComponentState oldState) {
    }

    public void onStateReplaced(ComponentState state, Circuit circuit, ComponentPos pos, ComponentState newState) {
    }

    public void onUse(ComponentState state, Circuit circuit, ComponentPos pos, EntityPlayer player) {
    }

    public void onPlaced(ServerCircuit circuit, ComponentPos pos, ComponentState state) {
    }

    public void scheduledTick(ComponentState state, ServerCircuit circuit, ComponentPos pos, Random random) {
    }

    public ComponentState getStateForNeighborUpdate(ComponentState state, FlatDirection direction, ComponentState neighborState, Circuit circuit, ComponentPos pos, ComponentPos neighborPos) {
        return state;
    }

    public void prepare(ComponentState state, CircuitAccess circuit, ComponentPos pos, int flags, int maxUpdateDepth) {
    }

    public abstract boolean isSolidBlock(Circuit circuit, ComponentPos pos);

    public boolean isSideSolidFullSquare(Circuit circuit, ComponentPos blockPos, FlatDirection direction) {
        return isSolidBlock(circuit, blockPos);
    }

    public int getWeakRedstonePower(ComponentState state, Circuit circuit, ComponentPos pos, FlatDirection direction) {
        return 0;
    }

    public int getStrongRedstonePower(ComponentState state, Circuit circuit, ComponentPos pos, FlatDirection direction) {
        return 0;
    }

    public boolean hasComparatorOutput(ComponentState componentState) {
        return false;
    }

    public int getComparatorOutput(ComponentState state, Circuit circuit, ComponentPos pos) {
        return 0;
    }

    public int increasePower(ComponentState state, FlatDirection side) {
        return 0;
    }

    public boolean canPlaceAt(ComponentState state, Circuit circuit, ComponentPos pos) {
        return true;
    }

    @Override
    public String toString() {
        return registryName;
    }

    public boolean emitsRedstonePower(ComponentState state) {
        return false;
    }

    public StateManager getStateManager() {
        return stateManager;
    }

    public static class Settings {
        public SoundType soundGroup = SoundType.STONE;

        public Settings() {
        }

        public Settings sounds(SoundType soundGroup) {
            this.soundGroup = soundGroup;
            return this;
        }
    }
}
