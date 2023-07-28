package com.herdlicka.igneousmachines.util;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MiningToolItem;

public class ItemStackUtils {
    public static boolean isFuel(ItemStack itemStack) {
        return AbstractFurnaceBlockEntity.canUseAsFuel(itemStack);
    }

    public static boolean isBlock(ItemStack itemStack) {
        return itemStack.getItem() instanceof BlockItem;
    }

    public static boolean isBucket(ItemStack stack) {
        return stack.isOf(Items.BUCKET);
    }

    public static boolean isTool(ItemStack stack) {
        return stack.getItem() instanceof MiningToolItem;
    }
}
