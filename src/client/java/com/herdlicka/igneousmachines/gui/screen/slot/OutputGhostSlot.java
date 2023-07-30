package com.herdlicka.igneousmachines.gui.screen.slot;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
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

    public void draw(MatrixStack matrices, MinecraftClient client, int x, int y) {
        if (this.result == null || this.result.isEmpty()) {
            return;
        }
        int j = this.x + x;
        int k = this.y + y;
        ItemRenderer itemRenderer = client.getItemRenderer();
        itemRenderer.renderInGui(matrices, result, j, k);
        RenderSystem.depthFunc(516);
        DrawableHelper.fill(matrices, j, k, j + 16, k + 16, 822083583);
        RenderSystem.depthFunc(515);
        itemRenderer.renderGuiItemOverlay(matrices, client.textRenderer, result, j, k);
    }

    public void drawTooltip(MatrixStack matrices, MinecraftClient client, int x, int y, int mouseX, int mouseY) {
        if (this.result == null || this.result.isEmpty()) {
            return;
        }
        int j = this.x + x;
        int k = this.y + y;
        if (mouseX >= j && mouseY >= k && mouseX < j + 16 && mouseY < k + 16) {
            if (result != null && client.currentScreen != null) {
                client.currentScreen.renderTooltip(matrices, client.currentScreen.getTooltipFromItem(result), mouseX, mouseY);
            }
        }
    }
}
