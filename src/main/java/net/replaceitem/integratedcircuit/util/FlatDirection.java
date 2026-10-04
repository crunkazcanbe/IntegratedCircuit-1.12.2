package net.replaceitem.integratedcircuit.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;
import net.replaceitem.integratedcircuit.IntegratedCircuitBlock;

/** 2D facing on the circuit grid, with conversions to/from vanilla EnumFacing. */
public enum FlatDirection {
    NORTH(0, "north", 0, -1, Axis.Y, EnumFacing.NORTH),
    EAST(1, "east", 1, 0, Axis.X, EnumFacing.EAST),
    SOUTH(2, "south", 0, 1, Axis.Y, EnumFacing.SOUTH),
    WEST(3, "west", -1, 0, Axis.X, EnumFacing.WEST);

    private final int index;
    private final String name;
    private final Vec3i offset;
    private final Axis axis;
    private final EnumFacing vanillaDirection;

    public static final FlatDirection[] VALUES = FlatDirection.values();
    public static final FlatDirection[] VALUES_X = {EAST, WEST};
    public static final FlatDirection[] VALUES_Y = {NORTH, SOUTH};

    FlatDirection(int index, String name, int dx, int dy, Axis axis, EnumFacing vanillaDirection) {
        this.index = index;
        this.name = name;
        this.offset = new Vec3i(dx, dy, 0);
        this.axis = axis;
        this.vanillaDirection = vanillaDirection;
    }

    public int getIndex() {
        return this.index;
    }

    public Vec3i getOffset() {
        return offset;
    }

    public Axis getAxis() {
        return axis;
    }

    public FlatDirection rotated(int times) {
        return VALUES[Math.floorMod(this.getIndex() + times, 4)];
    }

    public FlatDirection rotatedCounterclockwise(int times) {
        return this.rotated(-times);
    }

    public FlatDirection getOpposite() {
        return this.rotated(2);
    }

    public EnumFacing toVanillaDirection() {
        return vanillaDirection;
    }

    public EnumFacing toVanillaDirection(EnumFacing facing) {
        return this.rotated(FlatDirection.fromVanillaDirection(facing).getIndex()).toVanillaDirection();
    }

    public EnumFacing toVanillaDirection(IBlockState circuit) {
        if (circuit.getProperties().containsKey(IntegratedCircuitBlock.FACING))
            return this.toVanillaDirection(circuit.getValue(IntegratedCircuitBlock.FACING));
        return this.toVanillaDirection();
    }

    public static FlatDirection fromVanillaDirection(EnumFacing direction) {
        switch (direction) {
            case NORTH: return NORTH;
            case SOUTH: return SOUTH;
            case WEST: return WEST;
            case EAST: return EAST;
            default: throw new IllegalArgumentException("Cannot convert vertical EnumFacing to FlatDirection");
        }
    }

    public static FlatDirection fromVanillaDirection(EnumFacing facing, EnumFacing direction) {
        return FlatDirection.fromVanillaDirection(direction).rotatedCounterclockwise(FlatDirection.fromVanillaDirection(facing).getIndex());
    }

    public static FlatDirection fromVanillaDirection(IBlockState circuit, EnumFacing direction) {
        if (circuit.getProperties().containsKey(IntegratedCircuitBlock.FACING))
            return FlatDirection.fromVanillaDirection(circuit.getValue(IntegratedCircuitBlock.FACING), direction);
        return FlatDirection.fromVanillaDirection(direction);
    }

    public static FlatDirection[] forAxis(Axis axis) {
        return axis == Axis.X ? VALUES_X : VALUES_Y;
    }

    public String getSerializedName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public enum Axis {
        X, Y
    }
}
