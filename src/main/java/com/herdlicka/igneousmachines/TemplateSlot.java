package com.herdlicka.igneousmachines;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class TemplateSlot
        extends Slot {
    public TemplateSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        return 1;
    }

    @Override
    public ItemStack insertStack(ItemStack stack, int count) {
        if (stack.isEmpty() || !this.canInsert(stack)) {
            return stack;
        }
        this.setStack(stack.copyWithCount(1));
        return stack;
    }

    @Override
    public ItemStack takeStack(int amount) {
        super.takeStack(amount);
        return ItemStack.EMPTY;
    }
}
