package net.ultimporks.betterdiscs.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.ultimporks.betterdiscs.BetterMusicDiscs;
import net.ultimporks.betterdiscs.Reference;
import net.ultimporks.betterdiscs.init.ModMessages;
import net.ultimporks.betterdiscs.network.C2S.*;
import net.ultimporks.betterdiscs.util.menus.JukeblockMenu;
import org.jetbrains.annotations.NotNull;

public class JukeblockScreen extends AbstractContainerScreen<JukeblockMenu> {
    private static final ResourceLocation JUKEBOX_SCREEN = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "textures/gui/jukebox_gui.png");

    private int sliderX;
    private int sliderY;
    private final int sliderMinX = 42;
    private final int sliderMaxX = 132;
    private boolean dragging = false;

    private boolean isPlaying;
    private boolean isStopped;
    private boolean areParticlesEnabled;

    private int playLeftPos, playTopPos,
            stopLeftPos, stopTopPos,
            nextLeftPos, nextTopPos,
            rewindLeftPos, rewindTopPos,
            particlesLeftPos, particlesTopPos;

    // Button Widths
    private final int particlesButtonWidth = 12, particlesButtonHeight = 11,
            otherButtonWidth = 35, otherButtonHeight = 12;

    // GUI Button Offsets
    private final int playLeftOffset = 176, playTopOffset = 0,
            stopLeftOffset = 176, stopTopOffset = 50,
            particlesLeftOffset = 176, particlesTopOffset = 75,
            nextLeftOffset = 176, nextTopOffset = 88,
            rewindLeftOffset = 176, rewindTopOffset = 113;

    public JukeblockScreen(JukeblockMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
        int x = this.leftPos, y = this.topPos;
        // Play Button
        this.playLeftPos = x + 52;
        this.playTopPos = y + 48;
        // Stop Button
        this.stopLeftPos = x + 89;
        this.stopTopPos = y + 48;
        // Next Button
        this.nextLeftPos = x + 126;
        this.nextTopPos = y + 48;
        // Rewind Button
        this.rewindLeftPos = x + 15;
        this.rewindTopPos = y + 48;
        // Particles Button
        this.particlesLeftPos = x + 9;
        this.particlesTopPos = y + 63;
        // Volume Slider
        this.sliderX = getSliderXFromVolume(menu.getVolume());
        this.sliderY = this.topPos + 67;

    }
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(JUKEBOX_SCREEN, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        renderButtons(guiGraphics);
        renderVolumeSlider(guiGraphics, this.leftPos);
    }
    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.areParticlesEnabled = menu.areParticlesEnabled();
        this.isPlaying = menu.isPlayingMusic();
        this.isStopped = menu.isMusicStopped();

        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
        renderHoverText(guiGraphics, mouseX, mouseY);
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (handleButtonClick((int) mouseX, (int) mouseY)) {
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
            ModMessages.sendToServer(new C2SVolumeMessage(newVolume, menu.getJukeblockPos()));
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
        int sliderColor = getVolumeColor(getNewVolume());
        guiGraphics.fill(xPos + sliderX, sliderY, xPos + sliderX + 6, sliderY + 6, sliderColor);
    }
    private void renderButtons(GuiGraphics guiGraphics) {
        int x = this.leftPos, y = this.topPos;
        renderClickedButtons(guiGraphics, playLeftPos, playTopPos, playTopOffset,  isPlaying, "PlayButton");
        renderClickedButtons(guiGraphics, stopLeftPos, stopTopPos, stopTopOffset,  isStopped, "StopButton");
        renderClickedButtons(guiGraphics, particlesLeftPos, particlesTopPos, particlesTopOffset,  areParticlesEnabled, "ParticleButton");

        // Render Rewind and Next Buttons
        guiGraphics.blit(JUKEBOX_SCREEN, nextLeftPos, nextTopPos, nextLeftOffset, nextTopOffset, otherButtonWidth, otherButtonHeight);
        guiGraphics.blit(JUKEBOX_SCREEN, rewindLeftPos, rewindTopPos, rewindLeftOffset, rewindTopOffset, otherButtonWidth, otherButtonHeight);

    }
    private void renderClickedButtons(GuiGraphics guiGraphics, int xPosition, int yPosition, int yOffsetLocation, boolean active, String buttonName) {
        if (buttonName.equals("PlayButton")) {
            if (!active) {
                guiGraphics.blit(JUKEBOX_SCREEN, xPosition, yPosition, playLeftOffset, yOffsetLocation, otherButtonWidth, otherButtonHeight);
            } else {
                guiGraphics.blit(JUKEBOX_SCREEN, xPosition, yPosition, playLeftOffset, yOffsetLocation + 12, otherButtonWidth, otherButtonHeight);
            }
        }
        if (buttonName.equals("StopButton")) {
            if (!active) {
                guiGraphics.blit(JUKEBOX_SCREEN, xPosition, yPosition, stopLeftOffset, yOffsetLocation, otherButtonWidth, otherButtonHeight);
            } else {
                guiGraphics.blit(JUKEBOX_SCREEN, xPosition, yPosition, stopLeftOffset, yOffsetLocation + 12, otherButtonWidth, otherButtonHeight);
            }
        }
        if (buttonName.equals("ParticleButton")) {
            if (active) {
                guiGraphics.blit(JUKEBOX_SCREEN, xPosition, yPosition, particlesLeftOffset, yOffsetLocation, particlesButtonWidth, particlesButtonHeight);
            } else {
                guiGraphics.blit(JUKEBOX_SCREEN, xPosition, yPosition, 176 + 12, yOffsetLocation, particlesButtonWidth, particlesButtonHeight);
            }
        }
    }
    private void renderHoverText(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = this.leftPos, y = this.topPos;

        // Volume Slider
        if (mouseX >= this.leftPos + sliderX && mouseX <= this.leftPos + sliderX + 6 &&
                mouseY >= sliderY && mouseY <= sliderY + 6) {
            guiGraphics.drawString(minecraft.font, Component.literal("Volume: " + getNewVolume() + "%"), mouseX + 12, mouseY + 6, 0xFFFFFFFF);
        }
        // Particles Toggle
        if (isMouseOver(mouseX, mouseY, particlesLeftPos, particlesTopPos, particlesButtonWidth, particlesButtonHeight)) {
            if (areParticlesEnabled) {
                // Disable
                guiGraphics.drawString(minecraft.font, Component.literal("Disable Particles"), mouseX + 12, mouseY + 6, 0xFFFFFFFF);
            } else {
                // Enable
                guiGraphics.drawString(minecraft.font, Component.literal("Enable Particles"), mouseX + 12, mouseY + 6, 0xFFFFFFFF);
            }
        }
        // Play
        if (isMouseOver(mouseX, mouseY, playLeftPos, playTopPos, otherButtonWidth, otherButtonHeight)) {
            if (!isPlaying) {
                guiGraphics.drawString(minecraft.font, Component.literal("Play Music"), mouseX + 12, mouseY + 6, 0xFFFFFFFF);
            }
        }

        // Stop
        if (isMouseOver(mouseX, mouseY, stopLeftPos, stopTopPos, otherButtonWidth, otherButtonHeight)) {
            if (!isStopped) {
                guiGraphics.drawString(minecraft.font, Component.literal("Stop Music"), mouseX + 12, mouseY + 6, 0xFFFFFFFF);
            }
        }

        // Next
        if (isMouseOver(mouseX, mouseY, nextLeftPos, nextTopPos, otherButtonWidth, otherButtonHeight)) {
            guiGraphics.drawString(minecraft.font, Component.literal("Next Track"), mouseX + 12, mouseY + 6, 0xFFFFFFFF);
        }

        // Rewind
        if (isMouseOver(mouseX, mouseY, rewindLeftPos, rewindTopPos, otherButtonWidth, otherButtonHeight)) {
            guiGraphics.drawString(minecraft.font, Component.literal("Previous Track"), mouseX + 12, mouseY + 6, 0xFFFFFFFF);
        }
    }

    // Helper Methods
    private boolean handleButtonClick(int mouseX, int mouseY) {
        // Slider
        if (mouseX >= this.leftPos + sliderX && mouseX <= this.leftPos + sliderX + 6 &&
                mouseY >= sliderY && mouseY <= sliderY + 6) {
            dragging = true;
            return true;
        }

        // Particles
        if (isMouseOver(mouseX, mouseY, particlesLeftPos, particlesTopPos, particlesButtonWidth, particlesButtonHeight)) {
            if (areParticlesEnabled) {
                // Disable
                ModMessages.sendToServer(new C2SParticleMessage(false, menu.getJukeblockPos()));
            } else {
                // Enable
                ModMessages.sendToServer(new C2SParticleMessage(true, menu.getJukeblockPos()));
            }
            return true;
        }

        // Play
        if (isMouseOver(mouseX, mouseY, playLeftPos, playTopPos, otherButtonWidth, otherButtonHeight)) {
            if (!isPlaying) {
                ModMessages.sendToServer(new C2SButtonMessage(menu.getJukeblockPos(), "PlayButton"));
                BetterMusicDiscs.jukeblockLOGGING("Play Button Clicked!");
                return true;
            }
        }

        // Stop
        if (isMouseOver(mouseX, mouseY, stopLeftPos, stopTopPos, otherButtonWidth, otherButtonHeight)) {
            if (!isStopped) {
                ModMessages.sendToServer(new C2SButtonMessage(menu.getJukeblockPos(), "StopButton"));
                BetterMusicDiscs.jukeblockLOGGING("Stop Button Clicked!");
                return true;
            }
        }

        // Next
        if (isMouseOver(mouseX, mouseY, nextLeftPos, nextTopPos, otherButtonWidth, otherButtonHeight)) {
            ModMessages.sendToServer(new C2SButtonMessage(menu.getJukeblockPos(), "NextButton"));
            BetterMusicDiscs.jukeblockLOGGING("Next Track button clicked!");
            return true;
        }
        // Rewind
        if (isMouseOver(mouseX, mouseY, rewindLeftPos, rewindTopPos, otherButtonWidth, otherButtonHeight)) {
            ModMessages.sendToServer(new C2SButtonMessage(menu.getJukeblockPos(), "RewindButton"));
            BetterMusicDiscs.jukeblockLOGGING("Rewind Track button clicked!");
            return true;
        }

        return false;
    }
    private boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

}