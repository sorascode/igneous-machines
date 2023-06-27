package com.herdlicka.igneousmachines.util;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

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
        return stack.isOf(Items.WOODEN_PICKAXE) ||
                stack.isOf(Items.STONE_PICKAXE) ||
                stack.isOf(Items.IRON_PICKAXE) ||
                stack.isOf(Items.GOLDEN_PICKAXE) ||
                stack.isOf(Items.DIAMOND_PICKAXE) ||
                stack.isOf(Items.NETHERITE_PICKAXE) ||
                stack.isOf(Items.WOODEN_AXE) ||
                stack.isOf(Items.STONE_AXE) ||
                stack.isOf(Items.IRON_AXE) ||
                stack.isOf(Items.GOLDEN_AXE) ||
                stack.isOf(Items.DIAMOND_AXE) ||
                stack.isOf(Items.NETHERITE_AXE) ||
                stack.isOf(Items.WOODEN_SHOVEL) ||
                stack.isOf(Items.STONE_SHOVEL) ||
                stack.isOf(Items.IRON_SHOVEL) ||
                stack.isOf(Items.GOLDEN_SHOVEL) ||
                stack.isOf(Items.DIAMOND_SHOVEL) ||
                stack.isOf(Items.NETHERITE_SHOVEL) ||
                stack.isOf(Items.WOODEN_HOE) ||
                stack.isOf(Items.STONE_HOE) ||
                stack.isOf(Items.IRON_HOE) ||
                stack.isOf(Items.GOLDEN_HOE) ||
                stack.isOf(Items.DIAMOND_HOE) ||
                stack.isOf(Items.NETHERITE_HOE);
    }
}
