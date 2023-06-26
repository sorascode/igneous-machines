package com.herdlicka.igneousmachines.slot;

import com.herdlicka.igneousmachines.util.ItemStackUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class FuelSlot
        extends Slot {
    public FuelSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return ItemStackUtils.isFuel(stack) || ItemStackUtils.isBucket(stack);
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        return ItemStackUtils.isBucket(stack) ? 1 : super.getMaxItemCount(stack);
    }
}
