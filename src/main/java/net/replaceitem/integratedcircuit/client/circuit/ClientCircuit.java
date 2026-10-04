package net.replaceitem.integratedcircuit.client.circuit;

import net.minecraft.block.SoundType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.replaceitem.integratedcircuit.circuit.Circuit;
import net.replaceitem.integratedcircuit.circuit.CircuitSection;
import net.replaceitem.integratedcircuit.circuit.CircuitTickScheduler;
import net.replaceitem.integratedcircuit.circuit.Component;
import net.replaceitem.integratedcircuit.circuit.ComponentState;
import net.replaceitem.integratedcircuit.circuit.Components;
import net.replaceitem.integratedcircuit.network.IntegratedCircuitNetwork;
import net.replaceitem.integratedcircuit.network.packet.ComponentInteractionC2SPacket;
import net.replaceitem.integratedcircuit.network.packet.PlaceComponentC2SPacket;
import net.replaceitem.integratedcircuit.util.ComponentPos;
import net.replaceitem.integratedcircuit.util.FlatDirection;

import javax.annotation.Nullable;

/** Client-side circuit used by the editor screen; edits are mirrored optimistically and sent C2S. */
public class ClientCircuit extends Circuit {

    private final ClientCircuitContext context;
    private final CircuitTickScheduler clientTickScheduler = new CircuitTickScheduler();

    public ClientCircuit(ClientCircuitContext context) {
        super(true);
        this.context = context;
    }

    public ClientCircuit(ClientCircuitContext context, ComponentState[] portStates, CircuitSection section) {
        super(true, portStates, section);
        this.context = context;
    }

    public ClientCircuitContext getContext() {
        return context;
    }

    @Override
    public CircuitTickScheduler getCircuitTickScheduler() {
        return clientTickScheduler;
    }

    public void onComponentUpdateFromServer(ComponentState state, ComponentPos pos) {
        this.setComponentState(pos, state, Component.NOTIFY_ALL | Component.FORCE_STATE);
    }

    @Override
    public void placeComponentState(ComponentPos pos, Component component, FlatDirection placementRotation) {
        BlockPos blockPos = this.context.getBlockPos();
        IntegratedCircuitNetwork.CHANNEL.sendToServer(new PlaceComponentC2SPacket(pos, blockPos, component, placementRotation));

        ComponentState placementState = component.getPlacementState(this, pos, placementRotation);
        if (placementState != null) {
            boolean breaking = component == Components.AIR;
            SoundType soundGroup = (breaking ? getComponentState(pos).getComponent() : component).getSettings().soundGroup;
            boolean success = setComponentState(pos, placementState, Component.NOTIFY_ALL);
            if (success) {
                EntityPlayer player = Minecraft.getMinecraft().player;
                playSound(player, breaking ? soundGroup.getBreakSound() : soundGroup.getPlaceSound(), SoundCategory.BLOCKS,
                        (soundGroup.getVolume() + 1.0f) / 2.0f, soundGroup.getPitch());
            }
        }
    }

    public void breakComponentState(ComponentPos pos) {
        this.placeComponentState(pos, Components.AIR, FlatDirection.NORTH);
    }

    @Override
    public void useComponent(ComponentPos pos, EntityPlayer player) {
        IntegratedCircuitNetwork.CHANNEL.sendToServer(new ComponentInteractionC2SPacket(pos, this.context.getBlockPos()));
        super.useComponent(pos, player);
    }

    @Override
    public long getTime() {
        return context.getTime();
    }

    @Override
    protected void playSoundInternal(@Nullable EntityPlayer except, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        this.context.playSound(except, sound, category, volume, pitch);
    }

    // Client circuit is a passive mirror; no neighbour propagation needed (server is authoritative).
    @Override
    protected void updateListeners(ComponentPos pos, ComponentState oldState, ComponentState state, int flags) {
    }

    @Override
    public void updateNeighbors(ComponentPos pos, Component component) {
    }

    @Override
    public void updateNeighborsAlways(ComponentPos pos, Component sourceComponent) {
    }

    @Override
    public void updateNeighborsExcept(ComponentPos pos, Component sourceComponent, FlatDirection direction) {
    }

    @Override
    public void updateNeighbor(ComponentPos pos, Component sourceComponent, ComponentPos sourcePos) {
    }

    @Override
    public void updateNeighbor(ComponentState state, ComponentPos pos, Component sourceComponent, ComponentPos sourcePos, boolean notify) {
    }
}
