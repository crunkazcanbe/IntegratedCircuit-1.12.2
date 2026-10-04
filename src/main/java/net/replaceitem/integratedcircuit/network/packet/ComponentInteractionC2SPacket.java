package net.replaceitem.integratedcircuit.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.replaceitem.integratedcircuit.IntegratedCircuitBlock;
import net.replaceitem.integratedcircuit.IntegratedCircuitBlockEntity;
import net.replaceitem.integratedcircuit.util.ComponentPos;

/** C2S: client clicked an existing component (run its onUse server-side). */
public class ComponentInteractionC2SPacket implements IMessage {
    private int posX, posY;
    private BlockPos blockPos;

    public ComponentInteractionC2SPacket() {}

    public ComponentInteractionC2SPacket(ComponentPos pos, BlockPos blockPos) {
        this.posX = pos.getX();
        this.posY = pos.getY();
        this.blockPos = blockPos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.posX = buf.readInt();
        this.posY = buf.readInt();
        this.blockPos = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(posX);
        buf.writeInt(posY);
        buf.writeLong(blockPos.toLong());
    }

    public static class Handler implements IMessageHandler<ComponentInteractionC2SPacket, IMessage> {
        @Override
        public IMessage onMessage(ComponentInteractionC2SPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = player.getServerWorld();
            world.addScheduledTask(() -> {
                if (!(world.getTileEntity(message.blockPos) instanceof IntegratedCircuitBlockEntity)) return;
                IntegratedCircuitBlockEntity be = (IntegratedCircuitBlockEntity) world.getTileEntity(message.blockPos);
                if (!(world.getBlockState(message.blockPos).getBlock() instanceof IntegratedCircuitBlock)) return;
                if (be.getCircuit() == null) return;
                if (!be.isEditing(player)) return;
                be.getCircuit().useComponent(new ComponentPos(message.posX, message.posY), player);
            });
            return null;
        }
    }
}
