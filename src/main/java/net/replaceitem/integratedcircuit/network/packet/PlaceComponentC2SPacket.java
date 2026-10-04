package net.replaceitem.integratedcircuit.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.replaceitem.integratedcircuit.IntegratedCircuit;
import net.replaceitem.integratedcircuit.IntegratedCircuitBlock;
import net.replaceitem.integratedcircuit.IntegratedCircuitBlockEntity;
import net.replaceitem.integratedcircuit.circuit.Component;
import net.replaceitem.integratedcircuit.circuit.Components;
import net.replaceitem.integratedcircuit.util.ComponentPos;
import net.replaceitem.integratedcircuit.util.FlatDirection;

/** C2S: client placed/broke a component at a grid cell. */
public class PlaceComponentC2SPacket implements IMessage {
    private int posX, posY;
    private BlockPos blockPos;
    private String componentName;
    private int rotation;

    public PlaceComponentC2SPacket() {}

    public PlaceComponentC2SPacket(ComponentPos pos, BlockPos blockPos, Component component, FlatDirection rotation) {
        this.posX = pos.getX();
        this.posY = pos.getY();
        this.blockPos = blockPos;
        this.componentName = component.getRegistryName();
        this.rotation = rotation.getIndex();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.posX = buf.readInt();
        this.posY = buf.readInt();
        this.blockPos = BlockPos.fromLong(buf.readLong());
        this.componentName = ByteBufUtils.readUTF8String(buf);
        this.rotation = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(posX);
        buf.writeInt(posY);
        buf.writeLong(blockPos.toLong());
        ByteBufUtils.writeUTF8String(buf, componentName);
        buf.writeByte(rotation);
    }

    public static class Handler implements IMessageHandler<PlaceComponentC2SPacket, IMessage> {
        @Override
        public IMessage onMessage(PlaceComponentC2SPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = player.getServerWorld();
            world.addScheduledTask(() -> {
                if (!(world.getTileEntity(message.blockPos) instanceof IntegratedCircuitBlockEntity)) return;
                IntegratedCircuitBlockEntity be = (IntegratedCircuitBlockEntity) world.getTileEntity(message.blockPos);
                if (!(world.getBlockState(message.blockPos).getBlock() instanceof IntegratedCircuitBlock)) return;
                if (be.getCircuit() == null) return;
                if (!be.isEditing(player)) return;
                Component component = Components.byName(message.componentName);
                if (component == null) return;
                FlatDirection rot = FlatDirection.VALUES[message.rotation & 3];
                be.getCircuit().placeComponentState(new ComponentPos(message.posX, message.posY), component, rot);
            });
            return null;
        }
    }
}
