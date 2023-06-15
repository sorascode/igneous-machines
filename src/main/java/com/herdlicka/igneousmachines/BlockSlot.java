package com.herdlicka.igneousmachines;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class BlockSlot
        extends Slot {
    private final IgneousPlacerScreenHandler handler;

    public BlockSlot(IgneousPlacerScreenHandler handler, Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.handler = handler;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return this.handler.isBlock(stack);
    }
}
