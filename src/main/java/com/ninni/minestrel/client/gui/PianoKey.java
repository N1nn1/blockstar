package com.ninni.minestrel.client.gui;


import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Map;

public class PianoKey {
    public final int note;
    public final int x, y, width, height;
    public final boolean isBlack;
    public boolean isPressed = false;
    public static final Map<Integer, Integer> KEY_TO_NOTE = Map.ofEntries(
            Map.entry(GLFW.GLFW_KEY_Z, 48), //C3
            Map.entry(GLFW.GLFW_KEY_S, 49), //C#3
            Map.entry(GLFW.GLFW_KEY_X, 50), //D3
            Map.entry(GLFW.GLFW_KEY_D, 51), //D#3
            Map.entry(GLFW.GLFW_KEY_C, 52), //E3
            Map.entry(GLFW.GLFW_KEY_V, 53), //F3
            Map.entry(GLFW.GLFW_KEY_G, 54), //F#3
            Map.entry(GLFW.GLFW_KEY_B, 55), //G3
            Map.entry(GLFW.GLFW_KEY_H, 56), //G#3
            Map.entry(GLFW.GLFW_KEY_N, 57), //A3
            Map.entry(GLFW.GLFW_KEY_J, 58), //A#3
            Map.entry(GLFW.GLFW_KEY_M, 59), //B3
            Map.entry(GLFW.GLFW_KEY_COMMA, 60), //C2
            Map.entry(GLFW.GLFW_KEY_L, 61), //C#4
            Map.entry(GLFW.GLFW_KEY_PERIOD, 62), //D4
            Map.entry(GLFW.GLFW_KEY_SEMICOLON, 63), //D#4
            Map.entry(GLFW.GLFW_KEY_SLASH, 64), //E4

            Map.entry(GLFW.GLFW_KEY_Q, 60), //C4
            Map.entry(GLFW.GLFW_KEY_2, 61), //C#4
            Map.entry(GLFW.GLFW_KEY_W, 62), //D4
            Map.entry(GLFW.GLFW_KEY_3, 63), //D#4
            Map.entry(GLFW.GLFW_KEY_E, 64), //E4
            Map.entry(GLFW.GLFW_KEY_R, 65), //F4
            Map.entry(GLFW.GLFW_KEY_5, 66), //F#4
            Map.entry(GLFW.GLFW_KEY_T, 67), //G4
            Map.entry(GLFW.GLFW_KEY_6, 68), //G#4
            Map.entry(GLFW.GLFW_KEY_Y, 69), //A4
            Map.entry(GLFW.GLFW_KEY_7, 70), //A#4
            Map.entry(GLFW.GLFW_KEY_U, 71),  //B4
            Map.entry(GLFW.GLFW_KEY_I, 72), //C5
            Map.entry(GLFW.GLFW_KEY_9, 73), //C#5
            Map.entry(GLFW.GLFW_KEY_O, 74), //D5
            Map.entry(GLFW.GLFW_KEY_0, 75), //D#5
            Map.entry(GLFW.GLFW_KEY_P, 76)  //E5
    );


    public PianoKey(int note, int x, int y, boolean isBlack) {
        this.note = note;
        this.x = x;
        this.y = y;
        this.isBlack = isBlack;
        this.width = isBlack ? 7 : 9;
        this.height = isBlack ? 16 : 24;
    }

    public boolean isInside(int leftPos, int topPos, double mouseX, double mouseY) {
        return mouseX >= (leftPos + x) && mouseX <= (leftPos + x) + width && mouseY >= (topPos + y) && mouseY <= (topPos + y) + height;
    }

    void handleKeyPress(NoteBlockInstrument noteBlockInstrument) {
        double pitch = Math.pow(2, (this.note - 60) / 12.0);
        Minecraft.getInstance().player.playSound(noteBlockInstrument.getSoundEvent().get(),2.0F, ((float) pitch));
        isPressed = true;
    }

    static void initPianoKeys(List<PianoKey> pianoKeys) {
        int baseX = 23;
        int baseY = 125;
        int startingNote = 48; // C3
        int maxNote = 76; // E5

        // Standard white key offsets in a 12-note octave (C, D, E, F, G, A, B)
        int[] whiteNoteOffsets = {0, 2, 4, 5, 7, 9, 11};

        // Mapping: index of white note in octave → black key exists after it
        Map<Integer, Integer> blackNoteOffsets = Map.of(
                0, 1,  // C#
                1, 3,  // D#
                3, 6,  // F#
                4, 8,  // G#
                5, 10  // A#
        );

        int whiteKeyX = 0;

        for (int midiNote = startingNote; midiNote <= maxNote; ) {
            int octaveOffset = (midiNote - startingNote) / 12;

            for (int whiteNoteOffset : whiteNoteOffsets) {
                int note = startingNote + octaveOffset * 12 + whiteNoteOffset;
                if (note > maxNote) break;

                pianoKeys.add(new PianoKey(note, baseX + whiteKeyX, baseY, false));
                whiteKeyX += 8;
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
                        int x = baseX + whiteKeyX + 5;
                        pianoKeys.add(new PianoKey(blackNote, x, baseY, true));
                    }
                }

                whiteKeyX += 8;
                midiNote = whiteNote + 1;
            }
        }
    }
}