package net.replaceitem.integratedcircuit;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.replaceitem.integratedcircuit.circuit.ServerCircuit;
import net.replaceitem.integratedcircuit.util.FlatDirection;

import javax.annotation.Nullable;

public class IntegratedCircuitBlock extends Block implements ITileEntityProvider {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    /** Flat 2px-tall board sitting on top of the ground (matches the original mod). */
    protected static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 2.0 / 16.0, 1.0);

    /** Dye color of this circuit board variant, or null for the default (uncolored) variant. */
    @Nullable
    private final net.minecraft.item.EnumDyeColor color;

    public IntegratedCircuitBlock() {
        this(null);
    }

    public IntegratedCircuitBlock(@Nullable net.minecraft.item.EnumDyeColor color) {
        super(Material.IRON);
        this.color = color;
        this.setHardness(2.0f);
        this.setResistance(10.0f);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Nullable
    public net.minecraft.item.EnumDyeColor getColor() {
        return color;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SHAPE;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return SHAPE;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.getHorizontal(meta);
        return this.getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new IntegratedCircuitBlockEntity();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public net.minecraft.util.BlockRenderLayer getBlockLayer() {
        return net.minecraft.util.BlockRenderLayer.CUTOUT;
    }

    // ---- redstone ----

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return getOutputPower(world, pos, blockState, side.getOpposite());
    }

    @Override
    public int getStrongPower(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return getOutputPower(world, pos, blockState, side.getOpposite());
    }

    /** Reads a circuit port's output strength for a given outgoing world direction. */
    private int getOutputPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing worldDirection) {
        if (worldDirection.getAxis() == EnumFacing.Axis.Y) return 0;
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof IntegratedCircuitBlockEntity)) return 0;
        ServerCircuit circuit = ((IntegratedCircuitBlockEntity) te).getCircuit();
        if (circuit == null) return 0;
        FlatDirection portSide = FlatDirection.fromVanillaDirection(state, worldDirection);
        return circuit.getPortOutputStrength(portSide);
    }

    /** Reads incoming vanilla redstone power into a port (used by the circuit context). */
    public int getInputPower(World world, BlockPos pos, IBlockState state, FlatDirection portSide) {
        EnumFacing worldDirection = portSide.toVanillaDirection(state);
        BlockPos neighborPos = pos.offset(worldDirection);
        int power = world.getRedstonePower(neighborPos, worldDirection);
        if (power >= 15) return power;
        IBlockState neighborState = world.getBlockState(neighborPos);
        if (neighborState.getBlock() == net.minecraft.init.Blocks.REDSTONE_WIRE) {
            power = Math.max(power, neighborState.getValue(net.minecraft.block.BlockRedstoneWire.POWER));
        }
        return power;
    }

    /** Notifies vanilla neighbours that a port output changed. */
    public void updateTarget(World world, BlockPos pos, EnumFacing worldDirection) {
        BlockPos neighborPos = pos.offset(worldDirection);
        world.neighborChanged(neighborPos, this, pos);
        world.notifyNeighborsOfStateExcept(neighborPos, this, worldDirection.getOpposite());
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        if (!(player instanceof net.minecraft.entity.player.EntityPlayerMP)) return true;
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof IntegratedCircuitBlockEntity)) return true;
        IntegratedCircuitBlockEntity be = (IntegratedCircuitBlockEntity) te;
        net.minecraft.entity.player.EntityPlayerMP playerMP = (net.minecraft.entity.player.EntityPlayerMP) player;
        be.addEditor(playerMP);
        String name = "Integrated Circuit";
        net.replaceitem.integratedcircuit.network.IntegratedCircuitNetwork.CHANNEL.sendTo(
                new net.replaceitem.integratedcircuit.network.packet.OpenScreenS2CPacket(pos, be.getCircuitNbtForClient(), name),
                playerMP
        );
        return true;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (world.isRemote) return;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof IntegratedCircuitBlockEntity) {
            ((IntegratedCircuitBlockEntity) te).onNeighborChanged();
        }
    }
}
