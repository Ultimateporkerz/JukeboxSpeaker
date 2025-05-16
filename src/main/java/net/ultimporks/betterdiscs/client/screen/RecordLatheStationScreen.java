package net.ultimporks.betterdiscs.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.recipe.RecordLatheRecipe;
import net.ultimporks.betterdiscs.util.menus.RecordLatheStationMenu;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RecordLatheStationScreen extends AbstractContainerScreen<RecordLatheStationMenu> {
    private static final ResourceLocation CRAFT_VANILLA = new ResourceLocation(Reference.MOD_ID, "textures/gui/record_lathe_station_gui.png");

    private float scrollOffs;
    private boolean scrolling;
    private int startIndex;
    private boolean displayRecipes;

    public RecordLatheStationScreen(RecordLatheStationMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int leftPos = this.leftPos;
        int topPos = this.topPos;
        // Scrolling
        int scrollBar = (int)(41.0F * this.scrollOffs);
        int leftPos1 = this.leftPos + 52;
        int topPos1 = this.topPos + 14;
        int startIndex = this.startIndex + 12;
        // Render the main vanilla crafting screen
        guiGraphics.blit(CRAFT_VANILLA, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
        guiGraphics.blit(CRAFT_VANILLA, leftPos + 119, topPos + 15 + scrollBar, 176 + (this.isScrollBarActive() ? 0 : 12), 0, 12, 15);
        // Only render outputs if input has ingredient
        if (menu.inputHaveIngredient()) {
            this.renderButtons(guiGraphics, pMouseX, pMouseY, leftPos1, topPos1, startIndex);
            this.renderRecipes(guiGraphics, leftPos1, topPos1, startIndex);
            displayRecipes = true;
        } else {
            displayRecipes = false;
        }
        // Render progress Arrow if in use
        this.renderProgressArrow(guiGraphics, leftPos, topPos);
    }

    private void renderButtons(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, int pX, int pY, int pLastVisibleElementIndex) {
        for(int i = this.startIndex; i < pLastVisibleElementIndex && i < this.menu.getNumRecipes(); ++i) {
            int j = i - this.startIndex;
            int k = pX + j % 4 * 16;
            int l = j / 4;
            int i1 = pY + l * 18 + 2;
            int j1 = this.imageHeight;
            if (i == this.menu.getSelectedRecipeIndex()) {
                j1 += 18;
            } else if (pMouseX >= k && pMouseY >= i1 && pMouseX < k + 16 && pMouseY < i1 + 18) {
                j1 += 36;
            }

            pGuiGraphics.blit(CRAFT_VANILLA, k, i1 - 1, 0, j1, 16, 18);
        }

    }

    private void renderRecipes(GuiGraphics pGuiGraphics, int pX, int pY, int pStartIndex) {
        List<RecordLatheRecipe> list = this.menu.getRecipes();
        for(int i = this.startIndex; i < pStartIndex && i < menu.getNumRecipes(); ++i) {
            int j = i - this.startIndex;
            int k = pX + j % 4 * 16;
            int l = j / 4;
            int i1 = pY + l * 18 + 2;
            pGuiGraphics.renderItem(list.get(i).getResultItem(this.minecraft.level.registryAccess()), k, i1);
        }
    }
    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            guiGraphics.blit(CRAFT_VANILLA, x + 76, y + 73, 176, 17, menu.getScaledProgress(), 8);
        }
    }
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.renderTooltip(pGuiGraphics, pX, pY);
        if (this.displayRecipes) {
            int i = this.leftPos + 52;
            int j = this.topPos + 14;
            int k = this.startIndex + 12;
            List<RecordLatheRecipe> list = this.menu.getRecipes();

            for(int l = this.startIndex; l < k && l < this.menu.getNumRecipes(); ++l) {
                int i1 = l - this.startIndex;
                int j1 = i + i1 % 4 * 16;
                int k1 = j + i1 / 4 * 18 + 2;
                if (pX >= j1 && pX < j1 + 16 && pY >= k1 && pY < k1 + 18) {
                    pGuiGraphics.renderTooltip(this.font, list.get(l).getResultItem(this.minecraft.level.registryAccess()), pX, pY);
                }
            }
        }

    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        this.scrolling = false;
        if (this.displayRecipes) {
            int i = this.leftPos + 52;
            int j = this.topPos + 14;
            int k = this.startIndex + 12;

            for(int l = this.startIndex; l < k; ++l) {
                int i1 = l - this.startIndex;
                double d0 = pMouseX - (double)(i + i1 % 4 * 16);
                double d1 = pMouseY - (double)(j + i1 / 4 * 18);
                if (d0 >= 0.0D && d1 >= 0.0D && d0 < 16.0D && d1 < 18.0D && this.menu.clickMenuButton(this.minecraft.player, l)) {
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                    this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, l);
                    return true;
                }
            }

            i = this.leftPos + 119;
            j = this.topPos + 9;
            if (pMouseX >= (double)i && pMouseX < (double)(i + 12) && pMouseY >= (double)j && pMouseY < (double)(j + 54)) {
                this.scrolling = true;
            }
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.scrolling && this.isScrollBarActive()) {
            int i = this.topPos + 14;
            int j = i + 54;
            this.scrollOffs = ((float)pMouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
            this.startIndex = (int)((double)(this.scrollOffs * (float)this.getOffScreenRows()) + 0.5D) * 4;
            return true;
        } else {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
    }
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (this.isScrollBarActive()) {
            int i = this.getOffScreenRows();
            float f = (float)pDelta / (float)i;
            this.scrollOffs = Mth.clamp(this.scrollOffs - f, 0.0F, 1.0F);
            this.startIndex = (int)((double)(this.scrollOffs * (float)i) + 0.5D) * 4;
        }

        return true;
    }

    private boolean isScrollBarActive() {
        return this.displayRecipes && this.menu.getNumRecipes() > 12;
    }
    protected int getOffScreenRows() {
        return (this.menu.getNumRecipes() + 4 - 1) / 4 - 3;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
