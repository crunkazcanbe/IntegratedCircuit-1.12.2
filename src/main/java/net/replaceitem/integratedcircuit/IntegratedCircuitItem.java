package net.replaceitem.integratedcircuit;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class IntegratedCircuitItem extends ItemBlock {
    public IntegratedCircuitItem(Block block) {
        super(block);
        if (block instanceof IntegratedCircuitBlock) {
            this.setUnlocalizedName(IntegratedCircuit.MODID + "." + IntegratedCircuit.variantName((IntegratedCircuitBlock) block));
        } else {
            this.setUnlocalizedName(IntegratedCircuit.MODID + "." + IntegratedCircuit.BLOCK_ID);
        }
    }
}
