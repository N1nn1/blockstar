package com.ninni.blockstar.client.gui;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.client.sound.key.PianoKey;
import com.ninni.blockstar.server.inventory.KeyboardMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class KeyboardScreen extends AbstractContainerScreen<KeyboardMenu> {
    public static final ResourceLocation TEXTURE_BG = new ResourceLocation(Blockstar.MODID, "textures/gui/keyboard/bg.png");
    public static final ResourceLocation TEXTURE_BG_SHEET_MUSIC = new ResourceLocation(Blockstar.MODID, "textures/gui/keyboard/bg_sheet_music.png");
    public static final ResourceLocation TEXTURE_PAUSE = new ResourceLocation(Blockstar.MODID, "textures/gui/keyboard/pause.png");
    public static final ResourceLocation TEXTURE_WIDGETS = new ResourceLocation(Blockstar.MODID, "textures/gui/keyboard/widgets.png");
    private final List<PianoKey> pianoKeys = new ArrayList<>();
    private int lastMouseKey;
    private boolean sustainPedalPressed;

    public KeyboardScreen(KeyboardMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageWidth = 183;
        this.imageHeight = 254;
        this.inventoryLabelY = 161;
        this.inventoryLabelX = 12;
        PianoKey.initPianoKeys(pianoKeys);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(guiGraphics);
        int i = this.leftPos;
        int j = this.topPos;

        if (this.menu.getSheetMusicSlot().hasItem()) guiGraphics.blit(TEXTURE_BG_SHEET_MUSIC, i, j, 0, 0, this.imageWidth, this.imageHeight);
        else guiGraphics.blit(TEXTURE_BG, i, j, 0, 0, this.imageWidth, this.imageHeight);

        if (!this.menu.getSoundfontSlot().hasItem()) guiGraphics.blit(TEXTURE_WIDGETS, i + 7, j + 43, 32, 32, 16, 16);
        if (!this.menu.getSheetMusicSlot().hasItem()) {
            guiGraphics.blit(TEXTURE_WIDGETS, i + 160, j + 43, 48, 32, 16, 16);
            guiGraphics.blit(TEXTURE_WIDGETS, i + 84, j + 254, sustainPedalPressed ? 16 : 0, 32, 16, 16);
        }

        for (PianoKey key : pianoKeys) {
            if (key.isBlack) guiGraphics.blit(TEXTURE_WIDGETS, i + key.x, j + key.y, key.isPressed ? 25 : 18, 0, key.width, 16);
            else guiGraphics.blit(TEXTURE_WIDGETS, i + key.x,j + key.y, key.isPressed ? 9 : 0, 0, key.width, 24);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_SPACE && !sustainPedalPressed) {
            sustainPedalPressed = true;
            return true;
        }

        if (PianoKey.KEY_TO_NOTE.containsKey(keyCode)) {
            int note = PianoKey.KEY_TO_NOTE.get(keyCode);
            for (PianoKey key : pianoKeys) {
                if (key.note == note && !key.isPressed) {
                    key.handleKeyPress(menu, sustainPedalPressed);
                }
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_SPACE && sustainPedalPressed) {
            sustainPedalPressed = false;
            for (PianoKey key : pianoKeys) {
                if (!key.isPressed) {
                    key.stopKeySound(menu.getInstrumentType().getSoundfont(menu.getSoundfontSlot().getItem()), key.note);
                }
            }
            return true;
        }

        if (PianoKey.KEY_TO_NOTE.containsKey(keyCode)) {
            int note = PianoKey.KEY_TO_NOTE.get(keyCode);
            for (PianoKey key : pianoKeys) {
                if (key.note == note) {
                    key.handleKeyPress(menu, sustainPedalPressed);
                }
            }
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            pianoKeys.stream()
                    .sorted((a, b) -> Boolean.compare(b.isBlack, a.isBlack))
                    .filter(key -> key.isMouseHoveringOver(leftPos, topPos, mouseX, mouseY))
                    .findFirst()
                    .ifPresent(pianoKey -> {
                        if (!pianoKey.isPressed) {
                            lastMouseKey = pianoKey.note;
                            pianoKey.handleKeyPress(menu, sustainPedalPressed);
                        }
                    });
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (PianoKey key : pianoKeys) {
                if (key.note == lastMouseKey) {
                    key.handleKeyPress(menu, sustainPedalPressed);
                    lastMouseKey = -1;
                }
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
