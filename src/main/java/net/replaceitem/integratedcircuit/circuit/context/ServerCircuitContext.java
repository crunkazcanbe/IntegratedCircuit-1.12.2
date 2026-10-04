package net.replaceitem.integratedcircuit.circuit.context;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.replaceitem.integratedcircuit.circuit.ComponentState;
import net.replaceitem.integratedcircuit.util.ComponentPos;
import net.replaceitem.integratedcircuit.util.FlatDirection;

import javax.annotation.Nullable;
import java.util.Random;

public interface ServerCircuitContext extends CircuitContext {
    Random getRandom();

    void onComponentUpdate(ComponentPos pos, ComponentState state);

    void playSound(@Nullable EntityPlayer except, SoundEvent sound, SoundCategory category, float volume, float pitch);

    void updateExternal(FlatDirection portSide);

    void readExternalPower(FlatDirection portSide);

    void markDirty();

    void setRenderStrength(FlatDirection portSide, int integer);
}
