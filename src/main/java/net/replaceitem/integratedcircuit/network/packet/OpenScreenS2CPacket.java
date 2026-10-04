package net.replaceitem.integratedcircuit.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** S2C: open the circuit editor screen with a snapshot of the circuit. */
public class OpenScreenS2CPacket implements IMessage {
    private BlockPos pos;
    private NBTTagCompound circuitNbt;
    private String customName;

    public OpenScreenS2CPacket() {}

    public OpenScreenS2CPacket(BlockPos pos, NBTTagCompound circuitNbt, String customName) {
        this.pos = pos;
        this.circuitNbt = circuitNbt;
        this.customName = customName;
    }

    public BlockPos getPos() {
        return pos;
    }

    public NBTTagCompound getCircuitNbt() {
        return circuitNbt;
    }

    public String getCustomName() {
        return customName;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = BlockPos.fromLong(buf.readLong());
        this.circuitNbt = ByteBufUtils.readTag(buf);
        this.customName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        ByteBufUtils.writeTag(buf, circuitNbt == null ? new NBTTagCompound() : circuitNbt);
        ByteBufUtils.writeUTF8String(buf, customName == null ? "" : customName);
    }

    public static class Handler implements IMessageHandler<OpenScreenS2CPacket, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(OpenScreenS2CPacket message, MessageContext ctx) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() ->
                    net.replaceitem.integratedcircuit.client.network.ClientPacketHandler.handleOpenScreen(message));
            return null;
        }
    }
}
