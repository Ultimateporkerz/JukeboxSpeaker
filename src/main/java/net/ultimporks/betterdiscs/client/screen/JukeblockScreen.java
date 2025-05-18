package net.ultimporks.betterdiscs.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.ultimporks.betterdiscs.BetterMusicDiscs;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.client.JukeblockSoundEvents;
import net.ultimporks.betterdiscs.init.ModMessages;
import net.ultimporks.betterdiscs.network.C2S.*;
import net.ultimporks.betterdiscs.util.menus.JukeboxMenu;
import org.jetbrains.annotations.NotNull;

public class JukeblockScreen extends AbstractContainerScreen<JukeboxMenu> {
    private static final ResourceLocation JUKEBOX_SCREEN = new ResourceLocation(Reference.MOD_ID, "textures/gui/jukebox_gui.png");

    private int sliderX;
    private int sliderY;
    private final int sliderMinX = 42;
    private final int sliderMaxX = 132;
    private boolean dragging = false;

    private boolean playing;
    private boolean stopped;
    private boolean particlesEnabled;

    public JukeblockScreen(JukeboxMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
        this.sliderX = getSliderXFromVolume(menu.getVolume());
        this.sliderY = this.topPos + 72;

    }
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(JUKEBOX_SCREEN, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        renderButtons(guiGraphics);
        renderVolumeSlider(guiGraphics, this.leftPos);
    }
    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.particlesEnabled = menu.areParticlesEnabled();
        this.playing = menu.isPlayingMusic();
        this.stopped = menu.isMusicStopped();

        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
        renderHoverText(guiGraphics, mouseX, mouseY);
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (handleButtonClick(mouseX, mouseY)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (dragging) {
            sliderX = (int) (pMouseX - this.leftPos);
            sliderX = Math.max(sliderMinX, Math.min(sliderX, sliderMaxX));

            int newVolume = getNewVolume();
            C2SSyncVolumeMessage message = new C2SSyncVolumeMessage(newVolume, menu.getJukeblockPos());
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
        int sliderY = topPos + 71; // Fixed Y position of the slider
        int sliderColor = getVolumeColor(getNewVolume());
        guiGraphics.fill(xPos + sliderX, sliderY, xPos + sliderX + 6, sliderY + 6, sliderColor);
    }
    private void renderButtons(GuiGraphics guiGraphics) {
        int x = this.leftPos, y = this.topPos;
        renderButton(guiGraphics, x + 42, y + 51, 0, 35, playing, "PlayButton");
        renderButton(guiGraphics, x + 97, y + 51, 50, 35, stopped, "StopButton");
        renderButton(guiGraphics, x + 9, y + 63, 75, 12, particlesEnabled, "ParticleButton");
    }
    private void renderButton(GuiGraphics guiGraphics, int xPosition, int yPosition, int yOffsetLocation, int buttonWidth, boolean active, String buttonName) {
        if (buttonName.equals("PlayButton")) {
            if (!active) {
                guiGraphics.blit(JUKEBOX_SCREEN, xPosition, yPosition, 176, yOffsetLocation, buttonWidth, 12);
            } else {
                guiGraphics.blit(JUKEBOX_SCREEN, xPosition, yPosition, 176, yOffsetLocation + 12, buttonWidth, 12);
            }
        }
        if (buttonName.equals("StopButton")) {
            if (!active) {
                guiGraphics.blit(JUKEBOX_SCREEN, xPosition, yPosition, 176, yOffsetLocation, buttonWidth, 12);
            } else {
                guiGraphics.blit(JUKEBOX_SCREEN, xPosition, yPosition, 176, yOffsetLocation + 12, buttonWidth, 12);
            }
        }
        if (buttonName.equals("ParticleButton")) {
            if (active) {
                guiGraphics.blit(JUKEBOX_SCREEN, xPosition, yPosition, 176, yOffsetLocation, buttonWidth, 12);
            } else {
                guiGraphics.blit(JUKEBOX_SCREEN, xPosition, yPosition, 176 + 12, yOffsetLocation, buttonWidth, 12);
            }
        }
    }
    private void renderHoverText(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = this.leftPos, y = this.topPos;

        // Volume Slider
        if (mouseX >= this.leftPos + sliderX && mouseX <= this.leftPos + sliderX + 6 &&
                mouseY >= sliderY && mouseY <= sliderY + 6) {
            guiGraphics.drawString(minecraft.font, Component.literal("Volume: " + getNewVolume() + "%"), mouseX + 8, mouseY, 0xFFFFFFFF);
        }
        // Particles Toggle
        if (isWithinBounds(mouseX, mouseY, x + 8, y + 64, 12, 11)) {
            if (particlesEnabled) {
                // Disable
                guiGraphics.drawString(minecraft.font, Component.literal("Disable Particles"), mouseX + 8, mouseY, 0xFFFFFFFF);
            } else {
                // Enable
                guiGraphics.drawString(minecraft.font, Component.literal("Enable Particles"), mouseX + 8, mouseY, 0xFFFFFFFF);
            }
        }
        // Play
        if (isWithinBounds(mouseX, mouseY, x + 42, y + 51, 35, 12)) {
            if (!playing) {
                guiGraphics.drawString(minecraft.font, Component.literal("Play Music"), mouseX + 8, mouseY, 0xFFFFFFFF);
            }
        }

        // Stop
        if (isWithinBounds(mouseX, mouseY, x + 97, y + 51, 35, 12)) {
            if (!stopped) {
                guiGraphics.drawString(minecraft.font, Component.literal("Stop Music"), mouseX + 8, mouseY, 0xFFFFFFFF);
            }
        }
    }

    // Helper Methods
    private boolean handleButtonClick(double mouseX, double mouseY) {
        int x = this.leftPos, y = this.topPos;

        // Slider
        if (mouseX >= this.leftPos + sliderX && mouseX <= this.leftPos + sliderX + 6 &&
                mouseY >= sliderY && mouseY <= sliderY + 6) {
            dragging = true;
            return true;
        }

        // Particles
        if (isWithinBounds(mouseX, mouseY, x + 8, y + 64, 12, 11)) {
            if (particlesEnabled) {
                // Disable
                ModMessages.sendToServer(new C2SSyncParticleMessage(false, menu.getJukeblockPos()));
            } else {
                // Enable
                ModMessages.sendToServer(new C2SSyncParticleMessage(true, menu.getJukeblockPos()));
            }
            return true;
        }

        // Play
        if (isWithinBounds(mouseX, mouseY, x + 42, y + 51, 35, 12)) {
            if (!playing) {
                ModMessages.sendToServer(new C2SSyncPlayButtonMessage(menu.getJukeblockPos()));
                return true;
            }
        }

        // Stop
        if (isWithinBounds(mouseX, mouseY, x + 97, y + 51, 35, 12)) {
            if (!stopped) {
                ModMessages.sendToServer(new C2SSyncStopButtonMessage(menu.getJukeblockPos()));
                return true;
            }
        }
        return false;
    }
    private boolean isWithinBounds(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

}