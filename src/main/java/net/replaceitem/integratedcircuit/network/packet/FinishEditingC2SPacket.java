package net.replaceitem.integratedcircuit.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.replaceitem.integratedcircuit.IntegratedCircuitBlockEntity;

/** C2S: client closed the editor screen. */
public class FinishEditingC2SPacket implements IMessage {
    private BlockPos pos;

    public FinishEditingC2SPacket() {}

    public FinishEditingC2SPacket(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
    }

    public static class Handler implements IMessageHandler<FinishEditingC2SPacket, IMessage> {
        @Override
        public IMessage onMessage(FinishEditingC2SPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = player.getServerWorld();
            world.addScheduledTask(() -> {
                player.resetActiveHand();
                if (world.getTileEntity(message.pos) instanceof IntegratedCircuitBlockEntity) {
                    ((IntegratedCircuitBlockEntity) world.getTileEntity(message.pos)).removeEditor(player);
                }
            });
            return null;
        }
    }
}
