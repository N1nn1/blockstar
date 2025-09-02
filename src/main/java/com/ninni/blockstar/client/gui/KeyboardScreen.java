package com.ninni.blockstar.client.gui;

import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.client.config.KeyboardSettingsConfig;
import com.ninni.blockstar.client.sound.key.PianoKey;
import com.ninni.blockstar.server.inventory.KeyboardMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KeyboardScreen extends AbstractContainerScreen<KeyboardMenu> {
    public static final ResourceLocation TEXTURE_BG = new ResourceLocation(Blockstar.MODID, "textures/gui/keyboard/bg.png");
    public static final ResourceLocation TEXTURE_BG_SHEET_MUSIC = new ResourceLocation(Blockstar.MODID, "textures/gui/keyboard/bg_sheet_music.png");
    public static final ResourceLocation TEXTURE_PAUSE = new ResourceLocation(Blockstar.MODID, "textures/gui/keyboard/pause.png");
    public static final ResourceLocation TEXTURE_WIDGETS = new ResourceLocation(Blockstar.MODID, "textures/gui/keyboard/widgets.png");
    private final List<PianoKey> pianoKeys = new ArrayList<>();
    private int lastMouseKey;
    private boolean sustainPedalVisible;
    private boolean sustainPedalPressed;
    private static KeyboardScreen instance;
    private int octaveOffset = 0;

    public KeyboardScreen(KeyboardMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        instance = this;
        this.imageWidth = 183;
        this.imageHeight = 254;
        this.inventoryLabelY = 161;
        this.inventoryLabelX = 12;
        PianoKey.initPianoKeys(pianoKeys);
    }

    public static KeyboardScreen getInstance() {
        return instance;
    }

    @Override
    public void onClose() {
        super.onClose();
        instance = null;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        var sf = menu.getInstrumentType().getSoundfont(menu.getSoundfontSlot().getItem());
        var data = (sf != null) ? sf.getForInstrument(this.menu.getInstrumentType()) : null;
        sustainPedalVisible = data != null && data.held();
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
        if (!this.menu.getSheetMusicSlot().hasItem()) guiGraphics.blit(TEXTURE_WIDGETS, i + 160, j + 43, 48, 32, 16, 16);
        if (sustainPedalVisible) guiGraphics.blit(TEXTURE_WIDGETS, i + 84, j + 254, sustainPedalPressed ? 16 : 0, 32, 16, 16);

        for (PianoKey key : pianoKeys) {
            if (key.isBlack) guiGraphics.blit(TEXTURE_WIDGETS, i + key.x, j + key.y, key.isPressed ? 25 : 18, 0, key.width, 16);
            else guiGraphics.blit(TEXTURE_WIDGETS, i + key.x,j + key.y, key.isPressed ? 9 : 0, 0, key.width, 24);
        }

        int gearX = this.leftPos + 112;
        int gearY = this.topPos + 5;
        boolean hovered = mouseX >= gearX && mouseX < gearX + 10 && mouseY >= gearY && mouseY < gearY + 10;
        guiGraphics.blit(TEXTURE_WIDGETS, gearX, gearY, hovered ? 80 : 64, 32, 10, 10);
    }

    public void playNoteFromMidi(int note, int velocity) {
        for (PianoKey key : pianoKeys) {
            if (key.note == note) {
                if (velocity > 0 && !key.isPressed) {
                    key.press(menu, Optional.of(velocity));
                } else if (velocity == 0 && key.isPressed) {
                    key.release(menu, sustainPedalPressed);
                }
                break;
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == KeyboardSettingsConfig.pedalKey && !sustainPedalPressed && sustainPedalVisible) {
            sustainPedalPressed = true;
            return true;
        }

        if (keyCode == KeyboardSettingsConfig.octaveUpKey) {
            octaveOffset = Math.min(octaveOffset + 1, 3);
            return true;
        }
        if (keyCode == KeyboardSettingsConfig.octaveDownKey) {
            octaveOffset = Math.max(octaveOffset - 1, -3);
            return true;
        }

        if (KeyboardSettingsConfig.keyToNote.containsKey(keyCode)) {
            int note = KeyboardSettingsConfig.keyToNote.get(keyCode) + (octaveOffset * 12);
            for (PianoKey key : pianoKeys) {
                if (key.note == note && !key.isPressed) {
                    key.press(menu, Optional.empty());
                }
            }
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == KeyboardSettingsConfig.pedalKey && sustainPedalPressed) {
            sustainPedalPressed = false;
            for (PianoKey key : pianoKeys) {
                if (!key.isPressed) {
                    key.stopKeySound(menu, menu.getInstrumentType().getSoundfont(menu.getSoundfontSlot().getItem()));
                }
            }
            return true;
        }

        if (keyCode == KeyboardSettingsConfig.octaveUpKey || keyCode == KeyboardSettingsConfig.octaveDownKey) {
            octaveOffset = 0;
            return true;
        }

        if (KeyboardSettingsConfig.keyToNote.containsKey(keyCode)) {
            int note = KeyboardSettingsConfig.keyToNote.get(keyCode) + (octaveOffset * 12);
            for (PianoKey key : pianoKeys) {
                if (key.note == note) {
                    if (key.isPressed) {
                        key.release(menu, sustainPedalPressed);
                    }
                }
            }
            return true;
        }

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {

            int gearX = this.leftPos + 112;
            int gearY = this.topPos + 5;

            if (mouseX >= gearX && mouseX < gearX + 10 && mouseY >= gearY && mouseY < gearY + 10) {
                this.minecraft.player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.MASTER, 0.15F,1);
                Minecraft.getInstance().setScreen(new KeyboardKeybindConfigScreen(this, Minecraft.getInstance().options));
                return true;
            }

            pianoKeys.stream()
                    .sorted((a, b) -> Boolean.compare(b.isBlack, a.isBlack))
                    .filter(key -> key.isMouseHoveringOver(leftPos, topPos, mouseX, mouseY))
                    .findFirst()
                    .ifPresent(pianoKey -> {
                        if (!pianoKey.isPressed) {
                            lastMouseKey = pianoKey.note;
                            pianoKey.press(menu, Optional.empty());
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
                    if (key.isPressed) {
                        key.release(menu, sustainPedalPressed);
                    }
                    lastMouseKey = -1;
                }
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public void handleSustainPedalFromMidi(boolean pressed) {
        if (sustainPedalVisible) {
            if (sustainPedalPressed && !pressed) {
                for (PianoKey key : pianoKeys) {
                    if (!key.isPressed) {
                        key.stopKeySound(menu, menu.getInstrumentType().getSoundfont(menu.getSoundfontSlot().getItem()));
                    }
                }
            }
            sustainPedalPressed = pressed;
        }
    }
}
