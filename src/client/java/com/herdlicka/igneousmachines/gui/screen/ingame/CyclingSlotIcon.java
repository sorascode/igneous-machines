package com.herdlicka.igneousmachines.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

import java.util.List;

public class CyclingSlotIcon {
    private static final int field_42039 = 30;
    private static final int field_42040 = 16;
    private static final int field_42041 = 4;
    private final int slotId;
    private List<Identifier> textures = List.of();
    private int timer;
    private int currentIndex;

    public CyclingSlotIcon(int slotId) {
        this.slotId = slotId;
    }

    public void updateTexture(List<Identifier> textures) {
        if (!this.textures.equals(textures)) {
            this.textures = textures;
            this.currentIndex = 0;
        }

        if (!this.textures.isEmpty() && ++this.timer % 30 == 0) {
            this.currentIndex = (this.currentIndex + 1) % this.textures.size();
        }

    }

    public void render(ScreenHandler screenHandler, MatrixStack matrices, float delta, int x, int y) {
        Slot slot = screenHandler.getSlot(this.slotId);
        if (!this.textures.isEmpty() && !slot.hasStack()) {
            boolean bl = this.textures.size() > 1 && this.timer >= 30;
            float f = bl ? this.computeAlpha(delta) : 1.0F;
            if (f < 1.0F) {
                int i = Math.floorMod(this.currentIndex - 1, this.textures.size());
                this.drawIcon(slot, (Identifier)this.textures.get(i), 1.0F - f, matrices, x, y);
            }

            this.drawIcon(slot, (Identifier)this.textures.get(this.currentIndex), f, matrices, x, y);
        }
    }

    private void drawIcon(Slot slot, Identifier texture, float alpha, MatrixStack matrices, int x, int y) {
        RenderSystem.setShaderTexture(0, texture);
        drawSprite(matrices, x + slot.x, y + slot.y, 0, 16, 16, 1.0F, 1.0F, 1.0F, alpha);
    }


    public static void drawSprite(MatrixStack matrices, int x, int y, int z, int width, int height, float red, float green, float blue, float alpha) {
        drawTexturedQuad(matrices.peek().getPositionMatrix(), x, x + width, y, y + height, z, 0, 1, 0, 1, red, green, blue, alpha);
    }

    private static void drawTexturedQuad(Matrix4f matrix, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1, float red, float green, float blue, float alpha) {
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.enableBlend();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        bufferBuilder.vertex(matrix, (float)x0, (float)y0, (float)z).color(red, green, blue, alpha).texture(u0, v0).next();
        bufferBuilder.vertex(matrix, (float)x0, (float)y1, (float)z).color(red, green, blue, alpha).texture(u0, v1).next();
        bufferBuilder.vertex(matrix, (float)x1, (float)y1, (float)z).color(red, green, blue, alpha).texture(u1, v1).next();
        bufferBuilder.vertex(matrix, (float)x1, (float)y0, (float)z).color(red, green, blue, alpha).texture(u1, v0).next();
        BufferRenderer.drawWithShader(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    private float computeAlpha(float delta) {
        float f = (float)(this.timer % 30) + delta;
        return Math.min(f, 4.0F) / 4.0F;
    }
}
