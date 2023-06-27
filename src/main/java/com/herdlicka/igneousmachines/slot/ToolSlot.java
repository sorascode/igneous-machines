package com.herdlicka.igneousmachines.slot;

import com.herdlicka.igneousmachines.util.ItemStackUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class ToolSlot
        extends Slot {
    public ToolSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return ItemStackUtils.isTool(stack);
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        return 1;
    }
}
