package net.replaceitem.integratedcircuit.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.replaceitem.integratedcircuit.util.ComponentPos;

/** S2C: server pushes a single changed grid cell (by global state id) to editing clients. */
public class ComponentUpdateS2CPacket implements IMessage {
    private int posX, posY;
    private int stateId;

    public ComponentUpdateS2CPacket() {}

    public ComponentUpdateS2CPacket(ComponentPos pos, int stateId) {
        this.posX = pos.getX();
        this.posY = pos.getY();
        this.stateId = stateId;
    }

    public ComponentPos getPos() {
        return new ComponentPos(posX, posY);
    }

    public int getStateId() {
        return stateId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.posX = buf.readInt();
        this.posY = buf.readInt();
        this.stateId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(posX);
        buf.writeInt(posY);
        buf.writeInt(stateId);
    }

    public static class Handler implements IMessageHandler<ComponentUpdateS2CPacket, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(ComponentUpdateS2CPacket message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() ->
                    net.replaceitem.integratedcircuit.client.network.ClientPacketHandler.handleComponentUpdate(message));
            return null;
        }
    }
}
