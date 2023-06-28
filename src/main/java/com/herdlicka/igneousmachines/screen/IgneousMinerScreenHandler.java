package com.herdlicka.igneousmachines.screen;

import com.herdlicka.igneousmachines.IgneousMachinesMod;
import com.herdlicka.igneousmachines.slot.FuelSlot;
import com.herdlicka.igneousmachines.slot.OutputSlot;
import com.herdlicka.igneousmachines.slot.ToolSlot;
import com.herdlicka.igneousmachines.util.ItemStackUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class IgneousMinerScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    private final PlayerEntity player;

    private final PropertyDelegate propertyDelegate;

    //This constructor gets called on the client when the server wants it to open the screenHandler,
    //The client will call the other constructor with an empty Inventory and the screenHandler will automatically
    //sync this empty inventory with the inventory on the server.
    public IgneousMinerScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(11), new ArrayPropertyDelegate(2));
    }

    //This constructor gets called from the BlockEntity on the server without calling the other constructor first, the server knows the inventory of the container
    //and can therefore directly provide it as an argument. This inventory will then be synced to the client.
    public IgneousMinerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(IgneousMachinesMod.IGNEOUS_MINER_SCREEN_HANDLER, syncId);
        checkSize(inventory, 11);
        this.inventory = inventory;
        this.player = playerInventory.player;

        this.propertyDelegate = propertyDelegate;

        inventory.onOpen(playerInventory.player);

        int m;
        int l;
        //Our inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 3; ++l) {
                this.addSlot(new OutputSlot(inventory, l + m * 3, 62 + l * 18, 17 + m * 18));
            }
        }
        this.addSlot(new FuelSlot(inventory, 9, 24, 41));
        this.addSlot(new ToolSlot(inventory, 10, 136, 41));

        //The player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        //The player Hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }

        this.addProperties(propertyDelegate);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    // Shift + Player Inv Slot
    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (ItemStackUtils.isTool(originalStack)) {
                    if (!this.insertItem(originalStack, 10, 11, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (ItemStackUtils.isFuel(originalStack)) {
                    if (!this.insertItem(originalStack, 9, 10, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    public int getFuelProgress() {
        int i = this.propertyDelegate.get(1);
        if (i == 0) {
            i = 200;
        }
        return this.propertyDelegate.get(0) * 13 / i;
    }

    public boolean isBurning() {
        return this.propertyDelegate.get(0) > 0;
    }
}

