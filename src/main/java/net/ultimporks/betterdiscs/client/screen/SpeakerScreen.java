package net.ultimporks.betterdiscs.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.init.ModMessages;
import net.ultimporks.betterdiscs.network.C2S.C2SSyncVolumeMessage;
import net.ultimporks.betterdiscs.network.C2S.C2SSyncParticleMessage;
import net.ultimporks.betterdiscs.util.menus.SpeakerMenus;

@OnlyIn(Dist.CLIENT)
public class SpeakerScreen extends AbstractContainerScreen<SpeakerMenus> {
    private static final ResourceLocation SPEAKER_SCREEN = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "textures/gui/speakers_gui.png");

    private int sliderX;
    private int sliderY;
    private boolean particlesEnabled;
    private final int sliderMinX = 42;
    private final int sliderMaxX = 132;
    private boolean dragging = false;

    public SpeakerScreen(SpeakerMenus pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageHeight = 117;
        this.imageWidth = 176;
    }
    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
        this.sliderX = getSliderXFromVolume(menu.getVolume());
        this.sliderY = this.topPos + 16;
    }
    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, SPEAKER_SCREEN);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        pGuiGraphics.blit(SPEAKER_SCREEN, x, y, 0, 0, imageWidth, imageHeight);
        renderButtons(pGuiGraphics);
        renderVolumeSlider(pGuiGraphics, leftPos);
    }
    @Override
    public void render(GuiGraphics pGuiGraphics, int mouseX, int mouseY, float pPartialTick) {
        this.particlesEnabled = menu.areParticlesEnabled();
        super.render(pGuiGraphics, mouseX, mouseY, pPartialTick);
        renderTooltip(pGuiGraphics, mouseX, mouseY);
        renderHoverText(pGuiGraphics, mouseX, mouseY);
    }
    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (handleButtonClick(pMouseX, pMouseY)) {
            return true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }
    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (dragging) {
            sliderX = (int) (pMouseX - this.leftPos); // Move slider based on mouse X
            sliderX = Math.max(sliderMinX, Math.min(sliderX, sliderMaxX)); // Clamp within bounds

            int newVolume = getNewVolume();
            C2SSyncVolumeMessage message = new C2SSyncVolumeMessage(newVolume, menu.getSpeakerPos());
            ModMessages.sendToServer(message);
            return true;
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }
    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        dragging = false;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    // Volume
    private int getNewVolume() {
        return (int) (((sliderX - sliderMinX) / (float) (sliderMaxX - sliderMinX)) * 100);
    }
    private int getSliderXFromVolume(int volume) {
        return sliderMinX + (int) ((volume / 100.0) * (sliderMaxX - sliderMinX));
    }
    private int getVolumeColor(int volume) {
        if (volume <= 33) {
            // Low volume (red)
            return 0xFFFF0000; // Red
        } else if (volume <= 66) {
            // Medium volume (yellow)
            return 0xFFFFFF00; // Yellow
        } else {
            // High volume (green)
            return 0xFF00FF00; // Green
        }
    }

    // Renderers
    private void renderVolumeSlider(GuiGraphics guiGraphics, int xPos) {
        int sliderY = topPos + 16; // Fixed Y position of the slider
        int sliderColor = getVolumeColor(getNewVolume());
        guiGraphics.fill(xPos + sliderX, sliderY, xPos + sliderX + 6, sliderY + 6, sliderColor);
    }
    private void renderButtons(GuiGraphics guiGraphics) {
        if (particlesEnabled) {
            // Enabled
            guiGraphics.blit(SPEAKER_SCREEN, leftPos + 17, topPos + 12, 176, 0, 12, 12);
        } else {
            // Disabled
            guiGraphics.blit(SPEAKER_SCREEN, leftPos + 17, topPos + 12, 188, 0 , 12, 12);
        }
    }
    private void renderHoverText(GuiGraphics guiGraphics, int mouseX, int mouseY) {

        // Volume Slider
        if (mouseX >= this.leftPos + sliderX && mouseX <= this.leftPos + sliderX + 6 &&
                mouseY >= sliderY && mouseY <= sliderY + 6) {
            guiGraphics.drawString(minecraft.font, Component.literal("Volume: " + getNewVolume() + "%"), mouseX + 8, mouseY + 2, 0xFFFFFFFF);
        }

        // Particles Toggle
        if (isWithinBounds(mouseX, mouseY, this.leftPos + 17, this.topPos + 12, 12, 12)) {
            if (particlesEnabled) {
                // Disable
                guiGraphics.drawString(minecraft.font, Component.literal("Disable Particles"), mouseX + 8, mouseY + 2, 0xFFFFFFFF);
            } else {
                // Enable
                guiGraphics.drawString(minecraft.font, Component.literal("Enable Particles"), mouseX + 8, mouseY + 2, 0xFFFFFFFF);
            }
        }
    }

    // Helper Methods
    private boolean handleButtonClick(double mouseX, double mouseY)  {
        // Slider
        if (mouseX >= this.leftPos + sliderX && mouseX <= this.leftPos + sliderX + 6 &&
                mouseY >= sliderY && mouseY <= sliderY + 6) {
            dragging = true;
            return true;
        }

        // Particles
        if (isWithinBounds(mouseX, mouseY, this.leftPos + 17, this.topPos + 12, 12, 12)) {
            if (particlesEnabled) {
                // Disable
                ModMessages.sendToServer(new C2SSyncParticleMessage(false, menu.getSpeakerPos()));
            } else {
                // Enable
                ModMessages.sendToServer(new C2SSyncParticleMessage(true, menu.getSpeakerPos()));
            }
            return true;
        }
        return false;

    }
    private boolean isWithinBounds(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }


}
