package com.ninni.blockstar.client.sound.key;

import java.util.List;
import java.util.Map;

public class PianoKey extends Key {

    public PianoKey(int note, float velocity, int x, int y, boolean isBlack) {
        super(note, velocity, x, y, isBlack, isBlack ? 7 : 9, isBlack ? 16 : 24);
    }

    public static void initPianoKeys(List<PianoKey> pianoKeys) {
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

                pianoKeys.add(new PianoKey(note, 1, baseX + whiteKeyX, baseY, false));
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
                        pianoKeys.add(new PianoKey(blackNote, 1, x, baseY, true));
                    }
                }

                whiteKeyX += 8;
                midiNote = whiteNote + 1;
            }
        }
    }
}