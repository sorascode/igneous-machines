package com.herdlicka.igneousmachines.gui.screen.ingame;

import com.herdlicka.igneousmachines.screen.IgneousMinerScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.CyclingSlotIcon;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;


@Environment(value = EnvType.CLIENT)
public class IgneousMinerScreen extends HandledScreen<IgneousMinerScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("igneous-machines", "textures/gui/container/igneous_miner.png");
    private static final Identifier EMPTY_SLOT_HOE_TEXTURE = new Identifier("item/empty_slot_hoe");
    private static final Identifier EMPTY_SLOT_AXE_TEXTURE = new Identifier("item/empty_slot_axe");
    private static final Identifier EMPTY_SLOT_SHOVEL_TEXTURE = new Identifier("item/empty_slot_shovel");
    private static final Identifier EMPTY_SLOT_PICKAXE_TEXTURE = new Identifier("item/empty_slot_pickaxe");
    private static final List<Identifier> TOOL_SLOT_TEXTURES = List.of(EMPTY_SLOT_PICKAXE_TEXTURE, EMPTY_SLOT_AXE_TEXTURE, EMPTY_SLOT_SHOVEL_TEXTURE, EMPTY_SLOT_HOE_TEXTURE);

    private final CyclingSlotIcon templateSlotIcon = new CyclingSlotIcon(10);

    public IgneousMinerScreen(IgneousMinerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        this.templateSlotIcon.updateTexture(TOOL_SLOT_TEXTURES);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        if (this.handler.isBurning()) {
            int k = (this.handler).getFuelProgress();
            context.drawTexture(TEXTURE, x + 24, y + 24 + 12 - k, 176, 12 - k, 14, k + 1);
        }
        if (this.handler.hasBlock()) {
            int k = (this.handler).getBreakProgress();
            context.drawTexture(TEXTURE, x + 137, y + 23 + 15 - (15 - k), 176, 29 - (15 - k), 14, 15 - k);
        }
        this.templateSlotIcon.render(this.handler, context, delta, this.x, this.y);
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

