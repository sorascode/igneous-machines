package com.herdlicka.igneousmachines.block.entity;

import com.herdlicka.igneousmachines.IgneousMachinesMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class DepositerBlockEntity extends DispenserBlockEntity {
    public DepositerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(IgneousMachinesMod.DEPOSITER_BLOCK_ENTITY, blockPos, blockState);
    }

    protected Text getContainerName() {
        return Text.translatable("container.dropper");
    }
}
