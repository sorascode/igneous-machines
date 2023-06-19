package com.herdlicka.igneousmachines;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;

@Environment(value = EnvType.CLIENT)
public class IgneousCrafterScreen extends HandledScreen<IgneousCrafterScreenHandler> {
    //A path to the gui texture. In this example we use the texture from the dispenser
    private static final Identifier TEXTURE = new Identifier("igneous-machines", "textures/gui/container/igneous_crafter.png");

    protected int blockInventoryTitleX;
    protected int blockInventoryTitleY;
    protected Text blockInventoryTitle;

    public IgneousCrafterScreen(IgneousCrafterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);

        backgroundHeight = 221;

        playerInventoryTitleY = backgroundHeight - 94;

        blockInventoryTitleX = 8;
        blockInventoryTitleY = 77;
        blockInventoryTitle = Text.translatable(((TranslatableTextContent) title.getContent()).getKey() + ".inventory");
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
        if (this.handler.isBurning()) {
            int k = (this.handler).getFuelProgress();
            context.drawTexture(TEXTURE, x + 13, y + 24 + 12 - k, 176, 12 - k, 14, k + 1);
        }
        int k = (this.handler).getCraftProgress();
        context.drawTexture(TEXTURE, x + 103, y + 34, 176, 14, k + 1, 16);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        context.drawText(this.textRenderer, this.blockInventoryTitle, this.blockInventoryTitleX, this.blockInventoryTitleY, 0x404040, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }
}

