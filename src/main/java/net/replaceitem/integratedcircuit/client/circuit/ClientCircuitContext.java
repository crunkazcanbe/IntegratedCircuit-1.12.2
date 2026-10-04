package net.replaceitem.integratedcircuit.client.circuit;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

/** Minimal client-side context for a {@link ClientCircuit} living inside the editor screen. */
public class ClientCircuitContext {
    private final BlockPos blockPos;

    public ClientCircuitContext(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public long getTime() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null) return 0;
        return mc.world.getTotalWorldTime();
    }

    public void playSound(@Nullable EntityPlayer except, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world != null) {
            mc.world.playSound(except, blockPos, sound, category, volume, pitch);
        }
    }
}
