package net.replaceitem.integratedcircuit;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.replaceitem.integratedcircuit.circuit.CircuitSerializer;
import net.replaceitem.integratedcircuit.circuit.ComponentState;
import net.replaceitem.integratedcircuit.circuit.ServerCircuit;
import net.replaceitem.integratedcircuit.circuit.context.BlockEntityServerCircuitContext;
import net.replaceitem.integratedcircuit.network.IntegratedCircuitNetwork;
import net.replaceitem.integratedcircuit.network.packet.ComponentUpdateS2CPacket;
import net.replaceitem.integratedcircuit.util.ComponentPos;
import net.replaceitem.integratedcircuit.util.FlatDirection;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class IntegratedCircuitBlockEntity extends TileEntity implements ITickable {

    private static final String CIRCUIT_TAG = "circuit";

    @Nullable
    private ServerCircuit circuit;
    private final BlockEntityServerCircuitContext context = new BlockEntityServerCircuitContext(this);

    /** Per-port output signal strength, cached for vanilla redstone queries / rendering. */
    private final int[] renderSignalStrengths = new int[FlatDirection.VALUES.length];

    /** Players currently editing this circuit in the GUI (server side only). */
    private final Set<EntityPlayerMP> editingPlayers = new HashSet<>();

    public IntegratedCircuitBlockEntity() {
    }

    @Nullable
    public ServerCircuit getCircuit() {
        if (circuit == null && world != null && !world.isRemote) {
            circuit = new ServerCircuit(context);
        }
        return circuit;
    }

    public void setRenderSignalStrength(FlatDirection portSide, int power) {
        renderSignalStrengths[portSide.getIndex()] = power;
    }

    public int getRenderSignalStrength(FlatDirection portSide) {
        return renderSignalStrengths[portSide.getIndex()];
    }

    // ---- editor tracking ----

    public boolean isEditing(EntityPlayer player) {
        return player instanceof EntityPlayerMP && editingPlayers.contains(player);
    }

    public void addEditor(EntityPlayerMP player) {
        editingPlayers.add(player);
    }

    public void removeEditor(EntityPlayerMP player) {
        editingPlayers.remove(player);
    }

    public Set<EntityPlayerMP> getEditingPlayers() {
        return editingPlayers;
    }

    /** Serializes the current circuit (creating a default one if needed) into a fresh NBT tag for the GUI. */
    public NBTTagCompound getCircuitNbtForClient() {
        NBTTagCompound tag = new NBTTagCompound();
        ServerCircuit c = getCircuit();
        if (c != null) {
            CircuitSerializer.writeCircuit(c, tag);
        }
        return tag;
    }

    /** Called from the circuit context whenever a component state changes; mirror to editors. */
    public void onComponentUpdate(ComponentPos pos, ComponentState state) {
        if (world == null || world.isRemote) return;
        if (editingPlayers.isEmpty()) return;
        ComponentUpdateS2CPacket packet = new ComponentUpdateS2CPacket(pos, state.getStateId());
        for (EntityPlayerMP player : editingPlayers) {
            IntegratedCircuitNetwork.CHANNEL.sendTo(packet, player);
        }
    }

    public void onNeighborChanged() {
        if (world == null || world.isRemote) return;
        ServerCircuit c = getCircuit();
        if (c == null) return;
        for (FlatDirection portSide : FlatDirection.VALUES) {
            context.readExternalPower(portSide);
        }
    }

    @Override
    public void update() {
        if (world == null || world.isRemote) return;
        ServerCircuit c = getCircuit();
        if (c != null) {
            c.tick();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(CIRCUIT_TAG)) {
            NBTTagCompound circuitTag = compound.getCompoundTag(CIRCUIT_TAG);
            CircuitSerializer serializer = new CircuitSerializer(circuitTag);
            this.circuit = serializer.readServerCircuit(context);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (circuit != null) {
            NBTTagCompound circuitTag = new NBTTagCompound();
            CircuitSerializer.writeCircuit(circuit, circuitTag);
            compound.setTag(CIRCUIT_TAG, circuitTag);
        }
        return compound;
    }
}
