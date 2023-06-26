package com.herdlicka.igneousmachines.gui.screen.ingame;

import com.herdlicka.igneousmachines.screen.IgneousCrafterScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;

@Environment(value = EnvType.CLIENT)
public class IgneousCrafterScreen extends HandledScreen<IgneousCrafterScreenHandler> implements RecipeBookProvider {
    private static final Identifier TEXTURE = new Identifier("igneous-machines", "textures/gui/container/igneous_crafter.png");
    private static final Identifier RECIPE_BUTTON_TEXTURE = new Identifier("textures/gui/recipe_button.png");
    private final RecipeBookWidget recipeBook = new RecipeBookWidget();
    private boolean narrow;

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
        int i = this.x;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
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
        this.renderBackground(context);
        if (this.recipeBook.isOpen() && this.narrow) {
            this.drawBackground(context, delta, mouseX, mouseY);
            this.recipeBook.render(context, mouseX, mouseY, delta);
        } else {
            this.recipeBook.render(context, mouseX, mouseY, delta);
            super.render(context, mouseX, mouseY, delta);
            this.recipeBook.drawGhostSlots(context, this.x, this.y, true, delta);
        }
        this.drawMouseoverTooltip(context, mouseX, mouseY);
        this.recipeBook.drawTooltip(context, this.x, this.y, mouseX, mouseY);
    }

    @Override
    protected boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
        return (!this.narrow || !this.recipeBook.isOpen()) && super.isPointWithinBounds(x, y, width, height, pointX, pointY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.recipeBook.mouseClicked(mouseX, mouseY, button)) {
            this.setFocused(this.recipeBook);
            return true;
        }
        if (this.narrow && this.recipeBook.isOpen()) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        boolean bl = mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
        return this.recipeBook.isClickOutsideBounds(mouseX, mouseY, this.x, this.y, this.backgroundWidth, this.backgroundHeight, button) && bl;
    }

    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        super.onMouseClick(slot, slotId, button, actionType);
        this.recipeBook.slotClicked(slot);
    }

    @Override
    public void refreshRecipeBook() {
        this.recipeBook.refresh();
    }

    @Override
    public RecipeBookWidget getRecipeBookWidget() {
        return this.recipeBook;
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        this.recipeBook.update();
    }

    @Override
    protected void init() {
        super.init();
        this.narrow = this.width < 379;
        this.recipeBook.initialize(this.width, this.height, this.client, this.narrow, this.handler);
        this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);

        int bookX = 136;
        int bookY = 47;

        this.addDrawableChild(new TexturedButtonWidget(this.x + bookX, this.height / 2 - bookY, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, button -> {
            this.recipeBook.toggleOpen();
            this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
            button.setPosition(this.x + bookX, this.height / 2 - bookY);
        }));
        this.addSelectableChild(this.recipeBook);
        this.setInitialFocus(this.recipeBook);
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }
}

