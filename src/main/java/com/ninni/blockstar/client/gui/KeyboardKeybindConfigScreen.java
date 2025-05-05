package com.ninni.blockstar.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.ninni.blockstar.Blockstar;
import com.ninni.blockstar.client.config.KeyboardSettingsConfig;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KeyboardKeybindConfigScreen extends OptionsSubScreen {
    private int awaitingNote = -1;
    private boolean awaitingFunction = false;
    private final ResourceLocation MAPPINGS_TEXTURE = new ResourceLocation(Blockstar.MODID, "textures/gui/keyboard/mappings.png");
    private String awaitingLabel = "";
    private static final List<GuiPianoKey> GUI_KEYS = new ArrayList<>();
    public record GuiPianoKey(int note, int x, int y, int width, int height, boolean isBlack) {}

    public KeyboardKeybindConfigScreen(Screen lastScreen, Options options) {
        super(lastScreen, options, Component.translatable("blockstar.options.keyboard.title"));
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> {
            KeyboardSettingsConfig.save();
            this.minecraft.setScreen(this.lastScreen);
        }).bounds(this.width / 2 - 100, this.height - 30, 200, 20).build());

        int y = 40;

        Button octaveUpButton = Button.builder(Component.translatable("blockstar.options.keyboard.octave_up", getKeyName(KeyboardSettingsConfig.octaveUpKey)), b -> {
            awaitingFunction = true;
            awaitingLabel = "OCTAVE_UP";
        }).bounds(this.width / 2 - 100, y, 200, 20).build();
        this.addRenderableWidget(octaveUpButton);

        Button octaveDownButton = Button.builder(Component.translatable("blockstar.options.keyboard.octave_down", getKeyName(KeyboardSettingsConfig.octaveDownKey)), b -> {
            awaitingFunction = true;
            awaitingLabel = "OCTAVE_DOWN";
        }).bounds(this.width / 2 - 100, y + 25, 200, 20).build();
        this.addRenderableWidget(octaveDownButton);

        Button sustainPedalButton = Button.builder(Component.translatable("blockstar.options.keyboard.sustain_pedal", getKeyName(KeyboardSettingsConfig.pedalKey)), b -> {
            awaitingFunction = true;
            awaitingLabel = "PEDAL";
        }).bounds(this.width / 2 - 100, y + 50, 200, 20).build();
        this.addRenderableWidget(sustainPedalButton);


        this.initPianoKeys();
    }

    public void initPianoKeys() {
        GUI_KEYS.clear();

        int baseX = (this.width - 368) / 2;
        int baseY = 129;
        int startingNote = 48; // C3
        int maxNote = 83; // B5
        int whiteKeyWidth = 17;
        int whiteKeyHeight = 44;
        int blackKeyWidth = 13;
        int blackKeyHeight = 29;
        int whiteKeyX = 0;

        int[] whiteNoteOffsets = {0, 2, 4, 5, 7, 9, 11};

        Map<Integer, Integer> blackNoteOffsets = Map.of(
                0, 1,  // C#
                1, 3,  // D#
                3, 6,  // F#
                4, 8,  // G#
                5, 10  // A#
        );


        for (int midiNote = startingNote; midiNote <= maxNote; ) {
            int octaveOffset = (midiNote - startingNote) / 12;

            for (int whiteNoteOffset : whiteNoteOffsets) {
                int note = startingNote + octaveOffset * 12 + whiteNoteOffset;
                if (note > maxNote) break;
                GUI_KEYS.add(new GuiPianoKey(note, baseX + whiteKeyX, baseY, whiteKeyWidth, whiteKeyHeight, false));
                whiteKeyX += whiteKeyWidth;
                midiNote = note + 1;
            }
        }


        whiteKeyX = 0;

        for (int midiNote = startingNote; midiNote <= maxNote; ) {
            int octaveOffset = (midiNote - startingNote) / 12;

            for (int i = 0; i < whiteNoteOffsets.length; i++) {
                int whiteNote = startingNote + octaveOffset * 12 + whiteNoteOffsets[i];
                if (whiteNote > maxNote) break;

                if (blackNoteOffsets.containsKey(i)) {
                    int blackNote = whiteNote + 1;
                    if (blackNote <= maxNote) {
                        int x = baseX + whiteKeyX + 11;
                        GUI_KEYS.add(new GuiPianoKey(blackNote, x, baseY - 4, blackKeyWidth, blackKeyHeight, true));
                    }
                }

                whiteKeyX += whiteKeyWidth;
                midiNote = whiteNote + 1;
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTicks);

        int pianoX = (this.width - 368) / 2;
        int pianoY = 120;


        GuiPianoKey hoveredKey = null;
        for (GuiPianoKey key : GUI_KEYS) {
            if (mouseX >= key.x && mouseX < key.x + key.width && mouseY >= key.y && mouseY < key.y + key.height) {
                if (hoveredKey == null || key.isBlack()) {
                    hoveredKey = key;
                    if (key.isBlack()) break;
                }
            }
        }

        for (GuiPianoKey key : GUI_KEYS) {
            if (!key.isBlack) {
                graphics.blit(MAPPINGS_TEXTURE, key.x, key.y, 0, 0, 18, 44, 64, 64);
            } else {
                graphics.blit(MAPPINGS_TEXTURE, key.x, key.y, 18, 0, 13, 29, 64, 64);
            }

            drawKeyLabel(graphics, key, 0, 0);

            int keyWidth = key.x + key.width - (key.isBlack ? 1 : 0);
            if (key.note == awaitingNote) {
                graphics.fill(key.x + 1, key.y + 1, keyWidth, key.y + key.height - 1, 0x6088ffff);
            } else if (key == hoveredKey) {
                graphics.fill(key.x + 1, key.y + 1, keyWidth, key.y + key.height - 1, 0x605b60f4);
            }
        }
    }

    private String getKeyName(int keyCode) {
        String glfwName = GLFW.glfwGetKeyName(keyCode, 0);
        if (glfwName != null) return glfwName.toUpperCase();
        InputConstants.Key key = InputConstants.getKey(keyCode, 0);
        return Component.translatable(key.getName()).getString();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (GuiPianoKey key : GUI_KEYS) {
            if (!key.isBlack()) continue;
            if (mouseX >= key.x && mouseX < key.x + key.width && mouseY >= key.y && mouseY < key.y + key.height) {
                awaitingNote = key.note();
                this.minecraft.player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.MASTER, 0.3F, 1);
                return true;
            }
        }

        for (GuiPianoKey key : GUI_KEYS) {
            if (key.isBlack()) continue;
            if (mouseX >= key.x && mouseX < key.x + key.width && mouseY >= key.y && mouseY < key.y + key.height) {
                awaitingNote = key.note();
                this.minecraft.player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.MASTER, 0.3F, 1);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void drawKeyLabel(GuiGraphics graphics, GuiPianoKey key, int baseX, int baseY) {
        int keyCode = getKeyForNote(key.note());
        if (keyCode == -1) return;

        String label = getKeyName(keyCode);
        if (label == null || label.isBlank()) return;

        int textX = key.x() + (key.isBlack() ? 4 : 6);
        int textY = key.isBlack() ? 138 : 156;

        graphics.drawString(this.font, label, textX, textY, key.isBlack() ? 0xe6e3dc : 0x5e6d9a, false);
    }

    private int getKeyForNote(int note) {
        return KeyboardSettingsConfig.keyToNote.entrySet().stream().filter(e -> e.getValue() == note).map(Map.Entry::getKey).findFirst().orElse(-1);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (awaitingFunction) {
            switch (awaitingLabel) {
                case "OCTAVE_UP" -> KeyboardSettingsConfig.octaveUpKey = keyCode;
                case "OCTAVE_DOWN" -> KeyboardSettingsConfig.octaveDownKey = keyCode;
                case "PEDAL" -> KeyboardSettingsConfig.pedalKey = keyCode;
            }
            awaitingFunction = false;
            this.init();
            this.minecraft.player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.MASTER, 0.3F, 0.75F);
            return true;
        } else if (awaitingNote != -1) {
            KeyboardSettingsConfig.keyToNote.entrySet().removeIf(e -> e.getValue() == awaitingNote);
            KeyboardSettingsConfig.keyToNote.put(keyCode, awaitingNote);
            awaitingNote = -1;
            this.minecraft.player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.MASTER, 0.3F, 0.75F);
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        KeyboardSettingsConfig.save();
        super.onClose();
    }
}