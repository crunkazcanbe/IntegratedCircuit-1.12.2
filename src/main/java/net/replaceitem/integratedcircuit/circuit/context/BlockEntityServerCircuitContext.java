package net.replaceitem.integratedcircuit.circuit.context;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.replaceitem.integratedcircuit.IntegratedCircuitBlock;
import net.replaceitem.integratedcircuit.IntegratedCircuitBlockEntity;
import net.replaceitem.integratedcircuit.circuit.ComponentState;
import net.replaceitem.integratedcircuit.util.ComponentPos;
import net.replaceitem.integratedcircuit.util.FlatDirection;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockEntityServerCircuitContext implements ServerCircuitContext {

    private final IntegratedCircuitBlockEntity blockEntity;

    public BlockEntityServerCircuitContext(IntegratedCircuitBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Nullable
    private World getWorld() {
        return this.blockEntity.getWorld();
    }

    private BlockPos getPos() {
        return this.blockEntity.getPos();
    }

    @Override
    public Random getRandom() {
        World world = getWorld();
        if (world == null) return new Random();
        return world.rand;
    }

    @Override
    public void markDirty() {
        blockEntity.markDirty();
    }

    @Override
    public void setRenderStrength(FlatDirection portSide, int power) {
        this.blockEntity.setRenderSignalStrength(portSide, power);
        markDirty();
        World world = getWorld();
        if (world != null) {
            BlockPos pos = getPos();
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, net.replaceitem.integratedcircuit.circuit.Component.NOTIFY_LISTENERS);
        }
    }

    @Override
    public void onComponentUpdate(ComponentPos pos, ComponentState state) {
        this.blockEntity.onComponentUpdate(pos, state);
    }

    @Override
    public void updateExternal(FlatDirection portSide) {
        World world = getWorld();
        if (world == null) return;
        BlockPos pos = getPos();
        IBlockState state = world.getBlockState(pos);
        EnumFacing vanillaDirection = portSide.toVanillaDirection(state);
        if (state.getBlock() instanceof IntegratedCircuitBlock) {
            ((IntegratedCircuitBlock) state.getBlock()).updateTarget(world, pos, vanillaDirection);
        }
    }

    @Override
    public void readExternalPower(FlatDirection direction) {
        World world = getWorld();
        if (world == null) return;
        BlockPos pos = getPos();
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof IntegratedCircuitBlock && blockEntity.getCircuit() != null) {
            int power = ((IntegratedCircuitBlock) state.getBlock()).getInputPower(world, pos, state, direction);
            blockEntity.getCircuit().onExternalPowerChanged(direction, power);
        }
    }

    @Override
    public void playSound(@Nullable EntityPlayer except, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        World world = getWorld();
        if (world != null) {
            BlockPos pos = getPos();
            world.playSound(null, pos, sound, category, volume, pitch);
        }
    }

    @Override
    public long getTime() {
        World world = getWorld();
        if (world == null) return 0;
        return world.getTotalWorldTime();
    }
}
