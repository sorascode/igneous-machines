package com.herdlicka.igneousmachines.gui.screen.slot;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;

public class OutputGhostSlot {

    private ItemStack result = ItemStack.EMPTY;

    private int x;
    private int y;

    public void reset() {
        this.result = ItemStack.EMPTY;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(DrawContext context, MinecraftClient client, int x, int y) {
        if (this.result == null || this.result.isEmpty()) {
            return;
        }
        int j = this.x + x;
        int k = this.y + y;
        context.drawItemWithoutEntity(result, j, k);
        context.fill(RenderLayer.getGuiGhostRecipeOverlay(), j, k, j + 16, k + 16, 0x30FFFFFF);
        context.drawItemInSlot(client.textRenderer, result, j, k);
    }

    public void drawTooltip(DrawContext context, MinecraftClient client, int x, int y, int mouseX, int mouseY) {
        if (this.result == null || this.result.isEmpty()) {
            return;
        }
        int j = this.x + x;
        int k = this.y + y;
        if (mouseX >= j && mouseY >= k && mouseX < j + 16 && mouseY < k + 16) {
            if (result != null && client.currentScreen != null) {
                context.drawTooltip(client.textRenderer, Screen.getTooltipFromItem(client, result), mouseX, mouseY);
            }
        }
    }
}
